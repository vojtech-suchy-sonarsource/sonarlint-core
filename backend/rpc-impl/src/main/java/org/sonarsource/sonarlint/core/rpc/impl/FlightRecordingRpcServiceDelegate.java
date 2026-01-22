/*
ACR-7de3ec431d284b74a5383707c30a1688
ACR-597f80bf4ae0441cb2ed9a3a6623e64f
ACR-2e852744b83e42febe7bc6e546b08514
ACR-998cb8a98ff84392929f74c6775f668e
ACR-8728c717de71420b9d38a87222ec867b
ACR-83406075c3d74c7eab81c0beaab394f7
ACR-580c8bcc49d14d42a45dbc57ef23e96a
ACR-e75abc078d994759b338fddfccf65ee5
ACR-56ecf67605f44aafbb6cee5f1928e66e
ACR-db8c0bd6ed1c4e6181588f56cc697f5c
ACR-6b6ff1d08b404ef6a4ea313ad51e0d97
ACR-5e5a27a56adf45a6b7281e9b3cf8e2b0
ACR-d6b3031ff8f44a02b7a747168d274a13
ACR-ff340ab90fbb494ca09d944fdea2e122
ACR-2124143951ed4f2791d2862736946e5e
ACR-2254f50e60ac4c0183328bb08ff4654c
ACR-b795401a444c4b5db5482433c80e99f1
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import org.sonarsource.sonarlint.core.flight.recorder.FlightRecorderService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.flightrecorder.FlightRecordingRpcService;

public class FlightRecordingRpcServiceDelegate extends AbstractRpcServiceDelegate implements FlightRecordingRpcService {

  public FlightRecordingRpcServiceDelegate(SonarLintRpcServerImpl server) {
    super(server);
  }

  @Override
  public void captureThreadDump() {
    notify(() -> getBean(FlightRecorderService.class).captureThreadDump());
  }

}
