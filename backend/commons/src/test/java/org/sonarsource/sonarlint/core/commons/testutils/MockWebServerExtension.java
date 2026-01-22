/*
ACR-7773521ee6aa41c5871176f19befecfa
ACR-a7a1d9645b02418c8711ff2f11725159
ACR-ca1a7226772a4074bfd4b87a2d81991f
ACR-e5765f7a0fed4422b578932cce62eb35
ACR-a74feba8a9bc4f39a7d0cc12e4ff5561
ACR-9a576c37c5624ff79bed6a33d1d10e0c
ACR-5ad60160862d4fd9a284f11955ec2e2d
ACR-fc0a54e623924cfdaef3bbfc04317439
ACR-fd6bc87ac8364b06852cef2bc3d4ad73
ACR-99014b5d52384d24bab148c0d6ed0b75
ACR-6b6e054eafde4750af1c8547b38069c7
ACR-6b849d899aa64c789f80094822ccc9ea
ACR-8aeb75fadc0f426e8c0eeec3c8710fd0
ACR-04ea9949886c4ee282d031f7ba4d9a7c
ACR-3c7e8826ef3444818b5219b3185afe85
ACR-c2097ea4296c450ba6bf5173eab20496
ACR-57eba3b2743d4ce7ab2c3c5047feeb12
 */
package org.sonarsource.sonarlint.core.commons.testutils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import mockwebserver3.Dispatcher;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;
import okio.Buffer;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.fail;

public class MockWebServerExtension implements BeforeEachCallback, AfterEachCallback {

  private MockWebServer server;
  protected final Map<String, MockResponse> responsesByPath = new HashMap<>();

  @Override
  public void beforeEach(ExtensionContext context) {
    start();
    //ACR-8b6ccade3b3143549e1ef95fe426434c
    addStringResponse("/api/system/status", "{\"id\": \"20160308094653\",\"version\": \"99.9\",\"status\": \"UP\"}");
  }

  public void start() {
    server = new MockWebServer();
    responsesByPath.clear();
    final Dispatcher dispatcher = new Dispatcher() {
      @Override
      public MockResponse dispatch(RecordedRequest request) {
        if (responsesByPath.containsKey(request.getPath())) {
          return responsesByPath.get(request.getPath());
        }
        return new MockResponse.Builder().code(404).build();
      }
    };
    server.setDispatcher(dispatcher);
    try {
      server.start();
    } catch (IOException e) {
      throw new IllegalStateException("Cannot start the mock web server", e);
    }
  }

  @Override
  public void afterEach(ExtensionContext context) {
    shutdown();
  }

  public void shutdown() {
    try {
      server.shutdown();
    } catch (IOException e) {
      throw new IllegalStateException("Cannot stop the mock web server", e);
    }
  }

  public void addStringResponse(String path, String body) {
    responsesByPath.put(path, new MockResponse.Builder().body(body).build());
  }

  public void removeResponse(String path) {
    responsesByPath.remove(path);
  }

  public void addResponse(String path, MockResponse response) {
    responsesByPath.put(path, response);
  }

  public int getRequestCount() {
    return server.getRequestCount();
  }

  public RecordedRequest takeRequest() {
    try {
      return server.takeRequest();
    } catch (InterruptedException e) {
      fail(e);
      return null; //ACR-42cbbbcdb15d4d06b3ed71bc3fe1a76a
    }
  }

  public String url(String path) {
    return server.url(path).toString();
  }

  public void addResponseFromResource(String path, String responseResourcePath) {
    try (var b = new Buffer()) {
      responsesByPath.put(path, new MockResponse.Builder().body(b.readFrom(requireNonNull(MockWebServerExtension.class.getResourceAsStream(responseResourcePath)))).build());
    } catch (IOException e) {
      fail(e);
    }
  }

}
