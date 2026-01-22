/*
ACR-7b6ca8b974d6412cb820ea57b9fadd1c
ACR-8bdd54dbc9b64cab892bc965ed674450
ACR-b307088fa22346d4bf12c70c03e517f9
ACR-958ce8e9ce3e4c90ad25da0cfde33f0c
ACR-f5b116cd20e44211809a0bba647aa052
ACR-fb04cd899a294b689bf399d7762df228
ACR-2f1e4e7955d04128946c43b271b086fe
ACR-8cfdf8cd5d9c498c8baae7506094d74c
ACR-1561001f24c94b05afb12950f1700960
ACR-1ca6d93484504fb3a406903ca2123943
ACR-d876748e48e847668f5da18b1adaa0e8
ACR-d1e6894d79f94d2dac384bc8c200df77
ACR-0f00200960fe4a45925a76ec3ce820c2
ACR-a96d0614090043eeb2a7bcd0e20bbbbd
ACR-8f8d46319c6e47f18da26298ac048a2b
ACR-ac3fbb8be7754bc08c24eef1b6a746cc
ACR-e98754631d4c4b4a827adbe22bc5da9e
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProtobufFileUtil {
  private ProtobufFileUtil() {
    //ACR-71d00d97834e410ba767708ac4eec55c
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
