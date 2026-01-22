/*
ACR-1f31f4feedb14870a700a711501ba47e
ACR-6a5c6a55fa214b26a1215ac866d0cc22
ACR-af3151b543cf4dd8beaf5450e8b2922f
ACR-bf8ba55d19cc4020823308f3613c7174
ACR-025fc81646704821934559e4d81f71ae
ACR-e336977d5a9b4e828639380bfdc392af
ACR-c2daae60af04444385619b56b489ac85
ACR-3ec374b0aa2f4760ae8234ee03dccdef
ACR-c621c32bd620434f86376a273dbb645c
ACR-b8fd305181424e24a8e195de16f1156d
ACR-d6fabdfdb78e47e680634ad4ab7c03fd
ACR-dbd312a56a754e2d84727666f156fb6d
ACR-daf1edd4559b4495883fe2b54c5e57b9
ACR-61019c06a2cb4091ab63e8b4e87f4f29
ACR-48c1c89a704b4d6c848a80d1c5dc6038
ACR-5151f8f0c1974714ae5a2e72da4dd08c
ACR-98de5606d73a4ff495cf508d6228ef08
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.validate;

import com.google.gson.annotations.JsonAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.adapter.EitherTransientConnectionAdapterFactory;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.common.TransientSonarCloudConnectionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.common.TransientSonarQubeConnectionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;

public class ValidateConnectionParams {

  @JsonAdapter(EitherTransientConnectionAdapterFactory.class)
  private final Either<TransientSonarQubeConnectionDto, TransientSonarCloudConnectionDto> transientConnection;

  public ValidateConnectionParams(Either<TransientSonarQubeConnectionDto, TransientSonarCloudConnectionDto> transientConnection) {
    this.transientConnection = transientConnection;
  }

  public ValidateConnectionParams(TransientSonarQubeConnectionDto transientConnection) {
    this(Either.forLeft(transientConnection));
  }

  public ValidateConnectionParams(TransientSonarCloudConnectionDto transientConnection) {
    this(Either.forRight(transientConnection));
  }

  public Either<TransientSonarQubeConnectionDto, TransientSonarCloudConnectionDto> getTransientConnection() {
    return transientConnection;
  }
}
