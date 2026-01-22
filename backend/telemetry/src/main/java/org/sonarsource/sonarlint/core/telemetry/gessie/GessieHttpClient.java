/*
ACR-65cce51f18d1446096710e565a03aba5
ACR-5954f5f1c74c40409e5a13a9491eba01
ACR-e0964e2b4eee4c0da59b8fc1bac561cb
ACR-5c7f7bce70ae46eca7829a601365d5c4
ACR-40bd0e68b5114068a3b71ef03b1c1d84
ACR-d4395818fcd2432aa62620739de46a96
ACR-630039e6018d48c0bde4b965c1b7bfd9
ACR-6f4ec376b7de4d719a64462de4372ec1
ACR-63e3bf45b5724f34a8b2d6a91e0e72d0
ACR-a8cba16506aa4e14ade7d2394726cc95
ACR-4ec68bc31f0c4a04bcac8d117cf61086
ACR-f3cb839e1eea46bc84bcfaad6a65db4b
ACR-07ed4d01f9b04c70abc1ac1b4e219e50
ACR-5e2e82fcc4024b40b43bc67f00cbf23c
ACR-33f96db587a94d5a9173048673b34e00
ACR-35bffa5e0b70406b8c8bbe10692c3d39
ACR-4ea67e7b98ef4367b3c87061ba71c0a8
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
