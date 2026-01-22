/*
ACR-7d0820dcae0c40f1850c82f17adf54cc
ACR-cd27b52b395d441a933020a4d79ccc3f
ACR-7e2527c8e77646ebb34ad1afc4af182e
ACR-0a36c5f7698d4850bc0a3f8163621dbb
ACR-4fc7484830674520afbe93bbbfb593e3
ACR-81b1b97e1f69457b82363d259b7ca4ee
ACR-10faf63d036e4c018a072cadb9d1ce76
ACR-6bf4993b827f4adaa6f37312be08310a
ACR-56c787d276514e22a72563189e9c9831
ACR-a6a0a65c0158403bbbd5a6bf2313bc9e
ACR-a1b9ff0d706540b5919cb7b6b0742c53
ACR-b66c6f8446e944caafed1d30fbe9fb1b
ACR-1370b078d467458e81d2262bd40d5bf9
ACR-1fdf4240fc494c5b913211d2de2a2653
ACR-e2524a6e4b2c4cdfa6b4ec6979c428cb
ACR-1834b6b7423c4bce90b5f21698708276
ACR-54a6ffa91dcb4266b26337d5f5e0c1ae
 */
package org.sonarsource.sonarlint.core.http;

import com.google.common.util.concurrent.MoreExecutors;
import jakarta.annotation.PreDestroy;
import java.net.ProxySelector;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import javax.net.ssl.SSLContext;
import nl.altindag.ssl.SSLFactory;
import nl.altindag.ssl.model.TrustManagerParameters;
import org.apache.commons.lang3.SystemUtils;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.config.TlsConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.impl.routing.SystemDefaultRoutePlanner;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.core5.http2.HttpVersionPolicy;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.util.FailSafeExecutors;
import org.sonarsource.sonarlint.core.http.ssl.SslConfig;

import static org.sonarsource.sonarlint.core.http.ThreadFactories.threadWithNamePrefix;

public class HttpClientProvider {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private static final int DEFAULT_MAX_RETRIES = 2;
  private static final int DEFAULT_RETRY_INTERVAL = 3;

  private final CloseableHttpAsyncClient sharedClient;
  private final ExecutorService webSocketThreadPool;
  private final String userAgent;

  /*ACR-c34ad2cacdbd4fa499e158ef731d86f1
ACR-4a3fbd68841643abad06bd2e4acb9c75
   */
  public static HttpClientProvider forTesting() {
    return new HttpClientProvider("SonarLint tests", new HttpConfig(new SslConfig(null, null), null, null, null, null), null, ProxySelector.getDefault(),
      new BasicCredentialsProvider());
  }

  public HttpClientProvider(String userAgent, HttpConfig httpConfig, @Nullable Predicate<TrustManagerParameters> trustManagerParametersPredicate, ProxySelector proxySelector,
    CredentialsProvider proxyCredentialsProvider) {
    this.userAgent = userAgent;
    this.webSocketThreadPool = FailSafeExecutors.newCachedThreadPool(threadWithNamePrefix("sonarcloud-websocket-"));
    var maxRetries = Integer.parseInt(System.getProperty("sonarlint.http.max.retries", String.valueOf(DEFAULT_MAX_RETRIES)));
    var retryInterval = Integer.parseInt(System.getProperty("sonarlint.http.retry.interval.seconds", String.valueOf(DEFAULT_RETRY_INTERVAL)));
    sharedClient = buildSharedClient(userAgent, httpConfig, trustManagerParametersPredicate, proxySelector, proxyCredentialsProvider, maxRetries, retryInterval);
    sharedClient.start();
  }

