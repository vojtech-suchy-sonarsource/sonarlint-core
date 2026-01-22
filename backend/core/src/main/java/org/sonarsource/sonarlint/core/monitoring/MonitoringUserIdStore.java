/*
ACR-94ba8b320f1b40c0812501ddf0608850
ACR-90ccf6eccab543d3bb1de5615d6a2b5f
ACR-d8b0ce6f669e4a1bb464f288acfecbb9
ACR-38c80a6e041f4fbe9f59b95db90b65b3
ACR-eda338e2747f4eceb6a0b387433e8a27
ACR-0af19c7705bd4ab987914b91ca7bb245
ACR-802a87982f1646ac80e137dd642c36ba
ACR-c490767cf8db40c4b1adde0bfcbc323d
ACR-8f3f37a814364e519884f83a465199a2
ACR-43cf62ea75f142c4a8f121d7c22a997c
ACR-2cac30df67da48239a81db3c1582a974
ACR-ff020687d8a6436d8d8f9bd7477d74be
ACR-bfe1c202aac643f599f6a050065f065b
ACR-9dddb84df9374a6f8bea9894bcc4ac1f
ACR-2d8f5667188b4ef9b044f36cc79654d9
ACR-a06e1dd2f6174c7eb4886d64a0b32f79
ACR-62ee85b608d1440fbe9e11319e5d6e80
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
