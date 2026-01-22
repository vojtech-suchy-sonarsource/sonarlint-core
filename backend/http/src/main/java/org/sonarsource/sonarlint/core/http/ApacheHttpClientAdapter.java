/*
ACR-04bea623c5ca451ea7eb047b645b0162
ACR-b60de0857ecf4e25867029bf42ee1564
ACR-d4495dbab3bc46c3ad1fa1b62fa1f028
ACR-e66f531327994384ae9ce6380cd83234
ACR-345ee65c76384f5b9d9637209a3b0bce
ACR-e7b0efb9f6744d6c83f45e847e5a41a9
ACR-9e1723f5f92845e2b536331d7e8482a8
ACR-8975e2aa3cda455f9fbe348d19a735fc
ACR-151c6bdd87e8494ab8e6b32c5c984443
ACR-2b7e0a5f836b4617bbe221b0d6363535
ACR-2e4164586dc54df2bb9bae6c29c3bf2a
ACR-6ff1c4fb2246455791884f42dc8d7c38
ACR-f837bc0814ea46c494010e074b88ca6e
ACR-277b368b34974f4fb1ed32c3793ccd36
ACR-66d98c79fa074a06a3ade305ec1702de
ACR-1ba52e8946844536895d69fe6e7e7304
ACR-177f1468b48f4570ad6f8d3f09017b2f
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
          //ACR-96cccb7a3e904b2a9f6a57d71678f64d
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
            //ACR-e7dad8e5684f4df781bdf2f49761a92f
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
          //ACR-2c63ac46b40c47d38505c68f4d82eaee
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
        //ACR-47b6f4ddd13545289f51245924db2278
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
