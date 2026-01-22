/*
ACR-b86c31df18ea4c7db04e0729149bc13f
ACR-fe5293a0f103475fbc1e193a5f3eff83
ACR-4f2b0fee50ef4954a6eebe4f645f7dd2
ACR-6f4fecc84c1044ada42e79068a6bc00f
ACR-444cc72bb7b246d7946ebcfb82cab370
ACR-81624f03aba24bdaa3c5782b2f942a2b
ACR-c44895d56e5547e3b96f2160aa5e433e
ACR-1728244152284132b54d3972b9f2b991
ACR-387753a9613d4cbf8d12019fb1d5473e
ACR-8f9222e983004fa88a3fe192fe490c3c
ACR-d4a059ea7025407fbe569314b06aab96
ACR-15a54bbc190e487c986327a065b42dd4
ACR-54aabd9f64ed4cd484e5acc7085da1e6
ACR-4bab91c3b0444355a69bab07805e6e78
ACR-eca22f4242874f3e806005ec6afb787d
ACR-23c534280457463aa6c745d4f08c21aa
ACR-a6771e50ce374b88b8ddcebb4ad0d0e9
 */
package org.sonarsource.sonarlint.core.plugin.commons.sonarapi;

import org.sonar.api.SonarEdition;
import org.sonar.api.SonarProduct;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.utils.Version;
import org.sonarsource.sonarlint.plugin.api.SonarLintRuntime;

import static java.util.Objects.requireNonNull;

public class SonarLintRuntimeImpl implements SonarLintRuntime {

  private final Version sonarPluginApiVersion;
  private final Version sonarLintPluginApiVersion;
  private final long clientPid;

  public SonarLintRuntimeImpl(Version sonarPluginApiVersion, Version sonarLintPluginApiVersion, long clientPid) {
    this.clientPid = clientPid;
    this.sonarPluginApiVersion = requireNonNull(sonarPluginApiVersion);
    this.sonarLintPluginApiVersion = sonarLintPluginApiVersion;
  }

  @Override
  public Version getApiVersion() {
    return sonarPluginApiVersion;
  }

  @Override
  public Version getSonarLintPluginApiVersion() {
    return sonarLintPluginApiVersion;
  }

  @Override
  public SonarProduct getProduct() {
    return SonarProduct.SONARLINT;
  }

  @Override
  public SonarQubeSide getSonarQubeSide() {
    throw new UnsupportedOperationException("Can only be called in SonarQube");
  }

  @Override
  public SonarEdition getEdition() {
    throw new UnsupportedOperationException("Can only be called in SonarQube");
  }

  @Override
  public long getClientPid() {
    return clientPid;
  }

}
