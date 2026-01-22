/*
ACR-e92739e654754915aad2f981e3ba1010
ACR-a866f4065b484ff4adce6a0e082fb5ec
ACR-d3cfa4f687cd4d08a100beb3aaf8a421
ACR-020b4497c587413f89d260283e3c2d67
ACR-dcdfac86162e4d92833ce62619c4fa2f
ACR-6c5e1c4321ce41e0aaa51ca25cc964c2
ACR-9bea0bf8b8d44fffa06e005e7ae1f0f7
ACR-b85d38808d104d5aba21caee5d2c4ad8
ACR-af00978281b343b8b09215ffa224d670
ACR-3d9b3828a6614f5f9e1fd53ea27dff5a
ACR-c4a490ef10b14d9888db6888d5d9fe8f
ACR-29e367e5e2564f6fa08c50ad01d1834d
ACR-5c6aea4c66ab43068f0a8f66396246b2
ACR-e327c68470084762853fd9165168130f
ACR-4f0ebe7a5a534badad848effbfb8e444
ACR-7b07f23dc10d4710b7ebd7e66e53fdb3
ACR-e2737f7f2f15411ba4c20dad1e6104d9
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.branch;

public class MatchProjectBranchResponse {
  private final boolean isBranchMatched;

  public MatchProjectBranchResponse(boolean matchedSonarBranch) {
    this.isBranchMatched = matchedSonarBranch;
  }

  public boolean isBranchMatched() {
    return isBranchMatched;
  }
}
