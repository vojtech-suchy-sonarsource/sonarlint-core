/*
ACR-ce015a79bb9048c4b499b46cb6fdeda5
ACR-5e746961f2f844e99c8f89669c22280f
ACR-7f7709d3b670407baef7b2169291f3f7
ACR-295cd7f956c34b29ae8631842b861e1c
ACR-ce7c9e031de949498dbf76c01340f280
ACR-50f4aac76ec04a56b5c4834343346541
ACR-24396791818243c6a5b7dbd3c0202d94
ACR-f4bb9f68c02c41768888885dad2de7b3
ACR-e0e27682d9c34e3291ac1a3c73f2a8bf
ACR-658b20b7212e4a6d94c6b41d6b81dc6a
ACR-09814f5e14124d5785af077bd33e136d
ACR-b81eefde578c4a42866bb28060a737e5
ACR-6825a89079154c45a40892aa013f9113
ACR-ee3f826628c64c07a4a39206c1516672
ACR-e0c60b5e3bc044ae8202c86f3eade341
ACR-10931b8539e647df88bb5abc522f7b01
ACR-d05c653f76fd4ba7a729f09abd804608
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.ai;

public class GetRuleFileContentResponse {
  private final String content;

  public GetRuleFileContentResponse(String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }
}
