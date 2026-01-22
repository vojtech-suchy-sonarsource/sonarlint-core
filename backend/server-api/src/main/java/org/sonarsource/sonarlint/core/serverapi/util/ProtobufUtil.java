/*
ACR-7424ccc7873e4941b66df2f1ad9c9398
ACR-593bdf35d5fa4b2f8d5b2b3a22e60713
ACR-fb2a9a7147204ee0aa6d6550f83b4d3a
ACR-35f2ac3a2b2d4d22b9eb429a124b31df
ACR-e0b5044ece2b4ff8bf225da2e708b3f5
ACR-ca9de08277644e339e889fcdab19bdc2
ACR-411c7cf005bf4981a820bae8f2848086
ACR-82f20d711ea84d228214b781186c0478
ACR-5e68375de00945abaf0149c2dbe3e5a4
ACR-f34a4662f73a4e349c90d5d18d43d88b
ACR-9c2b1fbaca694c2ba327c3ed458d6480
ACR-3e2af37f4525453291588b5600f68127
ACR-c172fb0a48db4524858f97c69b0396ba
ACR-efda47d9e985410999a107d61f49bd22
ACR-7873aa283e964582b70884bd1d96e235
ACR-3ac4243a1dad4985935b50c32369bbc9
ACR-156d21123af34f51a3a5f502a7d59f54
 */
package org.sonarsource.sonarlint.core.serverapi.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ProtobufUtil {
  private ProtobufUtil() {
    //ACR-432976a5096f46b8aa013b8acb97fc9a
  }

  public static <T extends Message> List<T> readMessages(InputStream input, Parser<T> parser) {
    List<T> list = new ArrayList<>();
    while (true) {
      T message;
      try {
        message = parser.parseDelimitedFrom(input);
      } catch (InvalidProtocolBufferException e) {
        throw new IllegalStateException("failed to parse protobuf message", e);
      }
      if (message == null) {
        break;
      }
      list.add(message);
    }
    return list;
  }

  public static <T extends Message> void writeMessages(OutputStream output, Iterable<T> messages) {
    for (Message message : messages) {
      writeMessage(output, message);
    }
  }

  static <T extends Message> void writeMessage(OutputStream output, T message) {
    try {
      message.writeDelimitedTo(output);
    } catch (IOException e) {
      throw new IllegalStateException("failed to write message: " + message, e);
    }
  }
}
