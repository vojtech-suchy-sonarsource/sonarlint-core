/*
ACR-11365e437eb440c3918c7805a0eb4e60
ACR-68dd316ac46246baa9bb36cd10f8038a
ACR-480efa6f09eb42249d411c9ffe1b5216
ACR-f96e694684c64aa8ba2bec691060c9f2
ACR-9b2bd14672804a309d427d340634a6d3
ACR-bbd1463d2c844f2ca6cd7da8558d4e42
ACR-f9f3bec461354be6b880e13cff0dd420
ACR-ce4dcba113f34ccfb2f0ef3d7d1e9f68
ACR-b5bc9bdf8803490d825e884b30028e66
ACR-94b1da0bcfdf4b8aae09931d1330add9
ACR-e6f6ce0302cd4ccfbd0827acd70d2cf1
ACR-67ef69b107424a2eb51b7c7b92c37c27
ACR-04debc715399448e88295cacad2073f6
ACR-6678c44e96c549b19d8ca339893e8762
ACR-52117034fd604ea381240d771bda3e24
ACR-e6ddeffe591a4a1eb166e30ffe4677bc
ACR-6632582cc8ef4321b5e902f0de694db7
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.issue;

public class GetEffectiveIssueDetailsResponse {
  private final EffectiveIssueDetailsDto details;

  public GetEffectiveIssueDetailsResponse(EffectiveIssueDetailsDto details) {
    this.details = details;
  }

  public EffectiveIssueDetailsDto getDetails() {
    return details;
  }
}
