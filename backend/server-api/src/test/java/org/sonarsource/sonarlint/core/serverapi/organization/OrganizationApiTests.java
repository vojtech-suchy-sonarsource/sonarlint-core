/*
ACR-1c7a804f00ab41309890ada80ca19601
ACR-dd3db982fc6f4efaa446151c2be1f50c
ACR-2d3dc13124b44865b9f965509062aeb4
ACR-4c523bc6ce854e75bb98ac3bd7ef10ff
ACR-b9f341fffdb14497b5a13fb40f1d3020
ACR-edfb771c574e44a2b58ca9f343988045
ACR-c6088e3a80484089be9be2512696e9cc
ACR-d98f77e8b35d4176a1bdcfb24e867dd3
ACR-68ded71443a04853beed77880deebc46
ACR-c2605eabc2aa47908d956a1a32633109
ACR-5a90e98d887543e7ad99dedae11765a9
ACR-4ae0dd7ba61d4ac59f3f7664a405f734
ACR-a10167b607984210bc56975857cdf277
ACR-27efd6bc42de4b81a085c682c7e91610
ACR-b55da977f3f1493e8c947e3f090b36f7
ACR-5daac388e65548618132166b153d7d5b
ACR-d6fbe96e5eda41e091f9474f38d0fadb
 */
package org.sonarsource.sonarlint.core.serverapi.organization;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.http.HttpClientProvider;
import org.sonarsource.sonarlint.core.serverapi.MockWebServerExtensionWithProtobuf;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;
import org.sonarsource.sonarlint.core.serverapi.exception.UnexpectedBodyException;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarcloud.ws.Organizations;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarcloud.ws.Organizations.Organization;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarcloud.ws.Organizations.SearchWsResponse;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Common.Paging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class OrganizationApiTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  @RegisterExtension
  static MockWebServerExtensionWithProtobuf mockServer = new MockWebServerExtensionWithProtobuf();

  @Test
  void testListUserOrganizationWithMoreThan20Pages() {
    var underTest = new OrganizationApi(new ServerApiHelper(mockServer.endpointParams("myOrg"), HttpClientProvider.forTesting().getHttpClient()));

    for (var i = 0; i < 21; i++) {
      mockOrganizationsPage(i + 1, 10500);
    }

    var orgs = underTest.listUserOrganizations(new SonarLintCancelMonitor());

    assertThat(orgs).hasSize(10500);
  }

  @Test
  void should_search_organization_details() {
    mockServer.addProtobufResponse("/api/organizations/search.protobuf?organizations=org%3Akey&ps=500&p=1", SearchWsResponse.newBuilder()
      .addOrganizations(Organization.newBuilder()
        .setKey("orgKey")
        .setName("orgName")
        .setDescription("orgDesc")
        .build())
      .build());
    mockServer.addProtobufResponse("/api/organizations/search.protobuf?organizations=org%3Akey&ps=500&p=2", SearchWsResponse.newBuilder().build());
    var underTest = new OrganizationApi(new ServerApiHelper(mockServer.endpointParams(), HttpClientProvider.forTesting().getHttpClient()));

    var organization = underTest.searchOrganization("org:key", new SonarLintCancelMonitor());

    assertThat(organization).hasValueSatisfying(org -> {
      assertThat(org.getKey()).isEqualTo("orgKey");
      assertThat(org.getName()).isEqualTo("orgName");
      assertThat(org.getDescription()).isEqualTo("orgDesc");
    });
  }

  @Test
  void should_get_organization_by_key() {
    mockServer.addStringResponse("/organizations/organizations?organizationKey=org%3Akey&excludeEligibility=true", """
      [{
        "id": "orgId",
        "uuidV4": "f9cb252d-9f81-4e40-8b77-99fa13190b74"
      }]
      """);
    var underTest = new OrganizationApi(new ServerApiHelper(mockServer.endpointParams("org:key"), HttpClientProvider.forTesting().getHttpClient()));

    var organization = underTest.getOrganizationByKey(new SonarLintCancelMonitor());

    assertThat(organization)
      .isEqualTo(new GetOrganizationsResponseDto("orgId", UUID.fromString("f9cb252d-9f81-4e40-8b77-99fa13190b74")));
  }

  @Test
  void should_throw_if_get_organization_by_key_is_malformed() {
    mockServer.addStringResponse("/organizations/organizations?organizationKey=org%3Akey&excludeEligibility=true", """
      [{
        "id": "orgId",
        "uuidV4": "f9cb252d-
      """);
    var underTest = new OrganizationApi(new ServerApiHelper(mockServer.endpointParams("org:key"), HttpClientProvider.forTesting().getHttpClient()));

    var throwable = catchThrowable(() -> underTest.getOrganizationByKey(new SonarLintCancelMonitor()));

    assertThat(throwable).isInstanceOf(UnexpectedBodyException.class);
  }

  private void mockOrganizationsPage(int page, int total) {
    List<Organization> orgs = IntStream.rangeClosed(1, 500)
      .mapToObj(i -> Organization.newBuilder().setKey("org_page" + page + "number" + i).build())
      .toList();

    var paging = Paging.newBuilder()
      .setPageSize(500)
      .setTotal(total)
      .setPageIndex(page)
      .build();
    var response = Organizations.SearchWsResponse.newBuilder()
      .setPaging(paging)
      .addAllOrganizations(orgs)
      .build();
    mockServer.addProtobufResponse("/api/organizations/search.protobuf?member=true&ps=500&p=" + page, response);
  }

}
