/*
ACR-517e2dbf501a449dac9ff4d2c9f21812
ACR-d6c897ef536d41e08d10d9249c5bfbee
ACR-d9ce9b1dea264a6c9b264f0a9e616740
ACR-999f0bac70864dc1b4ff83c745d0ec18
ACR-73392a9f5aa845baa3de90ab761df8df
ACR-29fc137455a64cc1b7fd9e09427b79e0
ACR-33ac9e996e654f41aff53f64e2b5f7d4
ACR-d4a0c90d34744bccb370cd49c03a82f7
ACR-b99599ed6bce4858a69c55e15b8ccdb1
ACR-f81655a55fe145428cb4cb0c55d178c8
ACR-dbd4ad42c19a494cb0d2e97e5fa6e9a4
ACR-5cf9441858f04caeacef7d44a21e0e58
ACR-49012d9df0a542c6bde64424257c447c
ACR-66f1567e544848fc8f5e7b201c1d703a
ACR-ef50a21ba3ad474aa20fc7c0bb27e758
ACR-99be17982fe94235ab8107d3e79c0213
ACR-d3389a11d53544e58bac5956dd6cb1b2
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.common;

import com.google.gson.annotations.JsonAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.adapter.EitherCredentialsAdapterFactory;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TokenDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.UsernamePasswordDto;

public class TransientSonarQubeConnectionDto {

  private final String serverUrl;

  @JsonAdapter(EitherCredentialsAdapterFactory.class)
  private final Either<TokenDto, UsernamePasswordDto> credentials;

  public TransientSonarQubeConnectionDto(String serverUrl, Either<TokenDto, UsernamePasswordDto> credentials) {
    this.serverUrl = serverUrl;
    this.credentials = credentials;
  }

  public String getServerUrl() {
    return serverUrl;
  }

  public Either<TokenDto, UsernamePasswordDto> getCredentials() {
    return credentials;
  }
}
