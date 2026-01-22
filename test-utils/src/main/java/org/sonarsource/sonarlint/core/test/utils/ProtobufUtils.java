/*
ACR-6b258db9d05a44c0ac3a6747388d1e6e
ACR-95c5d9a72c314ff9bbd3849bc90e59e0
ACR-a7da41fb65c44370b9e4d712a997d14d
ACR-ba0b8e6822994300bd32f2a15b5eb2fd
ACR-29dbef2d3c2746f7b47d8236a3b630f7
ACR-e848954c9a1548e79e05f8dc33a3ce26
ACR-5af350e938da4bdaa011ea0d0ff87b42
ACR-3f5916344d4540af82b85fc3d7ad0381
ACR-affaa1fa44e34f11a494b016b4d3345e
ACR-f89c6d2aad2048369f854380c1dede3b
ACR-52feb1854d8d464dad9ece00aa74ece9
ACR-459c179ce8b24fa1b9e355ae6d89d29e
ACR-3b75439aafa34d359a8795b1aa79d2fc
ACR-65242c8843844a55a4358006ed606dc8
ACR-40afef4cca5746eeb94a1accac8594ed
ACR-aa4a6775d135487b9681a8de9000372f
ACR-676d8de323414965aba6f02ce50cac6a
 */
package org.sonarsource.sonarlint.core.test.utils;

import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.http.ContentTypeHeader;
import com.google.protobuf.Message;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProtobufUtils {

  private ProtobufUtils() {
    //ACR-50ff763e2fa34125b27d980b40a13d1f
  }

  public static Body protobufBody(Message message) {
    var baos = new ByteArrayOutputStream();
    try {
      message.writeTo(baos);
      return Body.ofBinaryOrText(baos.toByteArray(), ContentTypeHeader.absent());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static Body protobufBodyDelimited(Message... messages) {
    var baos = new ByteArrayOutputStream();
    try {
      for (var message : messages) {
        message.writeDelimitedTo(baos);
      }
      return Body.ofBinaryOrText(baos.toByteArray(), ContentTypeHeader.absent());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
