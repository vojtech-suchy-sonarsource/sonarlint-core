/*
ACR-7adb7916554b4df9af2dab6f0375d199
ACR-889eb82e15c44bbc9770977acc907009
ACR-3a5abbc9534f4fd488f6bd26171baf64
ACR-c9fb9695637a47a2b47898d7605c6aa1
ACR-fbef01a7423e4633868ed9186089727e
ACR-29c586a77e9a4d398a7ab89e03218429
ACR-2ca265bb4c2c4424984916635abc1812
ACR-67c9df66090044b0bb4e6425bcc319fd
ACR-6f785760fd204ce2bc791f1d9293d283
ACR-fab2c2f74ff7493eb031bc8d4199ef18
ACR-58240ed726224591bed47f2c4252d37e
ACR-cdf3894303a745a7b1c4f9499c961bcc
ACR-2f7b73fbab194f089d0ec7804cb433e0
ACR-c6bd10a5e22140eaa3842bc9a0294574
ACR-9f0f06f645ce4c5cbec51d7ca80936cd
ACR-4706b0a2fd544bffb62c76cb2560e71c
ACR-2db64ca8343e471489e3e00240d9038b
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProtobufFileUtil {
  private ProtobufFileUtil() {
    //ACR-79d758895c9441fda637a0b5a64e5063
  }

  public static <T extends Message> T readFile(Path file, Parser<T> parser) {
    try (var input = Files.newInputStream(file)) {
      return parser.parseFrom(input);
    } catch (IOException e) {
      throw new StorageException("Failed to read file: " + file, e);
    }
  }

  public static void writeToFile(Message message, Path toFile) {
    try (var out = Files.newOutputStream(toFile)) {
      message.writeTo(out);
      out.flush();
    } catch (IOException e) {
      throw new StorageException("Unable to write protocol buffer data to file " + toFile, e);
    }
  }
}
