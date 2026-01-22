/*
ACR-3e8a53ac02c849a4b7e2330acbcb15df
ACR-233e1403b3974a7fb4e2c483d20ba3b7
ACR-98ffa1e598a247cb9b081f60a606ae33
ACR-9ef03cc8d79946659201be2ec694cb63
ACR-cbce07b9992b4a2eb0bfcca34ccff35b
ACR-3893baf1f57c40af9ed7bf3f46a1ad70
ACR-6274fbf9665d4f258d22b01cd60ba6bf
ACR-d910c07aea52437e8f10c6ffbab90650
ACR-ab576a48d00643f187c7522c86052d4a
ACR-a354923836f7463cbc13ae4e67d1146c
ACR-756ffd077f654de3bce21d9f36180023
ACR-a8fd1990b9db4ce6a5a63fcf12d59d47
ACR-f678061e884d4146b558d2c08f864818
ACR-e3a7908afeed42c2a3389282fb285325
ACR-e72056775b7748e6a3af2458c2f0c636
ACR-d785d34453284d178c5c5aab4013dc02
ACR-e3b9aac65f0b422792185a104b7c517f
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.plugin;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;

public class DidSkipLoadingPluginParams {
  private final String configurationScopeId;
  private final Language language;
  private final SkipReason reason;
  private final String minVersion;
  private final String currentVersion;

  public DidSkipLoadingPluginParams(String configurationScopeId, Language language, SkipReason reason, String minVersion, @Nullable String currentVersion) {
    this.configurationScopeId = configurationScopeId;
    this.language = language;
    this.reason = reason;
    this.minVersion = minVersion;
    this.currentVersion = currentVersion;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public Language getLanguage() {
    return language;
  }

  public SkipReason getReason() {
    return reason;
  }

  public String getMinVersion() {
    return minVersion;
  }

  @CheckForNull
  public String getCurrentVersion() {
    return currentVersion;
  }

  public enum SkipReason {
    UNSATISFIED_JRE, UNSATISFIED_NODE_JS
  }
}
