/*
ACR-392e0ea55f6a4864be82958900de82f8
ACR-b7bb4887b5eb4fef8eef654dfe191e08
ACR-9fce42e57ed84d40a5f38d04d0a27bfc
ACR-593c23491ffd40a5a5ced6ce0050c578
ACR-04cdb1897c914256b452e592a9a4a78e
ACR-9ce83865b034445791fea7b55d60b63a
ACR-824ae2e91c2843cea3fc1e3db32947e8
ACR-9bbe85b3af8d48f28a42610a183b78c1
ACR-6e26e74341ce4054b4ff0df507bd465d
ACR-72f3e26b3f7d46f286379d39ff2e6bbe
ACR-7c0fe2d1b7284b8e91de00c43273c866
ACR-fe826c69943c4b6db209f445dbd5007d
ACR-96483ee941ad4b23b207c58e36e6423e
ACR-fcd6e290fe7546789b942c50638a25a6
ACR-b4e8ce9a2c434ae78b0632f4044177b6
ACR-d87a1cfe89074263a912dfb554ec2ffc
ACR-4ae9bd2f4d844a3ebfeac3714fe8ea23
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
