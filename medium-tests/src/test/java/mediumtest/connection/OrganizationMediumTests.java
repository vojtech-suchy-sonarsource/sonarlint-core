/*
ACR-265c226ef34842229a11a40f0da76fcf
ACR-57a241e7a6a2481db386d674bb39f284
ACR-c4c6d78314c34b008e7c4aa5785cd6c0
ACR-58cc8b58e3d948e39acc2d921ea4be66
ACR-4bd8806f8954491c9e4daf15c10cc285
ACR-9868f6883b514e679103aa78548acc8c
ACR-15735051d4a74ba6a1e2cb8fd7ab2f93
ACR-78aa651314cb4fb1bdd8cba001ed5a24
ACR-385bdf0afffe4508bd9167db6ff64900
ACR-dbbe543315af40cf99b6b7e6967c0e5d
ACR-e9593582fd424c7e98342eff9407091d
ACR-56bb0daee2a0471b9d3579c842276028
ACR-2483e0e53dd14d5a8283cae2cd2a8348
ACR-aafd923a85534165960ccd942295013e
ACR-3019cbfd71484b9dbe8cf955c66adef0
ACR-925f711fb5454f60b51a79fc94e2d98e
ACR-98eb0932d31949248c1db47d937fd89e
 */
package mediumtest.connection;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.ExecutionException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.org.FuzzySearchUserOrganizationsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.org.GetOrganizationParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.org.ListUserOrganizationsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.org.OrganizationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TokenDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.UsernamePasswordDto;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarcloud.ws.Organizations;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.sonarsource.sonarlint.core.test.utils.ProtobufUtils.protobufBody;

class OrganizationMediumTests {

  @RegisterExtension
  static WireMockExtension sonarcloudMock = WireMockExtension.newInstance()
    .options(wireMockConfig().dynamicPort())
    .build();

  @SonarLintTest
  void it_should_list_empty_user_organizations(SonarLintTestHarness harness) throws ExecutionException, InterruptedException {
    var fakeClient = harness.newFakeClient()
      .build();
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(sonarcloudMock.baseUrl())
      .start(fakeClient);
    sonarcloudMock.stubFor(get("/api/organizations/search.protobuf?member=true&ps=500&p=1")
      .willReturn(aResponse().withStatus(200).withResponseBody(protobufBody(Organizations.SearchWsResponse.newBuilder()
        .build()))));

    var details = backend.getConnectionService().listUserOrganizations(new ListUserOrganizationsParams(Either.forLeft(new TokenDto("token")), SonarCloudRegion.EU));

    assertThat(details.get().getUserOrganizations()).isEmpty();
  }

  @SonarLintTest
  void it_should_list_user_organizations(SonarLintTestHarness harness) throws ExecutionException, InterruptedException {
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(sonarcloudMock.baseUrl())
      .start();
    sonarcloudMock.stubFor(get("/api/organizations/search.protobuf?member=true&ps=500&p=1")
      .willReturn(aResponse().withStatus(200).withResponseBody(protobufBody(Organizations.SearchWsResponse.newBuilder()
        .addOrganizations(Organizations.Organization.newBuilder()
          .setKey("orgKey1")
          .setName("orgName1")
          .setDescription("orgDesc1")
          .build())
        .addOrganizations(Organizations.Organization.newBuilder()
          .setKey("orgKey2")
          .setName("orgName2")
          .setDescription("orgDesc2")
          .build())
        .build()))));
    sonarcloudMock.stubFor(get("/api/system/status")
      .willReturn(aResponse().withStatus(200).withBody("{\"id\": \"20160308094653\",\"version\": \"8.0\",\"status\": " +
        "\"UP\"}")));
    sonarcloudMock.stubFor(get("/api/organizations/search.protobuf?member=true&ps=500&p=2")
      .willReturn(aResponse().withStatus(200).withResponseBody(protobufBody(Organizations.SearchWsResponse.newBuilder().build()))));

    var details = backend.getConnectionService().listUserOrganizations(new ListUserOrganizationsParams(Either.forLeft(new TokenDto("token")), SonarCloudRegion.EU));

    assertThat(details.get().getUserOrganizations()).extracting(OrganizationDto::getKey, OrganizationDto::getName, OrganizationDto::getDescription)
      .containsExactlyInAnyOrder(
        tuple("orgKey1", "orgName1", "orgDesc1"),
        tuple("orgKey2", "orgName2", "orgDesc2"));

    sonarcloudMock.verify(getRequestedFor(urlEqualTo("/api/organizations/search.protobuf?member=true&ps=500&p=1"))
      .withHeader("Authorization", equalTo("Bearer token")));
  }

