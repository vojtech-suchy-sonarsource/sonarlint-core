/*
ACR-0462d951bbca4acb97268c3c9144b1a0
ACR-961654c9d6434528af39c69d0ebb973e
ACR-aa880cb5f5a84affbddc8ec73079ffa7
ACR-6d838711effd49f7821b07201fc9057f
ACR-b4d75d35433b492ab62341c191a3c927
ACR-9f41ab1d867d4860bae7e2ebb31a82b4
ACR-f190980dae80481b83378a3277edf001
ACR-e8df69b9665e4d3fa4150234f71328eb
ACR-83812c4c15884714904ffb92224773e5
ACR-468c5cabb331476dac9ea02019aa421f
ACR-eb9a5fbce8514af6ba9460dddf06a32f
ACR-ef2d31e5910d49fab0391028423fba49
ACR-6ed550ead40f4d528330c3934ecd4827
ACR-8dd5a30fa28846abac170af3e2f56a7e
ACR-6a2096ff2ed042bb8d24d3df6d371e4f
ACR-64e68efcae5a4208827d788491c4fdea
ACR-799ab5bf1ed149089dbca6555e62042e
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
