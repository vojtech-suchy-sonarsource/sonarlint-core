/*
ACR-daa0028166aa408697c21d2a40dfe18c
ACR-7ef55775990b4b249b37227fdf318c5a
ACR-db851b6057fd4af8b33bf78337338fcf
ACR-0367bf4d45714a3eab879151ac238f28
ACR-b441d06e56044f619ab1249ab2efb99f
ACR-0423730e99c446d2b9fe29a342106f98
ACR-72557c137bbc41eeb3afa02dca8ee238
ACR-459b0e6ed5c74d1a832889bcc2cfcab5
ACR-3430db6505db4080aa92a6b1e2eba400
ACR-d0ab003c91b047d9b3162de773d8e093
ACR-0324ea8e99924098a687172d0419239c
ACR-c679d76eec0e45e994c7b719cfd909af
ACR-a98f0aa5e7a4498c980f4bfc4ec79651
ACR-11c5ae0022e345f69698661b6149bff0
ACR-d69907ea841d471ea3fedc566ef8bc3b
ACR-9b6712c8499f474c9fd2c11278e00a36
ACR-569dceeaa59345a7b39fdfd29fc4053a
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

  /*ACR-3b5a5256ab95491e89d518c2a07c63c9
ACR-94f30620e43d49b9921871c1dc104efe
   */
  @CheckForNull
  public Either<TokenDto, UsernamePasswordDto> getCredentials() {
    return credentials;
  }
}
