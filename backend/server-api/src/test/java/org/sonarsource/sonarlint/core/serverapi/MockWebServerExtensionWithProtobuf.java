/*
ACR-8dcf6f16c3b741d1be7d6e5328e7997e
ACR-83e46b6763d04a3ab6958218462a8d6b
ACR-c4e4d3507e4146b49b843d5b79aadd3b
ACR-71a3731323cb4060a9f0930042e7e172
ACR-dddc49b7ebdf4b94b3e96c3b4e8efe35
ACR-becce7b00a434e6a9abdf2c20980d15a
ACR-1db72c70df3a4d5ab036781a6d858b5b
ACR-a5e2c30a29bd44f2aa66c4675dc5719b
ACR-ff6eaf487640486ab03d77035cbd79c6
ACR-c19b9b6c95d74292964ad668cd39ebee
ACR-5a1c92888c244313a8748c5e60199356
ACR-abf6050c727e418c965c4cad2dcb9655
ACR-8228b0bc97064bfd9e0207476fa987aa
ACR-6967023976aa469aa9bc30c672c068a3
ACR-20872dfbaa22428d9d08b1ae486b8eb0
ACR-09e3fd04132d4f58a03a137c93306b53
ACR-3410730995114939ad0fa6f23fb8cf95
 */
package org.sonarsource.sonarlint.core.serverapi;

import com.google.protobuf.Message;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;
import mockwebserver3.MockResponse;
import okio.Buffer;
import org.sonarsource.sonarlint.core.commons.testutils.MockWebServerExtension;
import org.sonarsource.sonarlint.core.http.HttpClientProvider;

import static org.junit.jupiter.api.Assertions.fail;

public class MockWebServerExtensionWithProtobuf extends MockWebServerExtension {

  public void addProtobufResponse(String path, Message m) {
    try (var b = new Buffer()) {
      m.writeTo(b.outputStream());
      responsesByPath.put(path, new MockResponse.Builder().body(b).build());
    } catch (IOException e) {
      fail(e);
    }
  }

  public void addProtobufResponseDelimited(String path, Message... m) {
    try (var b = new Buffer()) {
      writeMessages(b.outputStream(), Arrays.asList(m).iterator());
      responsesByPath.put(path, new MockResponse.Builder().body(b).build());
    }
  }

  public static <T extends Message> void writeMessages(OutputStream output, Iterator<T> messages) {
    while (messages.hasNext()) {
      writeMessage(output, messages.next());
    }
  }

  public static <T extends Message> void writeMessage(OutputStream output, T message) {
    try {
      message.writeDelimitedTo(output);
    } catch (IOException e) {
      throw new IllegalStateException("failed to write message: " + message, e);
    }
  }

  public ServerApiHelper serverApiHelper() {
    return serverApiHelper(null);
  }

  public ServerApiHelper serverApiHelper(@Nullable String organizationKey) {
    return new ServerApiHelper(endpointParams(organizationKey), HttpClientProvider.forTesting().getHttpClient());
  }

  public EndpointParams endpointParams() {
    return endpointParams(null);
  }

  public EndpointParams endpointParams(@Nullable String organizationKey) {
    return new EndpointParams(url("/"), url("/"), organizationKey != null, organizationKey);
  }


}