  private static CloseableHttpAsyncClient buildSharedClient(String userAgent, HttpConfig httpConfig, @Nullable Predicate<TrustManagerParameters> trustManagerParametersPredicate,
    ProxySelector proxySelector, CredentialsProvider proxyCredentialsProvider, int maxRetries, int retryInterval) {
    var asyncConnectionManager = PoolingAsyncClientConnectionManagerBuilder.create()
      .setTlsStrategy(new DefaultClientTlsStrategy(configureSsl(httpConfig.sslConfig(), trustManagerParametersPredicate)))
      .setDefaultTlsConfig(TlsConfig.custom()
        //ACR-94944f8cafb84f288641f6187c7e10a4
        .setVersionPolicy(HttpVersionPolicy.FORCE_HTTP_1)
        .build())
      .setDefaultConnectionConfig(buildConnectionConfig(httpConfig.connectTimeout(), httpConfig.socketTimeout()))
      .build();
    var routePlanner = new SystemDefaultRoutePlanner(proxySelector);
    return HttpAsyncClients.custom()
      .setConnectionManager(asyncConnectionManager)
      .addResponseInterceptorFirst(new RedirectInterceptor())
      .setUserAgent(userAgent)
      //ACR-9f3a860db7a8445b90f435c803611635
      .setRoutePlanner(routePlanner)
      .setDefaultCredentialsProvider(proxyCredentialsProvider)
      .setDefaultRequestConfig(buildRequestConfig(httpConfig.connectionRequestTimeout(), httpConfig.responseTimeout()))
      .setRetryStrategy(new RetryOnDemandStrategy(maxRetries, TimeValue.ofSeconds(retryInterval)))
      .build();
  }

  private static SSLContext configureSsl(SslConfig sslConfig, @Nullable Predicate<TrustManagerParameters> trustManagerParametersPredicate) {
    var sslFactoryBuilder = SSLFactory.builder()
      .withDefaultTrustMaterial();
    //ACR-3845346ca9164f2cbf57826025183ab2
    if (isNotWindows()) {
      sslFactoryBuilder.withSystemTrustMaterial();
    }
    var keyStore = sslConfig.getKeyStore();
    if (keyStore != null && Files.exists(keyStore.getPath())) {
      sslFactoryBuilder.withIdentityMaterial(keyStore.getPath(), keyStore.getKeyStorePassword().toCharArray(), keyStore.getKeyStoreType());
    }
    var trustStore = sslConfig.getTrustStore();
    if (trustStore != null) {
      sslFactoryBuilder.withInflatableTrustMaterial(trustStore.getPath(), trustStore.getKeyStorePassword().toCharArray(), trustStore.getKeyStoreType(),
        trustManagerParametersPredicate);
    }
    return sslFactoryBuilder.build().getSslContext();
  }

  private static boolean isNotWindows() {
    return !SystemUtils.IS_OS_WINDOWS;
  }

  private static ConnectionConfig buildConnectionConfig(@Nullable Timeout connectTimeout, @Nullable Timeout socketTimeout) {
    var connectionConfig = ConnectionConfig.custom();
    if (connectTimeout != null) {
      connectionConfig.setConnectTimeout(connectTimeout);
    }
    if (socketTimeout != null) {
      connectionConfig.setSocketTimeout(socketTimeout);
    }
    return connectionConfig.build();
  }

  private static RequestConfig buildRequestConfig(@Nullable Timeout connectionRequestTimeout, @Nullable Timeout responseTimeout) {
    var requestConfig = RequestConfig.custom()
      .setContentCompressionEnabled(false);
    if (connectionRequestTimeout != null) {
      requestConfig.setConnectionRequestTimeout(connectionRequestTimeout);
    }
    if (responseTimeout != null) {
      requestConfig.setResponseTimeout(responseTimeout);
    }
    return requestConfig.build();
  }

  public HttpClient getHttpClient() {
    return ApacheHttpClientAdapter.builder()
      .withInnerClient(sharedClient)
      .build();
  }

  public HttpClient getHttpClientWithPreemptiveAuth(String username, @Nullable String password) {
    return ApacheHttpClientAdapter.builder()
      .withInnerClient(sharedClient)
      .withUserNamePassword(username, password)
      .build();
  }

  public HttpClient getHttpClientWithPreemptiveAuth(String token, boolean shouldUseBearer) {
    return ApacheHttpClientAdapter.builder()
      .withInnerClient(sharedClient)
      .withToken(token)
      .useBearer(shouldUseBearer)
      .build();
  }

  public HttpClient getHttpClientWithXApiKeyAndRetries(String xApiKey) {
    return ApacheHttpClientAdapter.builder()
      .withInnerClient(sharedClient)
      .withXApiKey(xApiKey)
      .withRetries()
      .build();
  }

  public WebSocketClient getWebSocketClient(String token) {
    return new WebSocketClient(userAgent, token, webSocketThreadPool);
  }

  @PreDestroy
  public void close() {
    sharedClient.close(CloseMode.IMMEDIATE);
    if (!MoreExecutors.shutdownAndAwaitTermination(webSocketThreadPool, 1, TimeUnit.SECONDS)) {
      LOG.warn("Unable to stop web socket executor service in a timely manner");
    }
  }
}
