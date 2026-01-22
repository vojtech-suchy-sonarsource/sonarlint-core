/*
ACR-4722c14a15ae42e8ac7d3569c7276129
ACR-631ad98318194cd18f0fc1e5d2e34246
ACR-2522530ac9194926b86207b844541aa6
ACR-8be947f07cbe45a5ae10bee9599d1415
ACR-fef38410975e4176b8e4556860a0fd03
ACR-31ef435b349a48549f79a671f24faf24
ACR-82b995bbde584c85a450de5784612240
ACR-70c56c8ea0ca4034acabea9a54d416dc
ACR-fcb54effac514d94b55da54b3a378b96
ACR-4c8940c177c74c11924ba4b22238433b
ACR-f8771a86f20c430d898ab51f2ddaa917
ACR-6c841c15a2f6499a9ae4b0507d905f30
ACR-bef185efe4964a81a1eaf950041dc767
ACR-10c4e80da4534fcd92b1cfb9d5e32376
ACR-e02c0956b2b84174b712335772750c1f
ACR-4aa6604e351544089bd250b972a4a46a
ACR-59df66d2825048ef8e27ef6347b2141b
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope;


public class DidRemoveConfigurationScopeParams {
  private final String removedId;

  public DidRemoveConfigurationScopeParams(String removedId) {
    this.removedId = removedId;
  }

  public String getRemovedId() {
    return removedId;
  }
}
