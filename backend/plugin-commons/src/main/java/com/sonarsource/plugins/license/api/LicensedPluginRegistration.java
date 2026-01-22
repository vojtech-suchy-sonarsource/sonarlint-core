/*
ACR-b7e5646cb66949ceab85cea7e6c6eb38
ACR-722f81e1ca304c7384f11af5be7dd352
ACR-11322d462fe84ff79171e365e6ac9475
ACR-3dcef9b5a40b409ebcbe9aba30d81212
ACR-b29598c4a964434ba6d8a708e96efc92
ACR-a218042cf662402dbcf0b006ffceae58
ACR-9fc278f224b14979933c72fc78e3853f
ACR-b41d003c8ab44f2bb9ffe36cede4e15e
ACR-f3fab465fe2b47ac8178282dafda9b92
ACR-b5f84c0b254a47aab3f960b16820464f
ACR-00e0aa9bf7ea4fd1ac9f92dfde1dc416
ACR-15df3e9979094395a11e18d2d689d9f6
ACR-8be2082de18d4dd89efc55226b65d0a4
ACR-05ec542c081e4ac693d6d5c0b238379f
ACR-ca8cc415c9e44ddc8794b4a77e244306
ACR-672196489b0943a399b032c66a1362c2
ACR-f9245aeaa46e4b4fa13b46b1d94bccf1
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
