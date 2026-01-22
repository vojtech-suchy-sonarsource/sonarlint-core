/*
ACR-9544dde748b64961aea4587c7cb99007
ACR-335f84dd06c64d2d8fdbd74bf382c584
ACR-a4b32af4570546af840faf66c549f8cb
ACR-50ba7b42b6804e379db23d20352a9682
ACR-c2058b937df342abb8ed85638bcc460d
ACR-419b0940a5674152827358ed29a3e85d
ACR-c8eb576ba06e4951a4b9ea1880376736
ACR-c1c34aa7fbcd4a2a9e48138cba43077c
ACR-17e1b8e1f4284a69abea00eeaa37b353
ACR-a916c7b18bab4ba2a4d72dbdac1cd9c9
ACR-fd185cd5abeb4a209d983005cbeb06c8
ACR-ed8aa220a87e4820951d526821bb7e5f
ACR-c46933a1bb6e457a9b57b7acb7284b8f
ACR-8d6356f7fe3b43b388fad91d9502d6fd
ACR-6165e1c88ad64298a6aa031683d4904a
ACR-ccf5ec674de64c54b8bf7590f61590ae
ACR-66e17f7289ed486ca0119795ebb5893b
 */
package org.sonarsource.sonarlint.core.monitoring;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.UserPaths;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MonitoringUserIdStoreTests {

  @RegisterExtension
  static final SonarLintLogTester logTester = new SonarLintLogTester();

  private Path userHome;
  private Path userIdFilePath;

  @BeforeEach
  void setUp(@TempDir Path temp) {
    userHome = temp;
    userIdFilePath = userHome.resolve("id");
  }

  private MonitoringUserIdStore createStore() {
    var userPaths = mock(UserPaths.class);
    when(userPaths.getUserHome()).thenReturn(userHome);
    return new MonitoringUserIdStore(userPaths);
  }

  @Test
  void should_create_file_and_uuid_on_first_call() throws IOException {
    var store = createStore();

    assertThat(userIdFilePath).doesNotExist();

    var userId = store.getOrCreate();

    assertThat(userId).isPresent();
    assertThat(userIdFilePath).exists();
    var decodedContent = new String(Base64.getDecoder().decode(Files.readString(userIdFilePath)), StandardCharsets.UTF_8);
    assertThat(decodedContent).isEqualTo(userId.get().toString());
  }

  @Test
  void should_reuse_existing_uuid() throws IOException {
    var existingUuid = UUID.randomUUID();
    writeEncodedUuid(existingUuid);

    var store = createStore();
    var userId = store.getOrCreate();

    assertThat(userId)
      .isPresent()
      .contains(existingUuid);
  }

  @Test
  void should_return_cached_uuid_on_subsequent_calls() {
    var store = createStore();

    var firstCall = store.getOrCreate();
    var secondCall = store.getOrCreate();

    assertThat(firstCall).isPresent();
    assertThat(secondCall).isPresent();
    assertThat(firstCall).contains(secondCall.get());
  }

  @Test
  void should_overwrite_invalid_content_with_new_uuid() throws IOException {
    Files.writeString(userIdFilePath, "not-a-valid-base64-or-uuid");

    var store = createStore();
    var userId = store.getOrCreate();

    assertThat(userId).isPresent();
    var decodedContent = new String(Base64.getDecoder().decode(Files.readString(userIdFilePath)), StandardCharsets.UTF_8);
    assertThat(decodedContent).isEqualTo(userId.get().toString());
  }

  @Test
  void should_overwrite_empty_file_with_new_uuid() throws IOException {
    Files.writeString(userIdFilePath, "");

    var store = createStore();
    var userId = store.getOrCreate();

    assertThat(userId).isPresent();
    var decodedContent = new String(Base64.getDecoder().decode(Files.readString(userIdFilePath)), StandardCharsets.UTF_8);
    assertThat(decodedContent).isEqualTo(userId.get().toString());
  }

  @Test
  void should_trim_whitespace_when_reading_uuid() throws IOException {
    var existingUuid = UUID.randomUUID();
    var encoded = Base64.getEncoder().encodeToString(("  " + existingUuid.toString() + "\n  ").getBytes(StandardCharsets.UTF_8));
    Files.writeString(userIdFilePath, encoded);

    var store = createStore();
    var userId = store.getOrCreate();

    assertThat(userId)
      .isPresent()
      .contains(existingUuid);
  }

  private void writeEncodedUuid(UUID uuid) throws IOException {
    var encoded = Base64.getEncoder().encodeToString(uuid.toString().getBytes(StandardCharsets.UTF_8));
    Files.writeString(userIdFilePath, encoded);
  }

  @Test
  void concurrent_calls_should_return_same_uuid() {
    var store = createStore();

    int numberOfThreads = 10;
    var executorService = Executors.newFixedThreadPool(numberOfThreads);
    CountDownLatch latch = new CountDownLatch(1);
    List<Future<UUID>> futures = new ArrayList<>();

    IntStream.range(0, numberOfThreads).forEach(i -> {
      futures.add(executorService.submit(() -> {
        try {
          latch.await();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
        return store.getOrCreate().orElse(null);
      }));
    });

    latch.countDown();

    var results = futures.stream().map(f -> {
      try {
        return f.get();
      } catch (ExecutionException e) {
        fail(e.getCause());
        return null;
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return null;
      }
    }).toList();

    assertThat(results)
      .hasSize(numberOfThreads)
      .allMatch(uuid -> uuid != null && uuid.equals(results.get(0)));
  }
}
