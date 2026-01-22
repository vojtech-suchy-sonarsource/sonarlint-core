/*
ACR-17f974404e2a4551a12fd07c4f68f5f1
ACR-15a604364aa24e1b83653a789489b5ba
ACR-16be81b560a047ac80fe2dd5aa0b9338
ACR-22f0b0038a574112bcad655c661f725c
ACR-e06ffb114aac4d6db2506ca2f6ccc0be
ACR-dc21f4e940194f51a596f31e6eebf6ad
ACR-bd94e2b017394ef385c13a02434bb21b
ACR-0c7a26a07b4347a28537d5a50d59c106
ACR-e1cd0e6789684dc38d64fe7adf3b40c2
ACR-cc80580b46934e5da5faa158d3c8bbbc
ACR-eb5f07b64412480eac9f05ac8ca13dcf
ACR-6565802c2c234da288201c2a64fb7019
ACR-1aa62fbc95f84683a18218b9198ac680
ACR-5f9a8d6f1fde4051b50e10ae5ad9d18e
ACR-bdb4f76b91de4a2997b92d65b4653489
ACR-1dcd9b48d12e492281edcbd0f1ad5ff3
ACR-fec2c2b2dcfb432cb40e055ab6737675
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
