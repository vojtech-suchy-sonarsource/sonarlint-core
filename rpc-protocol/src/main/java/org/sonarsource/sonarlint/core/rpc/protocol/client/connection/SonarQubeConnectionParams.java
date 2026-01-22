/*
ACR-9bb2107775524bdeb445ecd7db836bc8
ACR-8b98e0f9bf6f43288e9d0f9b61d59bb6
ACR-100a1e182c8848c08e16d8172c2b75d7
ACR-7833356f98e04eaa8297d3bdaf440432
ACR-4a01a79dec8842ff96d56624e5e7ef29
ACR-46bc40c0e7d04644ae28982df9fc0ee3
ACR-e93c5d3e2d8a427cb20135f7aaa0393d
ACR-3775156e666b4d3d96222a006121c1c2
ACR-3e9b8211751d4db1ab0838e3fe85ccaf
ACR-940b25f99ae74e1fa331d50e7a97e6d0
ACR-ca506f76122149dfac33eb72078e67c9
ACR-264b2f3f9f1e428c9fd399b93f8a47af
ACR-e532e328f46342da8e89fcb1da7fe6f5
ACR-eee3e38aaa1846108e6eecd87db6ca0f
ACR-ae7a89f3fba144a482a8e0f5118ea6f4
ACR-98e6befd8f2b4d5b96966de875a98c4a
ACR-6c93188b9131429caae6c2b168145762
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.connection;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class SonarQubeConnectionParams {
  private final String serverUrl;
  private final String tokenName;
  private final String tokenValue;

  public SonarQubeConnectionParams(String serverUrl, @Nullable String tokenName, @Nullable String tokenValue) {
    this.serverUrl = serverUrl;
    this.tokenName = tokenName;
    this.tokenValue = tokenValue;
  }

  public String getServerUrl() {
    return serverUrl;
  }

  @CheckForNull
  public String getTokenName() {
    return tokenName;
  }

  @CheckForNull
  public String getTokenValue() {
    return tokenValue;
  }
}
