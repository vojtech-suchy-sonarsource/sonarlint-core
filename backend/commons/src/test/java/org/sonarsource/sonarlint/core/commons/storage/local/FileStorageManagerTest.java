/*
ACR-d623cc5765014fbe87aa65a0c043173c
ACR-41e3c4d7d7d441ff843f233b982f6c57
ACR-d0e67f3938284813bac94e526f2c61c2
ACR-b3bee3f36b0b4473b1e455585231b293
ACR-bf653b621e2e460ba160f3d2d7c12572
ACR-3acd6014952843988eaff77ef512a43c
ACR-df04a6a3cc994aa280d52d772d0308b5
ACR-59d499b32d7342c8a1511762355f1ee8
ACR-e66fd2def6a94af1b1ca4b554fb97599
ACR-d56c55920b994eb89d86234e36479bd7
ACR-b6d48b96894a41c2a45e84857da75b61
ACR-f50fb4b92c2447e88ead600b338a9640
ACR-8a495f6475004665bced746172af4f98
ACR-dbe36569458d4d82a0ff100e3066b63a
ACR-0b6cf11ad6a4410886c928a6b15ecb72
ACR-d5bb690266544431b7346b7f90e607d7
ACR-cfc3da4083794bc99ccf4609c6a6618a
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
    //ACR-f6761b8d7239484cb45cddc46640e37e
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

  /*ACR-f39cb282f4d2490f972fae95f3f64c89
ACR-7a58a1b0aae04b1d896c8473a1ddfb4b
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
