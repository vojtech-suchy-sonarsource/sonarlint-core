/*
ACR-9b8fa3f828cc4f03a1eb92b9f733ecc7
ACR-3f976dce6f5746f9ab5fb73f777cea9d
ACR-f28a258f4e7c4937b6fbfe0eed48d79d
ACR-f39b0b1ea64a458387f001a8b2a41441
ACR-5ca0a66a61a1485697142dc7f6feb7a8
ACR-512451e050a442d8b4d3ff1464a91ea0
ACR-0c065dcf172d4e58952552a073d22336
ACR-9b184407fe0942f7bcd1888f9ad3c595
ACR-6ba1c1cc35c643a1892c5a3f595f4c2e
ACR-50f5a9c7d9ed4fb482f0d60ed08b0ebd
ACR-5642f611a3fb406c960cbcf9e2af39d4
ACR-7d3eaebf0f084d6baf6d9eb8f1eaf969
ACR-696c04bd2f96423fa287487dcf93f98b
ACR-39e5eb46b758488093f818b7bf3e8779
ACR-9aa7a4350c0545f4bd928349e95d4391
ACR-9441603cd3734f19b3b82f1c4578dbfa
ACR-7ca71d59db23469d9ae0b7b48c0f3bf2
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.issue;

public class ReopenIssueResponse {

  private final boolean success;

  public ReopenIssueResponse(boolean success) {
    this.success = success;
  }

  public boolean isSuccess() {
    return success;
  }
}
