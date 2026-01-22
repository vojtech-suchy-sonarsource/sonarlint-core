/*
ACR-4317e3b14bf048c682b1a9383c2c6a91
ACR-4103e82af01344128641078cff1804d4
ACR-6b73d174056b48bba87d3f8347c2c28d
ACR-496f474495ea41ad9661824200be169c
ACR-f5cabc4e77d54814b8135bdd1d0543d1
ACR-3cde072811c541bb8301a7ce9df3d87b
ACR-cf9d0fd71c4e446f8796a6722932aa05
ACR-99972b360e3e4f268a79117b66c40056
ACR-bd90d99d1473460fa57dbec2ffd4732b
ACR-1e72f5de2ae54b5ea24a57e403f24224
ACR-48a7d986373145fd9685ca82dcd35c64
ACR-6f9cf91c32964980aa722a2f2f439e7b
ACR-9fa020aa79ae434aa629fff5b1642707
ACR-9b3ecdf1869e49259aa772482a6ed3f6
ACR-7b975aab9dd74d40aac5b580f4fd3c96
ACR-67d640675a9f49ebaf09aca6a645d804
ACR-b5a3ac610fb2444baf65facd45b2ea38
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.file;

import java.net.URI;
import java.util.Map;

public class GetFilesStatusResponse {

  private final Map<URI, FileStatusDto> fileStatuses;

  public GetFilesStatusResponse(Map<URI, FileStatusDto> fileStatuses) {
    this.fileStatuses = fileStatuses;
  }

  public Map<URI, FileStatusDto> getFileStatuses() {
    return fileStatuses;
  }
}
