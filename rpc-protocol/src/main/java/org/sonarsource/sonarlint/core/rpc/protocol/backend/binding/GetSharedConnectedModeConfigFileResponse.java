/*
ACR-0458932192fb495c9588d8ecfb7742e6
ACR-a575b5c435db45aa97dd2f5c90dc3610
ACR-1eb3fbb224624d3babdc2b6bbe9afd3d
ACR-14d34f4359d342b88b7dc465721da0e5
ACR-53c4e161635e4c00ba8f110a0861abfe
ACR-a2cda5aefa33423b8ac11020ed55d504
ACR-f9d31659b1eb47a28fd54c8598c63e75
ACR-923ef9f01b3444d1a8e76156a5f19187
ACR-1d977acc708142c79cbdbe4752d8c6f3
ACR-f5eedb1743c7412d9730cc8875b27ddd
ACR-16b5726eb2a841e7b003c01b1886288b
ACR-34db620860fd479c8865a070b937773d
ACR-71562bcb328c4244a7710021dbf8e0ba
ACR-1f71cf2b12c249da8d52695cca6d86db
ACR-20f6d45e3e624f5f90a52c65f26c631a
ACR-4f53bac1c8734f96aa81926b37842aa0
ACR-29a1d7f1e6ec4da1a1fed07ab1b6cc33
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.binding;

public class GetSharedConnectedModeConfigFileResponse {
  private final String jsonFileContent;

  public GetSharedConnectedModeConfigFileResponse(String jsonFileContent) {
    this.jsonFileContent = jsonFileContent;
  }

  public String getJsonFileContent() {
    return jsonFileContent;
  }
}
