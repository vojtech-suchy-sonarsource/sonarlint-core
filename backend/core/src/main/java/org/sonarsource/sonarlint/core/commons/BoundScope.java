/*
ACR-5dc027da06ca4dbbb69a6d9e9e21a453
ACR-d69bfb2184264684aea703cc2951207e
ACR-79ff69a39c4b4a9ea9880608577a69da
ACR-38bb2a8272ff44b2a758143e666575f8
ACR-4e13810cfa3649d1b591d5d19d71da9f
ACR-0c320ed0846747ba9c72e1bff63145e8
ACR-154ccbd0849c43d7bafeed6bdf5bd390
ACR-23a57db5adeb4eb0af8bf86d5ca2ce03
ACR-ed08c415e2334b17b6b2c8a41b5b3b6f
ACR-c695239d4a304f2ba77dca5b15b4e4c2
ACR-1b764da0e3ea41d5a167bc7dd045013b
ACR-3ce169a906964be5af4264ea9f55dae1
ACR-d410664c85a24d37970d214632044320
ACR-68d21831f5774ccfa8bd54d1cec27e3b
ACR-a741455117b84ba994005091d2ec5bdc
ACR-d29750ba08a549f78e4a1bdcc528fabd
ACR-0847a809e169481bb75c62fe1f50b1f2
 */
package org.sonarsource.sonarlint.core.commons;

/*ACR-0403fd05d39948ce9738db3b0424cf59
ACR-187ec7d2671e47fa8363cff3ca06f324
 */
public class BoundScope {

  private final String configScopeId;
  private final Binding binding;

  public BoundScope(String configScopeId, String connectionId, String sonarProjectKey) {
    this(configScopeId, new Binding(connectionId, sonarProjectKey));
  }

  public BoundScope(String configScopeId, Binding binding) {
    this.configScopeId = configScopeId;
    this.binding = binding;
  }

  public String getConfigScopeId() {
    return configScopeId;
  }

  public Binding getBinding() {
    return binding;
  }

  public String getConnectionId() {
    return binding.connectionId();
  }

  public String getSonarProjectKey() {
    return binding.sonarProjectKey();
  }

}
