/*
ACR-57db052bf61f4a22a8f4da6c0e40d75e
ACR-881007e71dcc43858fae6506cc938196
ACR-f82e2b9b9e9b4017873a31b50f0d8e77
ACR-cd92c349e63840dab92e66f38090f0ed
ACR-4a473c07fe6d45e9b97153bc88d77eda
ACR-f50b15a35d614d678a89e02fc16c04db
ACR-8c14f6173fce45fb9a877fffac85750e
ACR-cf102dbf3c364bb3998907df1fdfe319
ACR-cbb6646d3a8b4c1894a286cf6a858cc4
ACR-a3066a20086f4678ae15997d712ff8f1
ACR-d6d821995c5f40759d2e905f2cc81ebe
ACR-f37d2450557540118f6a62026d15bb08
ACR-55cf1e6117a648f5b06ed1cea577ef8a
ACR-c1b802d5fb2d4d57838c466077e0641f
ACR-f554d3b29f3a468ab96ae6f85a25314b
ACR-5acdc461e5b04349a35ec5451ac677c1
ACR-1205a1cf64c245a491c218c992fcb621
 */
package org.sonarsource.sonarlint.core.http;

import java.io.InterruptedIOException;
import java.net.URISyntaxException;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.apache.hc.client5.http.async.methods.AbstractCharResponseConsumer;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.nio.support.BasicRequestProducer;
import org.apache.hc.core5.util.Timeout;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

class ApacheHttpClientAdapter implements HttpClient {

  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String X_API_KEY_HEADER = "x-api-key";
  private static final Timeout STREAM_CONNECTION_REQUEST_TIMEOUT = Timeout.ofSeconds(10);
  private static final Timeout STREAM_CONNECTION_TIMEOUT = Timeout.ofMinutes(1);

  private final CloseableHttpAsyncClient apacheClient;
  @Nullable
  private final String usernameOrToken;
  @Nullable
  private final String password;
  private final boolean shouldUseBearer;
  @Nullable
  private final String xApiKey;
  private final boolean withRetries;
  private boolean connected = false;

  private ApacheHttpClientAdapter(CloseableHttpAsyncClient apacheClient, @Nullable String usernameOrToken, @Nullable String password, boolean shouldUseBearer,
    @Nullable String xApiKey, boolean withRetries) {
    this.apacheClient = apacheClient;
    this.usernameOrToken = usernameOrToken;
    this.password = password;
    this.shouldUseBearer = shouldUseBearer;
    this.xApiKey = xApiKey;
    this.withRetries = withRetries;
  }

  @Override
  public Response post(String url, String contentType, String bodyContent) {
    return waitFor(postAsync(url, contentType, bodyContent));
  }

  @Override
  public CompletableFuture<Response> postAsync(String url, String contentType, String body) {
    var request = SimpleRequestBuilder.post(url)
      .setBody(body, ContentType.parse(contentType))
      .build();
    return executeAsync(request);
  }

  @Override
  public Response get(String url) {
    return waitFor(getAsync(url));
  }

  @Override
  public CompletableFuture<Response> getAsync(String url) {
    return executeAsync(SimpleRequestBuilder.get(url).build());
  }

  @Override
  public CompletableFuture<Response> getAsyncAnonymous(String url) {
    return executeAsyncAnonymous(SimpleRequestBuilder.get(url).build());
  }

  @Override
  public CompletableFuture<Response> deleteAsync(String url, String contentType, String body) {
    var httpRequest = SimpleRequestBuilder
      .delete(url)
      .setBody(body, ContentType.parse(contentType))
      .build();
    return executeAsync(httpRequest);
  }

  private static Response waitFor(CompletableFuture<Response> f) {
    return f.join();
  }

  @Override
  public AsyncRequest getEventStream(String url, HttpConnectionListener connectionListener, Consumer<String> messageConsumer) {
    var request = SimpleRequestBuilder.get(url).build();
    request.setConfig(RequestConfig.custom()
      .setConnectionRequestTimeout(STREAM_CONNECTION_REQUEST_TIMEOUT)
      .setConnectTimeout(STREAM_CONNECTION_TIMEOUT)
      .setResponseTimeout(Timeout.ZERO_MILLISECONDS)
      .build());

    setAuthHeader(request);
    request.setHeader("Accept", "text/event-stream");
    connected = false;
    var cancelled = new AtomicBoolean();
    var httpFuture = apacheClient.execute(new BasicRequestProducer(request, null),
      new AbstractCharResponseConsumer<>() {
        @Override
        public void releaseResources() {
          //ACR-d8e3de0a5f164d37b8c003e02d9f5fb1
        }

        @Override
        protected int capacityIncrement() {
          return Integer.MAX_VALUE;
        }

        @Override
        protected void data(CharBuffer src, boolean endOfStream) {
          if (cancelled.get()) {
            throw new CancellationException();
          }
          if (connected) {
            messageConsumer.accept(src.toString());
          } else {
            var possiblyErrorMessage = src.toString();
            if (!possiblyErrorMessage.isEmpty()) {
              LOG.debug("Received event-stream data while not connected: " + possiblyErrorMessage);
            }
          }
        }

        @Override
        protected void start(HttpResponse httpResponse, ContentType contentType) {
          if (httpResponse.getCode() < 200 || httpResponse.getCode() >= 300) {
            connectionListener.onError(httpResponse.getCode());
          } else {
            connected = true;
            connectionListener.onConnected();
          }
        }

        @Override
        protected Object buildResult() {
          return null;
        }

        @Override
        public void failed(Exception cause) {
          if (cause instanceof CancellationException || cause instanceof InterruptedIOException) {
            return;
          }
          LOG.error("Stream failed", cause);
        }
      }, new FutureCallback<>() {

        @Override
        public void completed(Object result) {
          if (connected) {
            connectionListener.onClosed();
          }
        }

        @Override
        public void failed(Exception ex) {
          if (connected) {
            //ACR-c94d40cfbe8d44448386cf190bb758d7
            connectionListener.onClosed();
          } else {
            connectionListener.onError(null);
          }
        }

        @Override
        public void cancelled() {
          cancelled.set(true);
          LOG.debug("Stream has been cancelled");
        }
      });

    return new HttpAsyncRequest(httpFuture);
  }

