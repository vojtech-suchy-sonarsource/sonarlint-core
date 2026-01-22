/*
ACR-80ad0339aaba43828077a84d17de64e6
ACR-29d44c005f9b44cd90865a12196ae6a6
ACR-59f2eabc3867438987f7fce7bca9c444
ACR-50ce5578841f48bfaeab05f6b8fb5363
ACR-2786e44470d34959976f72bf130fd5f7
ACR-02c59b92ac8c4c9d93f69f81d267dd26
ACR-820a7afa01c146608a5aa5e710d66523
ACR-8a371f2a14c34bcb891617bc135cc412
ACR-05ebd64ab9a54ee6b365025e50aeaed5
ACR-c618229ad48549eab8ac69d4958c4b04
ACR-96c8394868c2403cb80681d4ac95fdde
ACR-ca736bc1cae04c5a9b2224e7d1291163
ACR-62df9325dc2e4aabaf6844d3c5c79999
ACR-5881d30e0d8d4612884db461920b994b
ACR-4a0d43bba95344db94577d2c0c3e1aa9
ACR-91f419a94fd24d409e29d037ca6906fa
ACR-affb057f43cf482ba920ca38ca9156c9
 */
package org.sonarsource.sonarlint.core;

import java.net.URI;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.SonarQubeCloudRegionDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SonarCloudActiveEnvironmentTests {
  private static URI baseUri = URI.create("baseUri");
  private static URI apiUri = URI.create("apiUri");
  private static URI webSocketUri = URI.create("webSocketUri");
  
  private static SonarQubeCloudRegionDto regionWithBaseUri = new SonarQubeCloudRegionDto(baseUri, null, null);
  private static SonarQubeCloudRegionDto regionWithApiUri = new SonarQubeCloudRegionDto(null, apiUri, null);
  private static SonarQubeCloudRegionDto regionWithWebSocketUri = new SonarQubeCloudRegionDto(null, null, webSocketUri);
  
  @Test
  void test_getUri() {
    assertThat(SonarCloudActiveEnvironment.prod().getUri(SonarCloudRegion.EU))
      .isEqualTo(SonarCloudRegion.EU.getProductionUri());
    assertThat(SonarCloudActiveEnvironment.prod().getUri(SonarCloudRegion.US))
      .isEqualTo(SonarCloudRegion.US.getProductionUri());

    assertThat(new SonarCloudActiveEnvironment(
      Map.of(org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion.EU, regionWithBaseUri))
      .getUri(SonarCloudRegion.EU))
      .isEqualTo(baseUri);
    assertThat(new SonarCloudActiveEnvironment(
      Map.of(org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion.US, regionWithBaseUri))
      .getUri(SonarCloudRegion.US))
      .isEqualTo(baseUri);

    assertThat(new SonarCloudActiveEnvironment(
      Map.of(org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion.EU, regionWithApiUri))
      .getUri(SonarCloudRegion.EU))
      .isEqualTo(SonarCloudRegion.EU.getProductionUri());
    assertThat(new SonarCloudActiveEnvironment(
      Map.of(org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion.US, regionWithApiUri))
      .getUri(SonarCloudRegion.US))
      .isEqualTo(SonarCloudRegion.US.getProductionUri());
  }

  @Test
  void test_getApiUri() {
    assertThat(SonarCloudActiveEnvironment.prod().getApiUri(SonarCloudRegion.EU))
      .isEqualTo(SonarCloudRegion.EU.getApiProductionUri());
    assertThat(SonarCloudActiveEnvironment.prod().getApiUri(SonarCloudRegion.US))
      .isEqualTo(SonarCloudRegion.US.getApiProductionUri());

    assertThat(new SonarCloudActiveEnvironment(
      Map.of(org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion.EU, regionWithApiUri))
      .getApiUri(SonarCloudRegion.EU))
      .isEqualTo(apiUri);
    assertThat(new SonarCloudActiveEnvironment(
      Map.of(org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion.US, regionWithApiUri))
      .getApiUri(SonarCloudRegion.US))
      .isEqualTo(apiUri);

    assertThat(new SonarCloudActiveEnvironment(
      Map.of(org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion.EU, regionWithBaseUri))
      .getApiUri(SonarCloudRegion.EU))
      .isEqualTo(SonarCloudRegion.EU.getApiProductionUri());
    assertThat(new SonarCloudActiveEnvironment(
      Map.of(org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion.US, regionWithBaseUri))
      .getApiUri(SonarCloudRegion.US))
      .isEqualTo(SonarCloudRegion.US.getApiProductionUri());
  }

  @Test
  void test_getWebSocketsEndpointUri() {
    assertThat(SonarCloudActiveEnvironment.prod().getWebSocketsEndpointUri(SonarCloudRegion.EU))
      .isEqualTo(SonarCloudRegion.EU.getWebSocketUri());
    assertThat(SonarCloudActiveEnvironment.prod().getWebSocketsEndpointUri(SonarCloudRegion.US))
      .isEqualTo(SonarCloudRegion.US.getWebSocketUri());

    assertThat(new SonarCloudActiveEnvironment(
      Map.of(org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion.EU, regionWithWebSocketUri)).
      getWebSocketsEndpointUri(SonarCloudRegion.EU))
      .isEqualTo(webSocketUri);
    assertThat(new SonarCloudActiveEnvironment(
      Map.of(org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion.US, regionWithWebSocketUri))
      .getWebSocketsEndpointUri(SonarCloudRegion.US))
      .isEqualTo(webSocketUri);

    assertThat(new SonarCloudActiveEnvironment(
      Map.of(org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion.EU, regionWithApiUri))
      .getWebSocketsEndpointUri(SonarCloudRegion.EU))
      .isEqualTo(SonarCloudRegion.EU.getWebSocketUri());
    assertThat(new SonarCloudActiveEnvironment(
      Map.of(org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion.US, regionWithApiUri))
      .getWebSocketsEndpointUri(SonarCloudRegion.US))
      .isEqualTo(SonarCloudRegion.US.getWebSocketUri());
  }
  
  @Test
  void test_isSonarQubeCloud() {
    assertThat(SonarCloudActiveEnvironment.prod().isSonarQubeCloud("aaaa")).isFalse();

    assertThat(SonarCloudActiveEnvironment.prod()
      .isSonarQubeCloud(SonarCloudRegion.EU.getProductionUri().toString())).isTrue();
    assertThat(SonarCloudActiveEnvironment.prod()
      .isSonarQubeCloud(SonarCloudRegion.US.getProductionUri().toString())).isTrue();

    assertThat(new SonarCloudActiveEnvironment(
      Map.of(org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion.EU, regionWithBaseUri))
      .isSonarQubeCloud(baseUri.toString())).isTrue();
    assertThat(new SonarCloudActiveEnvironment(
      Map.of(org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion.EU, regionWithApiUri))
      .isSonarQubeCloud(SonarCloudRegion.EU.getProductionUri().toString())).isTrue();
    assertThat(new SonarCloudActiveEnvironment(
      Map.of(org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion.US, regionWithBaseUri))
      .isSonarQubeCloud(baseUri.toString())).isTrue();
    assertThat(new SonarCloudActiveEnvironment(
      Map.of(org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion.US, regionWithApiUri))
      .isSonarQubeCloud(SonarCloudRegion.US.getProductionUri().toString())).isTrue();
  }

  @Test
  void test_getRegionOrThrow() {
    assertThatThrownBy(() -> SonarCloudActiveEnvironment.prod().getRegionOrThrow("aaaa"))
      .isInstanceOf(IllegalArgumentException.class);

    assertThat(SonarCloudActiveEnvironment.prod()
      .getRegionOrThrow(SonarCloudRegion.EU.getProductionUri().toString())).isEqualTo(SonarCloudRegion.EU);
    assertThat(SonarCloudActiveEnvironment.prod()
      .getRegionOrThrow(SonarCloudRegion.US.getProductionUri().toString())).isEqualTo(SonarCloudRegion.US);

    assertThat(new SonarCloudActiveEnvironment(
      Map.of(org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion.EU, regionWithBaseUri))
      .getRegionOrThrow(baseUri.toString())).isEqualTo(SonarCloudRegion.EU);
    assertThat(new SonarCloudActiveEnvironment(
      Map.of(org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion.US, regionWithBaseUri))
      .getRegionOrThrow(baseUri.toString())).isEqualTo(SonarCloudRegion.US);
  }
}
