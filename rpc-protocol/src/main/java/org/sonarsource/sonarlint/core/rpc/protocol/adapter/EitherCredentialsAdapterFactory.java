/*
ACR-06950dee9d1c4d378c2df5f582e0b2e8
ACR-9ccbd79cf8c64d1e9cd3b5a971b27145
ACR-9dbf4cf944a24149880bc350e79d2fdd
ACR-a522ea62e5024c17a46eb7b7dd67fb77
ACR-cd7d1b99678c40e59457d639f44449c5
ACR-d674bdec17aa49b1959d6e84b0b28375
ACR-9841904f1654463face7c9f5727b3f8c
ACR-60cb2cfb27f14f9e9ec6fc6e90e584bc
ACR-c749b82cdbbb47678fa1fde8db72aad9
ACR-3b1f334870b74300932080f55df4f7f8
ACR-71349962a5464a419df35f6f57e59d15
ACR-1e7774f1dfa9499f80ac962c1c1b84cd
ACR-ad60126c47094a2387a66753b3316c5a
ACR-79000e7f82fb4b5496fc636d08f9da73
ACR-701c840d48af4a9e8546df144b54606c
ACR-4f2aed06d58243c08c684f8a239c6253
ACR-ef71de0f903f4a7e95046a02f0f93c44
 */
package org.sonarsource.sonarlint.core.rpc.protocol.adapter;

import com.google.gson.reflect.TypeToken;
import org.eclipse.lsp4j.jsonrpc.json.adapters.EitherTypeAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TokenDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.UsernamePasswordDto;

public class EitherCredentialsAdapterFactory extends CustomEitherAdapterFactory<TokenDto, UsernamePasswordDto> {

  private static final TypeToken<Either<TokenDto, UsernamePasswordDto>> ELEMENT_TYPE = new TypeToken<>() {
  };

  public EitherCredentialsAdapterFactory() {
    super(ELEMENT_TYPE, TokenDto.class, UsernamePasswordDto.class, new EitherTypeAdapter.PropertyChecker("token"));
  }

}