  private void setAuthHeader(SimpleHttpRequest request) {
    if (usernameOrToken != null) {
      if (shouldUseBearer) {
        request.setHeader(AUTHORIZATION_HEADER, bearer(usernameOrToken));
      } else {
        request.setHeader(AUTHORIZATION_HEADER, basic(usernameOrToken, Objects.requireNonNullElse(password, "")));
      }
    } else if (xApiKey != null) {
      request.setHeader(X_API_KEY_HEADER, xApiKey);
    }
  }

  private class CompletableFutureWrappingFuture extends CompletableFuture<HttpClient.Response> {

    private final Future<SimpleHttpResponse> wrapped;

    private CompletableFutureWrappingFuture(SimpleHttpRequest httpRequest) {
      var callingThreadLogOutput = SonarLintLogger.get().getTargetForCopy();
      var context = new HttpClientContext();
      context.setAttribute(ContextAttributes.RETRIES_ENABLED, withRetries);
      this.wrapped = apacheClient.execute(httpRequest, context, new FutureCallback<>() {
        @Override
        public void completed(SimpleHttpResponse result) {
          SonarLintLogger.get().setTarget(callingThreadLogOutput);
          //ACR-9d8f25006553411b855a8df3a0689e17
          try {
            var uri = httpRequest.getUri().toString();
            CompletableFutureWrappingFuture.this.completeAsync(() -> {
              SonarLintLogger.get().setTarget(callingThreadLogOutput);
              return new ApacheHttpResponse(uri, result);
            });
          } catch (URISyntaxException e) {
            CompletableFutureWrappingFuture.this.completeAsync(() -> {
              SonarLintLogger.get().setTarget(callingThreadLogOutput);
              return new ApacheHttpResponse(httpRequest.getRequestUri(), result);
            });
          }
        }

        @Override
        public void failed(Exception ex) {
          SonarLintLogger.get().setTarget(callingThreadLogOutput);
          LOG.debug("Request failed", ex);
          CompletableFutureWrappingFuture.this.completeExceptionally(ex);
        }

        @Override
        public void cancelled() {
          SonarLintLogger.get().setTarget(callingThreadLogOutput);
          LOG.debug("Request cancelled");
          CompletableFutureWrappingFuture.this.cancel();
        }
      });
    }

    private void cancel() {
      super.cancel(true);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
      return wrapped.cancel(mayInterruptIfRunning);
    }
  }

  private CompletableFuture<Response> executeAsync(SimpleHttpRequest httpRequest) {
    try {
      setAuthHeader(httpRequest);
      return new CompletableFutureWrappingFuture(httpRequest);
    } catch (Exception e) {
      throw new IllegalStateException("Unable to execute request: " + e.getMessage(), e);
    }
  }

  private CompletableFuture<Response> executeAsyncAnonymous(SimpleHttpRequest httpRequest) {
    try {
      return new CompletableFutureWrappingFuture(httpRequest);
    } catch (Exception e) {
      throw new IllegalStateException("Unable to execute request: " + e.getMessage(), e);
    }
  }

  private static String basic(String username, String password) {
    var usernameAndPassword = String.format("%s:%s", username, password);
    var encoded = Base64.getEncoder().encodeToString(usernameAndPassword.getBytes(StandardCharsets.UTF_8));
    return String.format("Basic %s", encoded);
  }

  private static String bearer(String token) {
    return String.format("Bearer %s", token);
  }

  public static class HttpAsyncRequest implements AsyncRequest {
    private final Future<?> response;

    private HttpAsyncRequest(Future<?> response) {
      this.response = response;
    }

    @Override
    public void cancel() {
      try {
        response.cancel(true);
      } catch (Exception e) {
        //ACR-34ec4f3a912b4984b5c426624387d4e7
      }
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private CloseableHttpAsyncClient apacheClient;
    @Nullable
    private String usernameOrToken;
    @Nullable
    private String password;
    private boolean shouldUseBearer = false;
    @Nullable
    private String xApiKey;
    private boolean withRetries = false;

    public Builder withInnerClient(CloseableHttpAsyncClient apacheClient) {
      this.apacheClient = apacheClient;
      return this;
    }

    public Builder withUserNamePassword(String username, @Nullable String password) {
      this.usernameOrToken = username;
      this.password = password;
      return this;
    }

    public Builder withToken(String token) {
      this.usernameOrToken = token;
      return this;
    }

    public Builder useBearer(boolean shouldUseBearer) {
      this.shouldUseBearer = shouldUseBearer;
      return this;
    }

    public Builder withXApiKey(String xApiKey) {
      this.xApiKey = xApiKey;
      return this;
    }

    public Builder withRetries() {
      this.withRetries = true;
      return this;
    }

    ApacheHttpClientAdapter build() {
      if (apacheClient == null) {
        throw new IllegalStateException("Required an Apache HTTP client to wrap.");
      }

      return new ApacheHttpClientAdapter(apacheClient, usernameOrToken, password, shouldUseBearer, xApiKey, withRetries);
    }
  }
}
