/*
ACR-fb79150a651c446d873b111b8f1fe4cb
ACR-c203217a56834d4eaf30d79ea6fdc02a
ACR-265397eeccea44f6acddaaaa34716639
ACR-83b233724d7a42cba6d672291ce65e01
ACR-625952cbe0b04b25a6eda04f9d2002b9
ACR-983f2e45672a4306a98369926a224ef5
ACR-bf58d2913ca44301afd6f10f805eb50c
ACR-8ebfc947943547afbbf08b610a1c3c77
ACR-967293285f1e4ae0b7af37842635b2cc
ACR-cb9dd32786b3458eab76dddd396022be
ACR-9888a160423c4a0b9230e8fc7c84ab19
ACR-a8a6188a913c4ac88577bcf3004aac2b
ACR-9c7eaab502bc4992a3c3bdfb7a15ed84
ACR-f526acd52cc24a77a156dc15d20b0e40
ACR-3ff78ded2e874537bcd081fbe2b54a87
ACR-d5123fd422b449c4a77ed9acc36d8074
ACR-7027abc1c98541bc9ef25a4a55fc4c85
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.hotspot;

public class ChangeHotspotStatusParams {
  private final String configurationScopeId;
  private final String hotspotKey;
  private final HotspotStatus newStatus;

  public ChangeHotspotStatusParams(String configurationScopeId, String hotspotKey, HotspotStatus newStatus) {
    this.configurationScopeId = configurationScopeId;
    this.hotspotKey = hotspotKey;
    this.newStatus = newStatus;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public String getHotspotKey() {
    return hotspotKey;
  }

  public HotspotStatus getNewStatus() {
    return newStatus;
  }
}
