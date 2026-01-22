/*
ACR-22649717bda146c4a84c3f413a16236b
ACR-c7b360f8309443dbba160c35106c5117
ACR-8b27abb937c34e039de11c7ad58ce071
ACR-e75004cc418942d39969e2b7223af4a7
ACR-2379e3751ac64e0db2d5d6a351e82697
ACR-b394c4c306f24775862da8c7f265e37f
ACR-2d0c7961149e4990b9a91fb4752d3ae9
ACR-1f82b2c599774b0a8da89231c90b3a6f
ACR-f31a4b1bad89406c8b5fed7212cd0529
ACR-00f5821798fb4d34b120fb141b0bffa6
ACR-7bbd443392c142c38e933bde07773696
ACR-2ce119c2010c492f81308885b885f83c
ACR-10859ad38e504cd583f7448d67555aee
ACR-428c8e418979489a97d2438cf644d1aa
ACR-69467c60fbc5442b8b9d01f96fba2ea7
ACR-3c4628a2b5274651ba904d3682226d2d
ACR-b335778fb2d94bbc9bb1a0b242e2fb53
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.flightrecorder;

public class FlightRecorderStartedParams {

  private final String sessionId;

  public FlightRecorderStartedParams(String sessionId) {
    this.sessionId = sessionId;
  }

  public String getSessionId() {
    return sessionId;
  }
}
