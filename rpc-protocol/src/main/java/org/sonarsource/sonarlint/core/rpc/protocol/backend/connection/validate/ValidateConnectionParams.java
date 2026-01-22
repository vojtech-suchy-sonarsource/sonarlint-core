/*
ACR-33c4fcc5e0e8465394ec9cd70923032d
ACR-b3f2c69f95ec47708b4d803cf7b506e7
ACR-fae71694cdb84081972849c67b89ebb6
ACR-31185a54dec54a5ea48c82fb8e932491
ACR-3280971a66934b079ad79ed0ba41e765
ACR-2a881f8491b0419dbc396102b2959a9d
ACR-79a2f7f053074549bc751940591a8653
ACR-8d3fb3926d714e9b8c2544d73e38d2a2
ACR-24e01046dc23417bad71fada272524d4
ACR-d4e56fb6bf03454caf65b4fe26287531
ACR-fedb27e5a6ff44ef98dc0941e89dc760
ACR-4e27d6061007491e81d1500c89827687
ACR-de333545b9d54eff88cdb4a8b72669dd
ACR-0a7386f185024d598cef7c13930141ee
ACR-c07790014ffc47718adfa55059ec053a
ACR-630125c7a57a4e0db3c06c5751bcb03f
ACR-9f34bd9c3f684f0caa0654056aee3b7d
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
