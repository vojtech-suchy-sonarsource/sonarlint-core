/*
ACR-dc9c839e5b54422ea4be1d41c34c389b
ACR-1db3d73ddff84dfa8a367dd13f816676
ACR-7fccbe7775674746b2a6113ead24745c
ACR-23a419c52395422aa51f5e59b524ef15
ACR-1ab44871dc024c19a000ee3d35caa9a3
ACR-a0490715b7d648e182416148a879c42b
ACR-f7d27e06e9d649719baa68565e8e2488
ACR-42259c1c1c354e8dbdfa07ae7e9e493c
ACR-7dba30857e4c434a9a7b0b080b5ca3be
ACR-694129ba0ee44f4fad5cda46cf829d65
ACR-c392e51204ce496daffe97a45ece8159
ACR-3b1a1c996cd144828e75f6920c4c8634
ACR-16807460c8db40449cc4aeada3923668
ACR-7359c54f295a4aa9afb1a13b8aa32662
ACR-f708bcec912d4a88bf36bedca4567317
ACR-c5342f4a7694428a928208d91a38b292
ACR-1c71de9839d14c208b5f6a81d053ce2a
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
