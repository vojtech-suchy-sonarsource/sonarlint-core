/*
ACR-fb1b5f1f93a24faba1b7ed81dec7bac0
ACR-ed1d13b3558e44288362771265a3a050
ACR-6f3216db79e6432e9b3bb5d5e7504b4b
ACR-ba1c925950d64e25af039b87afe1595c
ACR-43c35a0c03d04e87a7aadb86c5e05499
ACR-e696a7f92e0746b5b4339dbbcf07fb71
ACR-0b5a81f7a6f940b4af62636b5ba9f17a
ACR-56ac7753d4e34988b9ef91a3819e480f
ACR-af8ba2bf61cb4b99ba361ac7ed194e86
ACR-6927210f130545b7a86c73cceb01664b
ACR-9c0871f1aa2c4d058920a44efa540320
ACR-31dbb84fb7144bfb8dd16582d5e13533
ACR-d12b10453da14f1bbb458a172c630e2c
ACR-90724afd55ba4b2fb40cc4148b2292e4
ACR-ae2d8e901cc54aaa938b0999be0ec5d5
ACR-65548d95277c40c0ae255adcc1d0272f
ACR-fbfe228acd4b4f5e8ac4f44fe6032f1c
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.auth;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

/*ACR-fe9cbcc99f6b410b9b6284e760b466bb
ACR-0bb1fde43eea4bd8887bee00e1871776
 */
public class HelpGenerateUserTokenResponse {
  private final String token;

  public HelpGenerateUserTokenResponse(@Nullable String token) {
    this.token = token;
  }

  @CheckForNull
  public String getToken() {
    return token;
  }
}
