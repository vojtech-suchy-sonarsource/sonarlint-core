/*
ACR-09edc77e08f144ecbcfc726dba6846cc
ACR-8c1b77261c8244698a6891fa95b24b58
ACR-6c978c157ac943769db0b22d8e244865
ACR-2ed6293be1a64a548bbe2fedca9088da
ACR-5f8d89f20ab84be49efd1f78c46a3a4a
ACR-c83a57b501724e9a83bf25d633d030f8
ACR-2404a86eadbc480192599f9d269f68e6
ACR-b140c8af5dbb4468acd23b2c97884e45
ACR-31a37994d4164d0d8f67bb305ac1a2ce
ACR-8a94432f964040a38fff398d8e7de417
ACR-a303d1d3c925489aa9ba577036c6e633
ACR-cc5a2294018e41438d8bae2f27053ccb
ACR-d3a96a9f784b4a1189141133edbf63ab
ACR-ab209e4a58ee4763b7766f5c24460627
ACR-988eacdbcb674610a6b9b1866a605d55
ACR-393f1efca05c4e5aa38a7eb4d4a99c47
ACR-c274afc21fdd419bb79e431ead693f4e
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.issue;

public class ReopenAllIssuesForFileResponse {

  private final boolean success;

  public ReopenAllIssuesForFileResponse(boolean success) {
    this.success = success;
  }

  public boolean isSuccess() {
    return success;
  }
}
