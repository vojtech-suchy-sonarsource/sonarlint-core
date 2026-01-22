/*
ACR-26f4b9f7d3eb4262be48478dc4f919f3
ACR-52a108cb8e264a2e9762c188ff8a6a63
ACR-30412702fbfe4c6ab222139a5c29333a
ACR-871042576f8d451794274a7dc7f2425a
ACR-8efb3899b8eb42c1ad89c802abbf860c
ACR-fcec0d2c07ae4384bc5b38440d0393a5
ACR-e2b7543554d34f60960da00b8b5bc599
ACR-79148d52b83144ceaa1420eedda8bcc8
ACR-39f28448df084a39a871ae1bef0da367
ACR-36f7bbf538504891802a8722de0b00d4
ACR-4b5fd611477748208b8e1e40dd8d7aae
ACR-c257edf81395420a89f682f3f184131a
ACR-efcdcb3a528c45f1904423f29b784abb
ACR-6a280851ca7543f2a57a12fffce8742f
ACR-a0da5c8f16d74c3282ee94d7007cf027
ACR-d0d6e916b2ab4d91b093bc325ebb9cdc
ACR-2c360c2c46bc44579f8dfbe9b702594e
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

import java.util.Map;

public class DidChangeAnalysisPropertiesParams {
  private final String configurationScopeId;
  private final Map<String, String> properties;

  public DidChangeAnalysisPropertiesParams(String configurationScopeId, Map<String, String> extraProperties) {
    this.configurationScopeId = configurationScopeId;
    this.properties = extraProperties;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public Map<String, String> getProperties() {
    return properties;
  }
}
