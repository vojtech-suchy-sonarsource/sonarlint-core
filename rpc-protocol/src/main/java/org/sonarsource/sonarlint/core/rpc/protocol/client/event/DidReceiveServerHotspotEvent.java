/*
ACR-3e2e707296ed450eaac558e288e8ce0b
ACR-c5db795d419343f78bb09e60a5970588
ACR-349bbf2ab1a24b47ac34e6f4e74e148c
ACR-bf8f331393e54ec5a648c9b77600eff5
ACR-2e283263f505493d8f163c801882ae76
ACR-2ec34c099d124a0f90ee13d3fc3674f7
ACR-554813dafab443c6849ed1544e6954df
ACR-28052eaf69454519a61d1f15fddf01c5
ACR-cf2516c2334d40628eba61abfb0917e5
ACR-ad6158d640394c6ebc9dd5f46c286f21
ACR-5a2925a0bf1a4868b8f85f18acf22aca
ACR-63057c11d1f44ac6b02fb6a415c3e12f
ACR-a76e6600ae3b460f83df43ea44c550b1
ACR-574ddc892ae341fe80beafa78098c094
ACR-c7709ef767c5446ab54ce97a3b3dd14d
ACR-9e591269f42247fd828a3656f2eded94
ACR-80e5ae35e981444993d1f3348c2387d8
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.event;

import java.nio.file.Path;

public class DidReceiveServerHotspotEvent {

  private final String connectionId;
  private final String sonarProjectKey;
  private final Path ideFilePath;

  public DidReceiveServerHotspotEvent(String connectionId, String sonarProjectKey, Path serverFilePath) {
    this.connectionId = connectionId;
    this.sonarProjectKey = sonarProjectKey;
    this.ideFilePath = serverFilePath;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public String getSonarProjectKey() {
    return sonarProjectKey;
  }

  public Path getIdeFilePath() {
    return ideFilePath;
  }
}
