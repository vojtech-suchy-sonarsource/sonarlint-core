/*
ACR-28e913f1f7bc4681acf662e218a7086e
ACR-6c9542c95dc04b24bff36a8a208d5c9f
ACR-25bddfd2e7f848bf96deb164651910cf
ACR-d96a8075dc28410eb399e4fde37f12be
ACR-2fd3bc9816fa4493a359646bdf4cf93e
ACR-48cfcf980d9c48848e43af9f2e416632
ACR-fc23f233a468478fbb25d2f1b92708dd
ACR-8dd5d21338ed49dda8f6c0fe45c64ac6
ACR-861b67526b99407b8517b03f57fe08d5
ACR-41c6e3a880714bd795b4a746d629fe7b
ACR-2727b02214304a268c8fbd73b7030553
ACR-fb96c5f86fbf40f897982cfbc6d01c60
ACR-548bc1df18ba46509b5239ffd71d26ab
ACR-e10b64ada3cd44588a6687ba4883e8f4
ACR-f0562559a05443df844e41e9f1570839
ACR-2e4720f341514de984cbc719888c8330
ACR-1a557286bf074b3da81d3eafc267e97b
 */
package com.sonarsource.plugins.license.api;

public class LicensedPluginRegistration {

  private final String pluginKey;

  private LicensedPluginRegistration(Builder builder) {
    this.pluginKey = builder.pluginKey;
  }

  public String getPluginKey() {
    return pluginKey;
  }

  public static LicensedPluginRegistration forPlugin(String pluginKey) {
    return new Builder().setPluginKey(pluginKey).build();
  }

  public static final class Builder {
    private String pluginKey;

    public Builder setPluginKey(String s) {
      this.pluginKey = s;
      return this;
    }

    public LicensedPluginRegistration build() {
      return new LicensedPluginRegistration(this);
    }
  }
}
