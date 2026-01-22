/*
ACR-6361454b0ec4418c9de62233f430d4c8
ACR-b5f0dac474014c8ba18f3a50fb353dad
ACR-a6c6b27a39804bd7b486c139950d46da
ACR-707d17c37ecd456f941a6aa01b54e5bb
ACR-fe1606df64c7436d9e007cbf00ddbc13
ACR-b0fa02242acd4e5fa3cf6f6b9a95c545
ACR-ac5609ef1a4c4c3e9d731edf1eb3d237
ACR-5b83d4caaac142eca2b4e0a7307f21f6
ACR-ce078c33fb3d4c8996c1fd1658581edf
ACR-76d45f9d59da4601bdbfc4a24d5bbd9d
ACR-b947bfa7142e4411b1a4eb06df5dab4f
ACR-f30d871385a143dc9957ba1ff5201a98
ACR-942d9f84ed18416b80e85db1d69ead78
ACR-aee917fe91014677b634a850b75851c8
ACR-df3e435c633943818efa5742fcc3cfbe
ACR-52a6976400e24692ab1e8f6d1255f7c5
ACR-3cab5f25984c425481ab926cf4605a63
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
