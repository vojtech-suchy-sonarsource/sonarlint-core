/*
ACR-575b00ceff234544bf8cf6b08d334c7a
ACR-0d91efc5aa93443194f3207c12f01e2c
ACR-52a41feae6a24b8d976eb5a3bf79c907
ACR-55acc4728bcc445b91dafc10007ca7d1
ACR-45ba6188e546413f919021329d367fbb
ACR-5226468777964362894bea42d3b7a15d
ACR-8236a4f52fc745b6a3f7a9d1885b7398
ACR-f0554ec521a749fe8c78b5038e96fdea
ACR-8f9a77e2400b4a9390629916ad7c75aa
ACR-e7c6cff8ab054ed4a03d45992de83bcb
ACR-f38e3c0eeaf14ce285b278e5896e386e
ACR-6de1aefe75fd487faf30523d0f045152
ACR-e11cb97888d94da0a67866729a85fec8
ACR-8b1e3d9e6c674bc8b638d6ad56a5295a
ACR-5113bc7c00114e73810d5f6433b9c4c2
ACR-5fdaab82f71544998ba764a5b0d53657
ACR-85bb3ca147434c949b749397a10689fb
 */
package org.sonarsource.sonarlint.core.monitoring;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.UserPaths;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

public class MonitoringUserIdStore {

  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private static final String USER_ID_FILE_NAME = "id";

  private final Path path;
  @Nullable
  private volatile UUID cachedUserId;

  public MonitoringUserIdStore(UserPaths userPaths) {
    this.path = userPaths.getUserHome().resolve(USER_ID_FILE_NAME);
  }

  public synchronized Optional<UUID> getOrCreate() {
    var cached = cachedUserId;
    if (cached != null) {
      return Optional.of(cached);
    }
    try {
      Files.createDirectories(path.getParent());
      try (var fileChannel = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.SYNC);
        var ignored = fileChannel.lock()) {
        var userId = readOrCreateUserId(fileChannel);
        cachedUserId = userId;
        return Optional.of(userId);
      }
    } catch (Exception e) {
      LOG.debug("Failed to read or create user ID", e);
      return Optional.empty();
    }
  }

  private static UUID readOrCreateUserId(FileChannel fileChannel) throws IOException {
    var existingId = readUserId(fileChannel);
    if (existingId != null) {
      return existingId;
    }
    var newId = UUID.randomUUID();
    writeUserId(fileChannel, newId);
    return newId;
  }

  @Nullable
  private static UUID readUserId(FileChannel fileChannel) throws IOException {
    if (fileChannel.size() == 0) {
      return null;
    }
    var buffer = ByteBuffer.allocate((int) fileChannel.size());
    fileChannel.read(buffer);
    try {
      var decoded = Base64.getDecoder().decode(buffer.array());
      var content = new String(decoded, StandardCharsets.UTF_8).trim();
      if (content.isEmpty()) {
        return null;
      }
      return UUID.fromString(content);
    } catch (IllegalArgumentException e) {
      LOG.debug("Invalid encoded UUID in " + USER_ID_FILE_NAME, e);
      return null;
    }
  }

  private static void writeUserId(FileChannel fileChannel, UUID userId) throws IOException {
    fileChannel.truncate(0);
    fileChannel.position(0);
    var encoded = Base64.getEncoder().encode(userId.toString().getBytes(StandardCharsets.UTF_8));
    fileChannel.write(ByteBuffer.wrap(encoded));
  }
}
