/*
ACR-51f877d47efe43d6a28df265c9ad1b15
ACR-bc805cdee5134356a80008a23b3e8339
ACR-ddf48b5d2e024539a08c2161cd645ecc
ACR-6a1c0ccd490d45fd9d357642ae2a2d6b
ACR-31a314e415cb486baee13cb2520dcc0e
ACR-d214fd002afd402cbb7e1939c61b6588
ACR-7d43642d2a354d33a1be3a410eaa8c60
ACR-245583aa51324f9fa0f5fd664b5b8456
ACR-c50badd315c942ada268303e8f75fda7
ACR-bcf016e0d17342f9b869129bc4e0767b
ACR-ecd4541ddeb4449d848c743421ab9c25
ACR-cd64587521ad4a8e979a61d6fd193c0f
ACR-6b09042184d149b6addebba5ae17532d
ACR-02f464eccb12459bb7159bb7cc7b7357
ACR-25c77a51626e4b9797770087844b147a
ACR-2fac37403b284faa85dbe9d4f740f0b2
ACR-6c52bac2d97440b19ce62ba6cf082a04
 */
package org.sonarsource.sonarlint.core.commons;

/*ACR-40c46674d2c74aef8af9502da9e228dd
ACR-55811d5517274b98a204a39293a46535
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
