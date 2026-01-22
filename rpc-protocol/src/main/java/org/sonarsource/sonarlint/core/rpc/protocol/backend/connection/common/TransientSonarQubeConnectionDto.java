/*
ACR-d0d353d3dbca485491615f80c1c8c7f9
ACR-8e1123a8fd724f72b6e94d38cda2c634
ACR-b0c61a7594e34cc3896c17a6d14ae5a2
ACR-c0ddef4ecdc04a9aae8b7010e76b8242
ACR-6519239a46db4a7986d51ad50a3dc379
ACR-125782cdf2a944c796284aaf7c55568f
ACR-4ada7bc4e67c4cbcad00c4ed577e7c87
ACR-a270b0666e674541bb8f3bb279668b92
ACR-d38ced710b454e2686d50ab8902208be
ACR-0d2e4b723bed45bd8e472971b8b96311
ACR-855fa74c873b4cb3b56a24f8342be435
ACR-f331ef33acad43cab090bbb203a54f35
ACR-d072274804b14cb2965f26afb8cce996
ACR-883d1edacea54a6d8a355ca9aed5d198
ACR-3b7f5100d6054a199c6eab6c874254b8
ACR-7920702912834a5da698e25a9a751057
ACR-def8dbdf8c6f4dc898878bdd70c70b1a
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
