/*
ACR-083205a594d149559f25b18dc1c0a906
ACR-361f356bb21d4dac9898681a0c1c0ddf
ACR-7c4b99ef6e1845c69b6f427689fda148
ACR-ce24184e25834fcea9f5211b9e493d29
ACR-afbbf724adef4020a06b35fd23257fca
ACR-e443339d2b534f2487deb16035790adb
ACR-2df315ac6f0c48aa9c7b5b8d851e4b4e
ACR-b394f82be7904979af4ca7735f288322
ACR-731a59c4a1634bfd89ab54ffdf9cb677
ACR-62a9fb92bbde49bf8fcde0c229622854
ACR-fb540f4757854d2a8ab30f814bf06d8d
ACR-1ee419abd5304f5d90b904786ee75d05
ACR-c7fd0d611fa0450d9518cce2d1689430
ACR-b976c6dab3844f5cb48f77322d80908a
ACR-0691e02f2d9140eda902505ffef6730b
ACR-32e8f47f4ade4c39883bc1deb88d08da
ACR-1eb506bc03bd421a87c2cbffc70f39ce
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.connection;

import com.google.gson.annotations.JsonAdapter;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.adapter.EitherCredentialsAdapterFactory;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TokenDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.UsernamePasswordDto;

public class GetCredentialsResponse {

  @JsonAdapter(EitherCredentialsAdapterFactory.class)
  @Nullable
  private final Either<TokenDto, UsernamePasswordDto> credentials;

  public GetCredentialsResponse(@Nullable Either<TokenDto, UsernamePasswordDto> credentials) {
    this.credentials = credentials;
  }

  public GetCredentialsResponse(TokenDto token) {
    this(Either.forLeft(token));
  }

  public GetCredentialsResponse(UsernamePasswordDto usernamePassword) {
    this(Either.forRight(usernamePassword));
  }

  /*ACR-ec3b2bee48bc404faa7283d0f5e0690c
ACR-9abff17bc5d2423a98e693f179b145aa
   */
  @CheckForNull
  public Either<TokenDto, UsernamePasswordDto> getCredentials() {
    return credentials;
  }
}
