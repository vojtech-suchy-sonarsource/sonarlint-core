/*
ACR-791fc0c1bc2e44e7a06555a00d74336b
ACR-206211306ae84f08a667200bafe31243
ACR-ee6ebb289d224eac91816d70c9d69fb5
ACR-37c412bd18284c98bb03221bfc2ac05d
ACR-d4aacaad6004418890c2030aeb162b35
ACR-5a7dce766a674dd5b970faf70166552a
ACR-a2d601c7bfad4ddb8962c5aca940df9c
ACR-31ff47ad9573409086e9ec47b004ca05
ACR-7bde1678af2f40b2b11fdaeda05aebb1
ACR-2a1e4de43cea4fd8aeb564f8652efa5d
ACR-0b47760d834c48769b3aed071503b50e
ACR-b7891a855c024e06a36045bdd14547f0
ACR-048cca1fd290491791a504a92b4f617b
ACR-8a76ff7e1e6b455e949e13990d305ef0
ACR-bb5fa5ea14d848e4bbf9d94600d7e153
ACR-955a921f29a744809d2ed4b713044a0f
ACR-343b44f5d89d494f9ba4b6c39173be83
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
