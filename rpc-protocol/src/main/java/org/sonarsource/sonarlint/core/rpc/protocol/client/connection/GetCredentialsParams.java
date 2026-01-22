/*
ACR-e592bbd9894c4d06b3e22247a9b9598c
ACR-1c40bb9fe598458da3319ac912033611
ACR-714c40b0d932472db627f6842c074aec
ACR-bf0331030a3448468d23ccfcb7d31e59
ACR-b6840bf846754ee9b34f58b0f7edda30
ACR-12b5ef8b14424ffbaecb863dcb5b1506
ACR-710ee6c8659242d59a9141dcd33b9648
ACR-6e85cc445e2948f599f46bc09245828d
ACR-e64ac0d369cb47d9ad498c06e87f5660
ACR-e080c88d3b1c4852ba9baed0776f94ee
ACR-92231ebaff59474fbfc13028c0f49864
ACR-555095d6f65f4405a0b6321713be5bde
ACR-5e84781408dc460695d0bcff14b2b0be
ACR-8af6ba1f8c2a48709ae6d6d8b7856d81
ACR-9cd779d31113442c992a087c502f79c3
ACR-5b763dbf498244a7924c3c5bda6abfac
ACR-6a2341a6311e437dbdbda7f4e8c60a0e
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.connection;

public class GetCredentialsParams {

  private final String connectionId;

  public GetCredentialsParams(String connectionId) {
    this.connectionId = connectionId;
  }

  public String getConnectionId() {
    return connectionId;
  }
}
