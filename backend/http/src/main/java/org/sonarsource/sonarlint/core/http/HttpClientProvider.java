/*
ACR-6d51521bbe2f4c1f858ae885a66a60f4
ACR-e9be003579c5468f83a53eb2da72e66d
ACR-01749ebd9d1e463a94bb7642334e2091
ACR-0271505017654415ba0e11d5b8c6adba
ACR-206872a8749841e99edfeb85b79ea308
ACR-50d0ca6a47454e4dbf3b380d5aa383a8
ACR-397bd56f5e0d4094935cc10cf77ef234
ACR-0bb9ef69e1c34e5fae4bbd0e99df2d6f
ACR-31ab87d8266744f1a21c425c25319f54
ACR-3d0b5143c8554ca99169e62a9c62e0e6
ACR-c51195d3276542339c3bdc98c1e39396
ACR-156dae7c000e413695493f241911dfcd
ACR-dd1af86e0c304e23a8cfb7a8b5d041d1
ACR-ccf6e2171691473e8a319419822673ea
ACR-a86ccb404be64686a29c00bd2e347643
ACR-5fe76200ad5a42738054b70e4c8f46ff
ACR-7fdce7b2d8f14ba6bc3653e661f1ab04
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

  /*ACR-80bfc6abc33c4f09b38e47a62eadac7d
ACR-df37fba9468f429684d56615a9ea15d4
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
        //ACR-50bf0831d9664011901adb6413a89659
        .setVersionPolicy(HttpVersionPolicy.FORCE_HTTP_1)
        .build())
      .setDefaultConnectionConfig(buildConnectionConfig(httpConfig.connectTimeout(), httpConfig.socketTimeout()))
      .build();
    var routePlanner = new SystemDefaultRoutePlanner(proxySelector);
    return HttpAsyncClients.custom()
      .setConnectionManager(asyncConnectionManager)
      .addResponseInterceptorFirst(new RedirectInterceptor())
      .setUserAgent(userAgent)
      //ACR-597c3c7bdf6f4c09be7c61f64fee3fe6
      .setRoutePlanner(routePlanner)
      .setDefaultCredentialsProvider(proxyCredentialsProvider)
      .setDefaultRequestConfig(buildRequestConfig(httpConfig.connectionRequestTimeout(), httpConfig.responseTimeout()))
      .setRetryStrategy(new RetryOnDemandStrategy(maxRetries, TimeValue.ofSeconds(retryInterval)))
      .build();
  }

  private static SSLContext configureSsl(SslConfig sslConfig, @Nullable Predicate<TrustManagerParameters> trustManagerParametersPredicate) {
    var sslFactoryBuilder = SSLFactory.builder()
      .withDefaultTrustMaterial();
    //ACR-61704f12fe2941fa8742c9ba37c02cc9
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
