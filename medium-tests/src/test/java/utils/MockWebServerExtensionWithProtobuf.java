/*
ACR-941acee09c364faaa6ce9d60ad6cf5b0
ACR-47873a0392c34ddf871878e528e148dd
ACR-aa0ab9e550074846b5dd8598ff1142d5
ACR-37e18f693f31487ca9280bbc4dd85b26
ACR-bf1d55afcc674876b9d576099cbc468d
ACR-e009892d70b34d22b3e3b671c8784dce
ACR-b3a1922f2d0b4bbfb25d5b6be1795988
ACR-addc1a970356474581b1c167d51d6423
ACR-7b1c77d1785d4bb4872bce8c5efc1bb5
ACR-3253fe88c3ff426bb044c93d09e5739e
ACR-5a446b0330f54c5bb008f06b4742e4fe
ACR-3a5dca9361a84e6da8b379ea4d595d1a
ACR-89c8df56832b4024966ab95f87f2b72e
ACR-f5850d5951804641b87af3d8c97cbcce
ACR-156a132330084cc79d108596d712ce37
ACR-4affc1556fc64d82a96cf73b58e3f488
ACR-a2a5af2b5e4444219a1767c5680ce5e3
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
