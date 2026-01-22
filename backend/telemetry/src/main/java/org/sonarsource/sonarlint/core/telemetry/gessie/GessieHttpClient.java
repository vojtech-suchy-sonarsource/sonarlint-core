/*
ACR-8d4e966ccff5413fa41e37e55e705eb1
ACR-9463a26e320c410d9fa17c1b18eea694
ACR-7135c6be66494ab685e57dd8c8acbf82
ACR-be92ee1fc2744c9db1aa180b82266ec8
ACR-650963883f9643b29f2ac64345d729e3
ACR-8f26b650862945a8a9dd695937739f72
ACR-4ac0edee1f6a4182994ed5e70fb661f6
ACR-cc783021703a48ff8c3935c3ea33049d
ACR-607c4359b1b748bebaaa7bdc1458e5c7
ACR-a8e9e32b01bc4e30b97aaa948a47ccb8
ACR-ea4c920183da4f1c81f124d91c785c78
ACR-f9e0a66f5bc84a25b5b19d02c97238a8
ACR-1811969c3360448ca8ca7f9d7bc846ed
ACR-49a4cc462b6d4e19af7579be1e7fef96
ACR-7ad543173d2f4f2e98ea4c18c5fd5c3d
ACR-a79397db37614c3a8983e7e0d8ed3391
ACR-090482decb824835ace53c0ee8e58fb2
 */
package org.sonarsource.sonarlint.core.telemetry.gessie;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.concurrent.CompletableFuture;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.http.HttpClient;
import org.sonarsource.sonarlint.core.http.HttpClientProvider;
import org.sonarsource.sonarlint.core.telemetry.InternalDebug;
import org.sonarsource.sonarlint.core.telemetry.gessie.event.GessieEvent;
import org.springframework.beans.factory.annotation.Qualifier;

public class GessieHttpClient {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final Gson gson = configureGson();
  private final HttpClient client;
  private final String endpoint;

  public GessieHttpClient(HttpClientProvider httpClientProvider,
    @Qualifier("gessieEndpoint") String gessieEndpoint,
    @Qualifier("gessieApiKey") String gessieApiKey) {
    this.client = httpClientProvider.getHttpClientWithXApiKeyAndRetries(gessieApiKey);
    this.endpoint = gessieEndpoint;
  }

  public void postEvent(GessieEvent event) {
    var json = gson.toJson(event);
    logGessiePayload(json);
    var futureResponse = client.postAsync(endpoint + "/ide", HttpClient.JSON_CONTENT_TYPE, json);
    handleGessieResponse(futureResponse);
  }

  private void logGessiePayload(String json) {
    if (isTelemetryLogEnabled()) {
      LOG.info("Sending Gessie payload.\n{}", json);
    }
  }

  private static Gson configureGson() {
    return new GsonBuilder()
      .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
      .serializeNulls()
      .create();
  }

  private static void handleGessieResponse(CompletableFuture<HttpClient.Response> responseCompletableFuture) {
    responseCompletableFuture.thenAccept(response -> {
      if (!response.isSuccessful() && InternalDebug.isEnabled()) {
        LOG.error("Failed to upload telemetry to Gessie: {} \n{}", response,
          response.bodyAsString());
      }
    }).exceptionally(exception -> {
      if (InternalDebug.isEnabled()) {
        LOG.error("Failed to upload telemetry to Gessie", exception);
      }
      return null;
    });
  }

  @VisibleForTesting
  boolean isTelemetryLogEnabled(){
    return Boolean.parseBoolean(System.getenv("SONARLINT_TELEMETRY_LOG"));
  }

}
