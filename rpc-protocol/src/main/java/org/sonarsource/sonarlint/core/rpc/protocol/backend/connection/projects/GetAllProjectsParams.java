/*
ACR-9044660a5ccb4a999a01c0d2a484aedc
ACR-6ae1466bfe7b4618a72d20613a7568ce
ACR-9896b2858aa64696826811aa26dec345
ACR-9567e585d13a41e9b08e071bfd97e455
ACR-9291aad133e6445eb94891486991da7e
ACR-acf7e8d2336c4f9c8365e81d259ae86f
ACR-d1cf918b7a204080b735c99f5ff794a3
ACR-54da65daccaf403f81a9815e9b8aa85e
ACR-c7d5d4865c994a60ae014bbfd3ec4868
ACR-7f60a62f6eb346769507c47a88d82499
ACR-19f4af9a5e024d1f95a310280d428040
ACR-d47aca01dba04b53b6e33153ca2fc148
ACR-bbb2a3d91eaf4938b7f5e0b98c100aed
ACR-55a8a668a0a347e4ae9dc69064c3740c
ACR-9b7fc87521374ec4a9b32ebd7841f7e1
ACR-90d0ae63295b4afe890c2021ab71156f
ACR-cb08ec78d9ae40fcb15f1fec073c051a
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.projects;

import com.google.gson.annotations.JsonAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.adapter.EitherTransientConnectionAdapterFactory;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.common.TransientSonarCloudConnectionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.common.TransientSonarQubeConnectionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;

public class GetAllProjectsParams {

  @JsonAdapter(EitherTransientConnectionAdapterFactory.class)
  private final Either<TransientSonarQubeConnectionDto, TransientSonarCloudConnectionDto> transientConnection;

  public GetAllProjectsParams(Either<TransientSonarQubeConnectionDto, TransientSonarCloudConnectionDto> transientConnection) {
    this.transientConnection = transientConnection;
  }

  public GetAllProjectsParams(TransientSonarQubeConnectionDto transientConnection) {
    this(Either.forLeft(transientConnection));
  }

  public GetAllProjectsParams(TransientSonarCloudConnectionDto transientConnection) {
    this(Either.forRight(transientConnection));
  }

  public Either<TransientSonarQubeConnectionDto, TransientSonarCloudConnectionDto> getTransientConnection() {
    return transientConnection;
  }
}
