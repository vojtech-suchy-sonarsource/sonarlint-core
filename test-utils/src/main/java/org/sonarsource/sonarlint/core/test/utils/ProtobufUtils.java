/*
ACR-a3ab95ddab874a5eac8332a9d82d7118
ACR-9193ccfb41704b3ea30136a55417a12a
ACR-4eedd47e6c8a4e9db4a71d804766aa28
ACR-d5da99feac7d41dbaf4919577b99f4de
ACR-3119f2a0c8494d0a8ab9cf25f78b4dbf
ACR-d4926a74c12a4ac5864201584ac7a59a
ACR-122872c011b048829a6770ab621a539c
ACR-674c0e74a20f4be488649b1c27baafbd
ACR-3abf5729ef2b43588a37eb8c0a34c389
ACR-2322f69c69c149fdb836ef134fb5884b
ACR-f7a66bdf84534e28b0645ba7e1a400cb
ACR-8f26a392f92a48c0ab3fa57810ce1d69
ACR-bad0778515104f478db54fd5d3cb3763
ACR-e68583e0016345ad8277aef568843c29
ACR-9076ec57cc274dc98ac15b3d379e2311
ACR-25458562792344c18e726f2784698801
ACR-1d36f55cf4b94aad92b4e96972c834bf
 */
package org.sonarsource.sonarlint.core.test.utils;

import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.http.ContentTypeHeader;
import com.google.protobuf.Message;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProtobufUtils {

  private ProtobufUtils() {
    //ACR-b2cc90bba3aa4560bf6aa096c234f547
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
