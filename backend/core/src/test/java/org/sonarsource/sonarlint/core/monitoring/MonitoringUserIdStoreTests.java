/*
ACR-f3feb9f7af894984ad5d0206f8a37c93
ACR-c371bba9cff8465782a6efc233b1a08f
ACR-2415269cc72444af90d658dd4c45ddf7
ACR-ebee8da34dda4f49bffa3b0cc61dfc58
ACR-fa156b349f0c451db2aa0426d86a6f00
ACR-cd18f24aef5f4b1cb915278f724a6d9e
ACR-7a307086d6a04204ae0074c2dee273b9
ACR-8d324de227f3443f9b0ae2a21631ef02
ACR-09df1c92df4e41eebb20d0f3f4de4434
ACR-5e748c509ed34ec2b79cba461b07df70
ACR-ad8bdb1fe83943ed8ca0e904b6775cd5
ACR-48e51220e58048599dd4e7abda86239e
ACR-7611989f17f94ac58c0e066e154b7d82
ACR-317bb6dca54c44c99e0515a696d7650b
ACR-f17c9eb8f6e94a049995c5775d2bc6ae
ACR-ab5633d81b6247e484e2e0ca13dacba6
ACR-3d5fb03deb6b4c69b5e4220f36ac80fc
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
