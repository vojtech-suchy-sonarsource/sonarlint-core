/*
ACR-8f96ad5560a147e5be7c2901e2181db2
ACR-b916da5dfc1f4c2cb9ad9181bfdd2023
ACR-2c7a54850a1d45ccb4dacc7fe710359d
ACR-fbe382f1e191416ca9e77fc1cc45e433
ACR-a48af48ad45644cb98b0ea6c26f08369
ACR-07938685e97245e48e555c17fd1d5d5d
ACR-7f2fc658981e4b75892cad02903556d3
ACR-0277958d2b56439a80440be477adf219
ACR-48b6c2b0977d4f3abdd022d9462cd7cf
ACR-c431095f518048c5a33ac06b18e33dc3
ACR-24091918e951477887f9e349ce32947b
ACR-896a9b1c198d4422abc8585af6691527
ACR-6bcd7ad898114fe596e5b406f13f851e
ACR-1862d3528186489d92ae84a4910a0cdc
ACR-be442c34cde4424d8028070189389b1c
ACR-a00100933b61429b9784431e21fd0af2
ACR-d398db8e95754d6d9f606221420ec02b
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.newcode;

public class GetNewCodeDefinitionResponse {

  private final String description;

  private final boolean isSupported;

  public GetNewCodeDefinitionResponse(String description, boolean isSupported) {
    this.description = description;
    this.isSupported = isSupported;
  }

  public String getDescription() {
    return description;
  }

  public boolean isSupported() {
    return isSupported;
  }
}
