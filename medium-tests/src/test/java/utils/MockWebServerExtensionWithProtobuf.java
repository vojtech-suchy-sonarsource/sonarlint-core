/*
ACR-71fbf936ffdb4a6fbc5d21b762f3a39b
ACR-cd2128dc54944436819ee110d060f8d9
ACR-e280db40782e4cf6b1679598cc8b1528
ACR-11208f87fada45b49bbceceaab0a1631
ACR-7a79ad43356b4464b657f70e3eddc56b
ACR-bc171bf088644e32acfff51a8503e59c
ACR-68cb36ea16eb4cb3be0baeb0068975a7
ACR-8c7a677061bb4556b5b36d2513e2b744
ACR-0e2675392fb440999475e51278a9ce9f
ACR-8ead776e525b450585f70fa0079b7dcd
ACR-2dd0c725cd2d450da134b53b6727bbca
ACR-14d0ef38549640eb9651fb5790ae672e
ACR-246d4be8481f4733abf49ec93abff80e
ACR-1aacf87cd0d24efd8ee5c83090f8af14
ACR-059ab2a6750c43e692aaf69472c0e6ab
ACR-d79dd31a7b294275b54a12df65f4e1b7
ACR-4988c13355c84e90ba511e81517571ed
 */
package utils;

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
import org.sonarsource.sonarlint.core.serverapi.EndpointParams;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;

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
