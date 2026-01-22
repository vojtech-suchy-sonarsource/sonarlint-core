/*
ACR-5862ae83848e42af8fc9c5dae23aa8dc
ACR-85100cc00cdb454eb1cb538aa639f259
ACR-1708c294f9da49cbafaecc4398822fab
ACR-0b1990f69a7a4310b6e4b9ff3ac1d28a
ACR-740bdb9c5b5747c381cdef314ac0247f
ACR-624a6e81823d45c398228c3d619f484e
ACR-9a6cf1a3691b4476860b6b44632e8fc4
ACR-5e99d5c9b04d4d0280e73ba45db3bac6
ACR-33ee085eb6db4a97b88b400a5f6c3a07
ACR-2c36598a4c6b4dd282370f3ae47b4807
ACR-53f04bf7d9f04990ba49a7c96d40572e
ACR-278b3d3042474fca8fe9a34baf1530eb
ACR-bc5f3ef193bc4786bdc2ad691f9c034e
ACR-cbc3a46baa94445a90e99972b4571bea
ACR-2602784b7b5c49329d5df9db14bfa092
ACR-01114eecae6f414db26c29a4297e8c2a
ACR-c28e17d6efd448c3963a417a88021f63
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
