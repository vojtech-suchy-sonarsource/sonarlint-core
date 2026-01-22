/*
ACR-deee535c20924a5e81debdba31fc397f
ACR-bef1fcba28f54132877307e2502f67f4
ACR-0163c6075a214a63ac20ecb22d8191a1
ACR-18fd9c34a912449e9a1017caf611a76b
ACR-b0b94095a0774ef78b67e4758941eb14
ACR-74732a7ac76e406d92d2087b21da5382
ACR-23a0e65f276f4603a5ab597723adedf6
ACR-f1de57cbb2ca45dab278e24db5055c3b
ACR-080866afd25340c08e3f96c667998be4
ACR-ac170a61e5ae4382be5f1470017f97b8
ACR-fa0e8f6298374e52b9d1a742d6e7e86c
ACR-509ed649ee644d56a8e00277d64e0688
ACR-0744a6ee3f0f4002a3c6cf9f202a3cd2
ACR-0eef6879057e44d0a78b1f05b97f3ce0
ACR-9a0518ed2b794272bedfe6a3def45cdc
ACR-968a028681d843cda271fbd9321e8139
ACR-e247cac055d34ed1b1da3f2fe8efbe18
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
    //ACR-15351147574d4ca9a6638f131f3ae808
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
      return null; //ACR-3a80aa95b4d24331962a775c129ef075
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
