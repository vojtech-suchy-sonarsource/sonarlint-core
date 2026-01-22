/*
ACR-14df46c4b41346238e2c0f43644c8bae
ACR-cdf1b60ef28f42fe9f10e27de1d6fbdf
ACR-277a05b7bd8747bcb67f692087e7c9dd
ACR-dbb34ca0eee640a2a842d5c58710ba53
ACR-03a6084371674792ba89bf2d089aa3f9
ACR-6866b385c98e483ebf548a03cccaad02
ACR-c2b3eeb339da44f9b91ab4f747cee05d
ACR-84253fb490fc46889af1691163694f53
ACR-220e449105284a7cb3b9d7fa42ffa7aa
ACR-426d7aed2ced462dbd61128013095b5f
ACR-bc857a33dd1445e99dbc4244731e432d
ACR-24d37423d04f48ada1c327636d6290be
ACR-6d220fb164994564bd30b3d1bbf5c1d9
ACR-f726963cf9a44931b97fc27bc62b7f1c
ACR-dbf1b4adbcf945c889bd894bca9dd219
ACR-5ab727edd2354bf998594f7a3ec01591
ACR-f404f2faa8d44bc7a3e554a232799dc5
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
    //ACR-a03c1ae3130f46b48d9d2e78fa466722
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