  @SonarLintTest
  void it_should_get_organizations_by_key(SonarLintTestHarness harness) throws ExecutionException, InterruptedException {
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(sonarcloudMock.baseUrl())
      .start();
    sonarcloudMock.stubFor(get("/api/organizations/search.protobuf?organizations=myCustomOrg&ps=500&p=1")
      .willReturn(aResponse().withStatus(200).withResponseBody(protobufBody(Organizations.SearchWsResponse.newBuilder()
        .addOrganizations(Organizations.Organization.newBuilder()
          .setKey("myCustom")
          .setName("orgName")
          .setDescription("orgDesc")
          .build())
        .build()))));
    sonarcloudMock.stubFor(get("/api/organizations/search.protobuf?organizations=myCustomOrg&ps=500&p=2")
      .willReturn(aResponse().withStatus(200).withResponseBody(protobufBody(Organizations.SearchWsResponse.newBuilder().build()))));

    var details = backend.getConnectionService().getOrganization(new GetOrganizationParams(Either.forRight(new UsernamePasswordDto("user", "pwd")), "myCustomOrg", SonarCloudRegion.EU));

    var organization = details.get().getOrganization();
    assertThat(organization.getKey()).isEqualTo("myCustom");
    assertThat(organization.getName()).isEqualTo("orgName");
    assertThat(organization.getDescription()).isEqualTo("orgDesc");

    sonarcloudMock.verify(getRequestedFor(urlEqualTo("/api/organizations/search.protobuf?organizations=myCustomOrg&ps=500&p=1"))
      .withHeader("Authorization", equalTo("Basic " + Base64.getEncoder().encodeToString("user:pwd".getBytes(StandardCharsets.UTF_8)))));
  }

  @SonarLintTest
  void it_should_fuzzy_search_and_cache_organizations_on_sonarcloud(SonarLintTestHarness harness) {
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(sonarcloudMock.baseUrl())
      .start();
    sonarcloudMock.stubFor(get("/api/organizations/search.protobuf?member=true&ps=500&p=1")
      .willReturn(aResponse().withStatus(200).withResponseBody(protobufBody(Organizations.SearchWsResponse.newBuilder()
        .addOrganizations(Organizations.Organization.newBuilder()
          .setKey("org-foo1")
          .setName("My Company Org Foo 1")
          .setDescription("orgDesc 1")
          .build())
        .addOrganizations(Organizations.Organization.newBuilder()
          .setKey("org-foo2")
          .setName("My Company Org Foo 2")
          .setDescription("orgDesc 2")
          .build())
        .addOrganizations(Organizations.Organization.newBuilder()
          .setKey("org-bar")
          .setName("My Company Org Bar")
          .setDescription("orgDesc 3")
          .build())
        .build()))));
    sonarcloudMock.stubFor(get("/api/organizations/search.protobuf?member=true&ps=500&p=2")
      .willReturn(aResponse().withStatus(200).withResponseBody(protobufBody(Organizations.SearchWsResponse.newBuilder().build()))));

    var credentials = Either.<TokenDto, UsernamePasswordDto>forRight(new UsernamePasswordDto("user", "pwd"));
    var emptySearch = backend.getConnectionService().fuzzySearchUserOrganizations(new FuzzySearchUserOrganizationsParams(credentials, "", org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion.EU)).join();
    assertThat(emptySearch.getTopResults())
      .isEmpty();

    var searchMy = backend.getConnectionService().fuzzySearchUserOrganizations(new FuzzySearchUserOrganizationsParams(credentials, "My", org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion.EU)).join();
    assertThat(searchMy.getTopResults())
      .extracting(OrganizationDto::getKey, OrganizationDto::getName)
      .containsExactly(
        Assertions.tuple("org-bar", "My Company Org Bar"),
        Assertions.tuple("org-foo1", "My Company Org Foo 1"),
        Assertions.tuple("org-foo2", "My Company Org Foo 2"));

    var searchFooByName = backend.getConnectionService().fuzzySearchUserOrganizations(new FuzzySearchUserOrganizationsParams(credentials, "Foo", org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion.EU)).join();
    assertThat(searchFooByName.getTopResults())
      .extracting(OrganizationDto::getKey, OrganizationDto::getName)
      .containsExactly(
        Assertions.tuple("org-foo1", "My Company Org Foo 1"),
        Assertions.tuple("org-foo2", "My Company Org Foo 2"));

    var searchBarByKey = backend.getConnectionService().fuzzySearchUserOrganizations(new FuzzySearchUserOrganizationsParams(credentials, "org-bar", org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion.EU)).join();
    assertThat(searchBarByKey.getTopResults())
      .extracting(OrganizationDto::getKey, OrganizationDto::getName)
      .containsExactly(
        Assertions.tuple("org-bar", "My Company Org Bar"));

    //ACR-1ef6f1af184c4a96b13d80e063698d4c
    sonarcloudMock.verify(exactly(1), getRequestedFor(urlEqualTo("/api/organizations/search.protobuf?member=true&ps=500&p=1")));
  }

}
