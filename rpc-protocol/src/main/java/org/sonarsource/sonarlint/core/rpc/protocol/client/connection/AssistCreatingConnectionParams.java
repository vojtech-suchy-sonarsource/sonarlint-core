/*
ACR-8580ed3556594ada9629227877375f39
ACR-fb4694aa5bbd4769aaa63c9ef402b38e
ACR-3ebf410455764640a63964c8a4bf9a1a
ACR-f632fc5875194e05b9e57296ff0b5627
ACR-02157393e8004b29b9f2b3d8d4ee629d
ACR-9a0787833c6946c3ace44e1fa2a0695b
ACR-a178e2c1308644eba6e0af1b2e8f8b75
ACR-ac8f0632c1564bf4ae3659a9898e4e38
ACR-5905b83226c04d8cae87712403a16b6b
ACR-ca69c521224f4462b076708f0c851709
ACR-dd7f9783ecc34050a9c40bd8be9733d9
ACR-9e8895db527c4d4b9c7f9ac9606734e3
ACR-31fceb117a9149eab35ba306846e4d95
ACR-8282916f9be44b47b468f806d31e03f6
ACR-d831cecb60294bb6aa47c44f7edd5d42
ACR-fca8cb5de58a40b1a85b93c8a55c3940
ACR-fdfec08c0a384b7ba6d73262f7ab688b
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.connection;

import com.google.gson.annotations.JsonAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.adapter.EitherSonarQubeSonarCloudConnectionParamsAdapterFactory;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;

public class AssistCreatingConnectionParams {
  @JsonAdapter(EitherSonarQubeSonarCloudConnectionParamsAdapterFactory.class)
  private final Either<SonarQubeConnectionParams, SonarCloudConnectionParams> connectionParams;

  public AssistCreatingConnectionParams(Either<SonarQubeConnectionParams, SonarCloudConnectionParams> connectionParams) {
    this.connectionParams = connectionParams;
  }

  public AssistCreatingConnectionParams(SonarQubeConnectionParams sonarQubeConnection) {
    this(Either.forLeft(sonarQubeConnection));
  }

  public AssistCreatingConnectionParams(SonarCloudConnectionParams sonarCloudConnection) {
    this(Either.forRight(sonarCloudConnection));
  }

  public Either<SonarQubeConnectionParams, SonarCloudConnectionParams> getConnectionParams() {
    return connectionParams;
  }

  public String getTokenName() {
    return connectionParams.isLeft() ?
      connectionParams.getLeft().getTokenName()
      : connectionParams.getRight().getTokenName();
  }

  public String getTokenValue() {
    return connectionParams.isLeft() ?
      connectionParams.getLeft().getTokenValue()
      : connectionParams.getRight().getTokenValue();
  }
}
