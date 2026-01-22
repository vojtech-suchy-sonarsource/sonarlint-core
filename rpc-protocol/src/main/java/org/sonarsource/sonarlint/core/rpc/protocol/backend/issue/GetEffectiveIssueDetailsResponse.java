/*
ACR-952260dc7e154793aef6fc0a241cc57d
ACR-fd5e5be7ca784e738c15c92ad6fd4403
ACR-cabe203fded243e89fc0824186c25f11
ACR-e065da456ecf48618b1aff5e70cdc60c
ACR-1ef59120bfe646f9b23efa4f485b7792
ACR-58693d98532741748d65ae4808bbb0c4
ACR-8d8210fe298b42c8b1a041a2bbb7bdc4
ACR-096443f0b55b437b8235ca5e35a23dd5
ACR-aef352a6655f4ef692e4dc3052da7a08
ACR-f98c3563fd4443edbb0dcd5f299bad18
ACR-32f763ceef8247dcb0f7858545bd178d
ACR-953a41d2dc554650a4d2de14285817b4
ACR-ba37830bb47b4aeeb75a2e0e17c08040
ACR-9da7766a9f2c4d79ad86c7bda6be45c3
ACR-9784f3349506404d9c56428e7d7a548d
ACR-6480f4598158496d838733aa3807ef03
ACR-0fad479d34d4496cb5fa994c7615b780
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
