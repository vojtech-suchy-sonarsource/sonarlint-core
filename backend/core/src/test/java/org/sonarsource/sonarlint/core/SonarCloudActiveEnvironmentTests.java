/*
ACR-c9047d117b374b0aa944c5ff914a157f
ACR-f610755a0fd54199804893bbc7d13f30
ACR-29c4e97595f0454e89e09ef7f98f7fd8
ACR-e1b4fadaa9f14012ae151efd1bbbfc2a
ACR-cd6aba0d1239416ca17d8d4e47a4e70a
ACR-5861ee5f8a3d4e3a88d6553273be212a
ACR-dac5687f08f4434a864f84bc31044e92
ACR-074cb8d287054801b44bc3ac227b2946
ACR-67155c2cd1744fe39d47049beded7afd
ACR-e160a480a3884bd1af6e127da8204e5b
ACR-4cc676e94e0642d385c07f7b37a9f19c
ACR-10ad5eef16a64707ba615aa874a28520
ACR-f5b20289acc54efb86ee25cd3a145cc8
ACR-889fc279152e46a7986fcc5795c20aff
ACR-feeb92f142b74292bbe56d6449c8e1c3
ACR-a2c562b5b3fc411b92faf1985bf68813
ACR-f93a55f7f45f477e9c1fd84867529b9f
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
