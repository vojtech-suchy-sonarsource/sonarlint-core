/*
ACR-56d0cd96213c4438a870ff8ff755eb0b
ACR-5ee33c404b084a2ba2d20ec5346ad486
ACR-a7c8919e2d5e447ebd1fa003a4e65232
ACR-16417e85941d44e5aa13ed9e62195de1
ACR-9fb32276afef40d5a0026be70bab1fbb
ACR-11eefb055c9f47f091af44f928631fb0
ACR-44b9d8d1eef84a1b8e72f655bb480262
ACR-3956c4ff539e47b295fe4bc4b36f69ec
ACR-e7e5db77aaed4a6f9ba59ed564a974fb
ACR-82d0774f131e453e9a4d3e2c492fefa4
ACR-4aa2b9a304f7407f8121aa8ea55a149c
ACR-ab1ec0e78c8e462ea674416fc663cbfc
ACR-3831b43f3c1749899e206ac49012a2b1
ACR-a26c604c6e24482dae73d849f86613f2
ACR-d824bd7f6aac4fbaaa86f9cae74f1921
ACR-2159724740924543a5d144a5577e0cb5
ACR-cefda824e32b4eed9753eebf1bd9a1fb
 */
package testutils;

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
