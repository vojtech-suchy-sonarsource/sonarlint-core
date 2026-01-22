/*
ACR-7b1f2974f7ec42f9b58eac04b521082b
ACR-26db6b9e48484fc288b3d90d274618f3
ACR-45d6f6b79b5a49caaac3b81ddfdcefe2
ACR-8cd61d52ccf84264adcc61a0089ba477
ACR-07201e27c93d41f28bdc8c05f8ec1bcb
ACR-1b1455303f8d4b3e982c172b94ecc28d
ACR-e093c893dea04c0aa674d5c5555e6aa4
ACR-e6775be9c7fe46b585402dabbcf4565e
ACR-41b3c9fc2cb94d1fa2c4addfbf134778
ACR-5cfa1c1c5b7c407e825fca422f4ec999
ACR-cf31899ed2304bf9a678f92ee7e53863
ACR-39c673a34a8f48829ab486e0c3cd90fc
ACR-a68e34c365d044c594b64d134f45c9ae
ACR-3fe5272e5e1c421eb72561335293c14c
ACR-a2f90ccdcb7841a89a479f649f940e08
ACR-763bb941919143f0a08bf8cba79301dc
ACR-682ee2e0515c4c019a9cf2a8a5886384
 */
package org.sonarsource.sonarlint.core.commons.storage.local;

import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.commons.storage.adapter.LocalDateAdapter;
import org.sonarsource.sonarlint.core.commons.storage.adapter.LocalDateTimeAdapter;
import org.sonarsource.sonarlint.core.commons.storage.adapter.OffsetDateTimeAdapter;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

class FileStorageManagerTest {

  private Path filePath;

  @BeforeEach
  void setUp(@TempDir Path temp) {
    filePath = temp.resolve("test");
  }

  @Test
  void should_update() {
    var storage = new FileStorageManager<>(filePath, Dummy::new, Dummy.class);

    storage.getStorage();
    assertThat(filePath).doesNotExist();

    storage.tryUpdateAtomically(dummy -> dummy.data = "update");
    assertThat(filePath).exists();

    var dummy = storage.getStorage();

    assertThat(dummy.data).isEqualTo("update");
  }

  @Test
  void supportConcurrentUpdates() {
    var storage = new FileStorageManager<>(filePath, Dummy::new, Dummy.class);
    int nThreads = 10;
    var executorService = Executors.newFixedThreadPool(nThreads);
    CountDownLatch latch = new CountDownLatch(1);
    List<Future<?>> futures = new ArrayList<>();
    //ACR-beef187ab77d465da20b20bd38ca5620
    IntStream.range(0, nThreads).forEach(i -> {
      futures.add(executorService.submit(() -> {
        try {
          latch.await();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        storage.tryUpdateAtomically(data -> data.counter++);
      }));
    });
    latch.countDown();
    futures.forEach(f -> {
      try {
        f.get();
      } catch (ExecutionException e) {
        fail(e.getCause());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });
    assertThat(storage.getStorage().counter).isEqualTo(nThreads);
  }

  @Test
  void tryUpdateAtomically_should_not_crash_if_too_many_read_write_requests() {
    var storageManager = new FileStorageManager<>(filePath, Dummy::new, Dummy.class);

    Runnable read = () -> storageManager.getStorage().getCounter();
    Runnable write = () -> storageManager.tryUpdateAtomically(dummy -> dummy.counter++);
    Stream.of(
        IntStream.range(0, 100).mapToObj(operand -> CompletableFuture.runAsync(write)),
        IntStream.range(0, 100).mapToObj(value -> CompletableFuture.runAsync(read)),
        IntStream.range(0, 100).mapToObj(operand -> CompletableFuture.runAsync(write)),
        IntStream.range(0, 100).mapToObj(value -> CompletableFuture.runAsync(read))
      ).flatMap(Function.identity())
      .forEach(CompletableFuture::join);

    assertThat(storageManager.getStorage().counter).isEqualTo(200);
  }

  @Test
  void tryRead_should_be_aware_of_file_deletion() {
    var storageManager = new FileStorageManager<>(filePath, Dummy::new, Dummy.class);

    assertThat(storageManager.getStorage().counter).isZero();

    storageManager.tryUpdateAtomically(dummy -> dummy.counter++);
    assertThat(storageManager.getStorage().counter).isEqualTo(1);

    filePath.toFile().delete();

    assertThat(storageManager.getStorage().counter).isZero();
  }

  /*ACR-b411952632bf4942a79319488dbe60a1
ACR-1e614149c4054ee7ab7ee761468d4871
   */
  @Test
  @DisabledOnOs(OS.WINDOWS)
  void tryRead_should_be_aware_of_file_modification() throws IOException {
    var storageManager = new FileStorageManager<>(filePath, Dummy::new, Dummy.class);

    assertThat(storageManager.getStorage().counter).isZero();

    storageManager.tryUpdateAtomically(dummy -> dummy.counter++);
    assertThat(storageManager.getStorage().counter).isEqualTo(1);

    var dummy = new Dummy();
    dummy.counter = 2;
    writeToLocalStorageFile(dummy);

    await().atMost(5, SECONDS).untilAsserted(() -> assertThat(storageManager.getStorage().counter).isEqualTo(2));
  }

  private void writeToLocalStorageFile(Object newStorage) throws IOException {
    var newJson = new GsonBuilder()
      .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter().nullSafe())
      .registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe())
      .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter().nullSafe())
      .create().toJson(newStorage);
    var encoded = Base64.getEncoder().encode(newJson.getBytes(StandardCharsets.UTF_8));
    writeToLocalStorageFile(encoded);
  }

  private void writeToLocalStorageFile(byte[] encoded) throws IOException {
    FileUtils.writeByteArrayToFile(filePath.toFile(), encoded);
  }

  @Test
  void tryRead_returns_default_local_storage_if_file_is_empty() throws IOException {
    writeToLocalStorageFile(new byte[0]);
    assertThat(filePath.toFile()).isEmpty();

    var storageManager = new FileStorageManager<>(filePath, Dummy::new, Dummy.class);
    assertThat(storageManager.getStorage().data).isEqualTo("default");
    assertThat(storageManager.getStorage().counter).isZero();
  }

  private static class Dummy implements LocalStorage {

    private String data;
    private int counter = 0;

    Dummy() {
      this("default");
    }

    Dummy(String data) {
      this.data = data;
    }

    public int getCounter() {
      return counter;
    }
  }
}
