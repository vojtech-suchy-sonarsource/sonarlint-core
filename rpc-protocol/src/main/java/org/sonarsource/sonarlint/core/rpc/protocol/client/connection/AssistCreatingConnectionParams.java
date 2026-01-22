/*
ACR-0c145419f1154873881124d8c9a47edc
ACR-40aaa4c8c1964fef8de4b7d7f9569d1d
ACR-a9b5d32f0c1d40bab1af4db77cf196af
ACR-e0a6d9650531403b8dce754891d96d02
ACR-42102c7483dd4ee7b096ac2d01d6892d
ACR-b7d4a017583048d6b74b66cdfa590b05
ACR-8c7a2a44ad7e40d7898dbcbbd7b409df
ACR-268b26d58dbc43b190a735ff38ddc24f
ACR-b4cf36fd5d1a4ffaba977d6861e4e9a3
ACR-32257ddbd6204ca0b0983eedf243bbdc
ACR-b7a3edaa7fda4e878579ab5eb0ea5862
ACR-4a5c5c9687394396a75bf79f876e39ea
ACR-269cae99f88e44cda656b6aab5b56def
ACR-94df9df96802486e8d0fd71cddf90a14
ACR-1fd2b8acc7524d85a2420b0259915748
ACR-b418453db09e44dbbe0942f27dfd6b95
ACR-d67963ed61c0419c9e0a3971910ea88d
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
