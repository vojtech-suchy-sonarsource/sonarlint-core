/*
ACR-54900bad2bb844b08b13ca123ae027b6
ACR-e0a8e17b3d554177b69c45584ed8159a
ACR-dff1cd766fc8487c86a920863ecfcf0d
ACR-e28f5addee1e492597c14d23e36ffcae
ACR-eacf255feab7435581d8f4d070de8bb7
ACR-4c61e74f8717416f86aa93badbd93491
ACR-6cd60be25ad84be8b7f49f1bf447079d
ACR-9a4f653dcc824d65a97b71e89da9b919
ACR-cd012c4da1d0421d97a1ecd5acbf1761
ACR-71916cac11aa4ef28034352d144bdf9e
ACR-52ebbe79a8bc4b20ae7368442c6e57dd
ACR-72799fedaef948f2829b7d74d05fc06b
ACR-1725b90377054586ad50a08999598bc0
ACR-25157bd87a474f5581ea97b7e096bdcd
ACR-2e00244bd6ca4a15bd94de04d7ceeaf3
ACR-d400e83778b645809a9d1cb9582bf42d
ACR-8c805fc0b1f64cf1833d8d6e8f19c1f1
 */
package org.sonarsource.sonarlint.core.plugin.commons;

import org.junit.jupiter.api.Test;
import org.sonar.api.batch.InstantiationStrategy;
import org.sonar.api.batch.ScannerSide;
import org.sonar.api.ce.ComputeEngineSide;
import org.sonar.api.server.ServerSide;
import org.sonarsource.api.sonarlint.SonarLintSide;

import static org.assertj.core.api.Assertions.assertThat;

class ExtensionUtilsTests {

  @Test
  void shouldBeBatchInstantiationStrategy() {
    assertThat(ExtensionUtils.isInstantiationStrategy(DefaultScannerService.class, InstantiationStrategy.PER_BATCH)).isFalse();
    assertThat(ExtensionUtils.isInstantiationStrategy(new DefaultScannerService(), InstantiationStrategy.PER_BATCH)).isFalse();

  }

  @Test
  void shouldBeProjectInstantiationStrategy() {
    assertThat(ExtensionUtils.isInstantiationStrategy(DefaultScannerService.class, InstantiationStrategy.PER_PROJECT)).isTrue();
    assertThat(ExtensionUtils.isInstantiationStrategy(new DefaultScannerService(), InstantiationStrategy.PER_PROJECT)).isTrue();

  }

  @Test
  void testIsSonarLintSide() {
    assertThat(ExtensionUtils.isSonarLintSide(ScannerService.class)).isFalse();

    assertThat(ExtensionUtils.isSonarLintSide(ServerService.class)).isFalse();
    assertThat(ExtensionUtils.isSonarLintSide(new ServerService())).isFalse();
    assertThat(ExtensionUtils.isSonarLintSide(new WebServerService())).isFalse();
    assertThat(ExtensionUtils.isSonarLintSide(new ComputeEngineService())).isFalse();
    assertThat(ExtensionUtils.isSonarLintSide(new DefaultSonarLintService())).isTrue();
  }

  @ScannerSide
  @InstantiationStrategy(InstantiationStrategy.PER_BATCH)
  public static class ScannerService {

  }

  @ScannerSide
  public static class DefaultScannerService {

  }

  @SonarLintSide
  public static class DefaultSonarLintService {

  }

  @ServerSide
  public static class ServerService {

  }

  @ServerSide
  public static class WebServerService {

  }

  @ComputeEngineSide
  public static class ComputeEngineService {

  }

}
