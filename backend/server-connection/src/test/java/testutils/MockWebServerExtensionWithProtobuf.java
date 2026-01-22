/*
ACR-e2cf7e0e462244e3b435351c56e971de
ACR-82216343834c41a5a95a12db02282bd0
ACR-3cb6a11fc8534701b6867bbae079d327
ACR-06e373697b9e495e851d6e561160d4ad
ACR-2c1ab9ea037644bd8892d1251fc412af
ACR-681f78add61e41c09e701f5aa29ec071
ACR-98f87081201b49968c475ecfbda9ca69
ACR-4a0816ab20e84295a531da7dac9aa45e
ACR-c1d6135ca93f4973be83debf29c7f56f
ACR-60df8dd0e2f14057829aad3ad7eb390b
ACR-fab432449ca64f39821372eaffc67725
ACR-372ca1ae0f09423e831628c97b8773d3
ACR-c7372684caab464ca56e5e7b49123cec
ACR-f5ab03ef312848e886166271d14d3c14
ACR-7db13eea0d894b778d5997b77435c809
ACR-a244fe8daf924e79b0c32f4013a5f725
ACR-df67eb36d9b74989b94965f074786125
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
