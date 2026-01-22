/*
ACR-61e153bee3e043dbbddb003f55a4ecd1
ACR-6f124608d69a4f67a70220388e17fc4f
ACR-851b5ea47f37490e911bc4ace8e20b66
ACR-1ff6f6040f504de7aa3dd55546df9627
ACR-f7c2f7f664054ee89753ec6d06fd15b0
ACR-b0c06d88d044437fb022c436698c7971
ACR-1dced9a9c93145d89a66482a13479f56
ACR-6679f6d3280d4a93b65559af8790a5c2
ACR-966437075680455ba6411e5341bda79f
ACR-a75ae45fd9be4f0da8b724b183bf26d0
ACR-78d785b2680e4592ac162d19350ff5f9
ACR-f3a5e9a3cbc14ae594bda516d379111f
ACR-2cb81324b8fc4bbfa48005d9249267ca
ACR-2b4a69a164b249ed8f462c7c58cf1e1b
ACR-2d8e661847be48f3becef5adc52513cb
ACR-aad02e7e2ef64d0aa456066bcebba59d
ACR-d212cbd36da24d67a9384afacb92fd8b
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
