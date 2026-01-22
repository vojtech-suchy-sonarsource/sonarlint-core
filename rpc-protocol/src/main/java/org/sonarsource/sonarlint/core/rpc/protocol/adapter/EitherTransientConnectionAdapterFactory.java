/*
ACR-8f68dd45055344dabdf80f8ef4ab813b
ACR-a5825d73d65d40a1b5a080ee382a6f9e
ACR-b5558118945749739b6e421e9522b4a8
ACR-4a8b9f06158e4244b4fa18409b07f64a
ACR-6e9f2af79ba74e238ea7d923c5b34f28
ACR-57c33156bcc94596b0eba3afeb100ff7
ACR-ea884457b2654f248cfae74168c2f55f
ACR-5a6870fb73374c908a5103800be742bc
ACR-557c53cdd28844da89a09887ec064989
ACR-a2707eba4a014945a51a404eabbf3bb3
ACR-05a97ee710ba48318bb66b2aac0ec5cd
ACR-3ff9cad63a0a4c5b8116f4bbe8d1f099
ACR-ac604c3d883a40938af1b7e4fbf9b21c
ACR-c325e3d131554afaa53eea72e805eb5e
ACR-ef1e4010bf0a4ec0b1ed7028aa7538b5
ACR-b3ffaa93a96b417f984bc64f1cac2079
ACR-56667ea7640b415ca6f54d969e42ec30
 */
package org.sonarsource.sonarlint.core.rpc.protocol.adapter;

import com.google.gson.reflect.TypeToken;
import org.eclipse.lsp4j.jsonrpc.json.adapters.EitherTypeAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.common.TransientSonarCloudConnectionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.common.TransientSonarQubeConnectionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;

public class EitherTransientConnectionAdapterFactory extends CustomEitherAdapterFactory<TransientSonarQubeConnectionDto, TransientSonarCloudConnectionDto> {

  private static final TypeToken<Either<TransientSonarQubeConnectionDto, TransientSonarCloudConnectionDto>> ELEMENT_TYPE = new TypeToken<>() {
  };

  public EitherTransientConnectionAdapterFactory() {
    super(ELEMENT_TYPE, TransientSonarQubeConnectionDto.class, TransientSonarCloudConnectionDto.class, new EitherTypeAdapter.PropertyChecker("serverUrl"));
  }
}
