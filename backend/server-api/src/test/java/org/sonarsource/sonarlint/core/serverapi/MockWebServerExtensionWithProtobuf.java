/*
ACR-f967e2ec888644a5b0135060af2bf088
ACR-6bc0d1e6ca5c4bccbc0c4aa842c1f239
ACR-49fd8a34542d4f849415d0072a2c2450
ACR-ed9a3c8c666d4ae8a54213452aae87cc
ACR-c3942f410e6646e590dac1ca40596d54
ACR-47a23616b19a4acb9cedc814cdb100a1
ACR-6ce8325db7cb407098e03d7fec649e9d
ACR-26adbd810fca4edb9a0a0c47c69e352d
ACR-45809ac883424bcdbeb18b5f87e252cc
ACR-12aed1177793468a8088c736f450242c
ACR-6a702c384550440abdc19f2e696e2662
ACR-736260b1fdb1412894516f1d4ffe1359
ACR-052d8b3c7e7c435b8c93b836f38d3cb1
ACR-8728530bc714412482fce27db5503f5c
ACR-45009b17133845b7a6db25b0f4ba50e8
ACR-41eb371064ed4b9984f059109114d03f
ACR-a2b4b98cb5c94ea491ec2cf5a85e0646
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
