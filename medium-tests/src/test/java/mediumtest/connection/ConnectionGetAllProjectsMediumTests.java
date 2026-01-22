/*
ACR-c8e4af303a9c4d2da605590a0d2e6fa8
ACR-ce17cf7677a547e78cba85aaff9a2d64
ACR-c3ae02cdc2b7465c87ffecdf1be5b4a9
ACR-cccfdbf1b7314bffb02701fe0f56dddc
ACR-e08be2d73ce549fa9b8390ac5deee20c
ACR-687f44ddd5874184a63f799521ca1c26
ACR-281f53961a2b4dc3a8e0a05edd6ceabd
ACR-ed93ef900fbf4f8b866b6be07195aa23
ACR-f4ccfe35311c4edead2cd3137c3ebeb4
ACR-f97a51a9321c4f80972ae8c1ac882693
ACR-88f18c5db9904fa285f30be247b8c3a3
ACR-46b1ec35ee464018a2362df964f66e4e
ACR-ec1b3c1e770149f087d79454e4762fbf
ACR-1f9f4962f8ad4acfbee141deed521b83
ACR-0751d1303e3f4ac2a06f2db3f03257a5
ACR-c9ab694da7f8401896accd5a5de6fcff
ACR-e5c583b5a1cf485e871fbc594edf85e0
 */
package mediumtest.connection;

import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.common.TransientSonarCloudConnectionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.common.TransientSonarQubeConnectionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.projects.FuzzySearchProjectsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.projects.GetAllProjectsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.projects.GetAllProjectsResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.projects.SonarProjectDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.SonarCloudRegion;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TokenDto;
import org.sonarsource.sonarlint.core.test.utils.SonarLintTestRpcServer;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.awaitility.Awaitility.await;

class ConnectionGetAllProjectsMediumTests {

  @SonarLintTest
  void it_should_return_an_empty_response_if_no_projects_in_sonarqube(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer().start();
    var backend = harness.newBackend().start();

    var response = getAllProjects(backend, new TransientSonarQubeConnectionDto(server.baseUrl(), Either.forLeft(new TokenDto(null))));

    assertThat(response.getSonarProjects()).isEmpty();
  }

  @SonarLintTest
  void it_should_return_an_empty_response_if_no_projects_in_sonarcloud_organization(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarCloudServer()
      .withOrganization("myOrg")
      .start();
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(server.baseUrl())
      .start();

    var response = getAllProjects(backend, new TransientSonarCloudConnectionDto("myOrg", Either.forLeft(new TokenDto("token")), SonarCloudRegion.EU));

    assertThat(response.getSonarProjects()).isEmpty();
  }

  @SonarLintTest
  void it_should_return_the_list_of_projects_on_sonarqube(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer()
      .withProject("projectKey1", project -> project.withName("MyProject1"))
      .withProject("projectKey2", project -> project.withName("MyProject2"))
      .start();
    var backend = harness.newBackend().start();

    var response = getAllProjects(backend, new TransientSonarQubeConnectionDto(server.baseUrl(), Either.forLeft(new TokenDto("token"))));

    assertThat(response.getSonarProjects())
      .extracting(SonarProjectDto::getKey, SonarProjectDto::getName)
      .containsOnly(tuple("projectKey1", "MyProject1"), tuple("projectKey2", "MyProject2"));
  }

  @SonarLintTest
  void it_should_fuzzy_search_for_projects_on_sonarqube(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer()
      .withProject("mycompany:project-foo1", project -> project.withName("My Company Project Foo 1"))
      .withProject("mycompany:project-foo2", project -> project.withName("My Company Project Foo 2"))
      .withProject("mycompany:project-bar", project -> project.withName("My Company Project Bar"))
      .start();
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", server.baseUrl())
      .start();

    var emptySearch = backend.getConnectionService().fuzzySearchProjects(new FuzzySearchProjectsParams("connectionId", "")).join();
    assertThat(emptySearch.getTopResults())
      .isEmpty();

    var searchMy = backend.getConnectionService().fuzzySearchProjects(new FuzzySearchProjectsParams("connectionId", "My")).join();
    assertThat(searchMy.getTopResults())
      .extracting(SonarProjectDto::getKey, SonarProjectDto::getName)
      .containsExactly(
        tuple("mycompany:project-bar", "My Company Project Bar"),
        tuple("mycompany:project-foo1", "My Company Project Foo 1"),
        tuple("mycompany:project-foo2", "My Company Project Foo 2"));

    var searchFooByName = backend.getConnectionService().fuzzySearchProjects(new FuzzySearchProjectsParams("connectionId", "Foo")).join();
    assertThat(searchFooByName.getTopResults())
      .extracting(SonarProjectDto::getKey, SonarProjectDto::getName)
      .containsExactly(
        tuple("mycompany:project-foo1", "My Company Project Foo 1"),
        tuple("mycompany:project-foo2", "My Company Project Foo 2"));

    var searchBarByKey = backend.getConnectionService().fuzzySearchProjects(new FuzzySearchProjectsParams("connectionId", "project-bar")).join();
    assertThat(searchBarByKey.getTopResults())
      .extracting(SonarProjectDto::getKey, SonarProjectDto::getName)
      .containsExactly(
        tuple("mycompany:project-bar", "My Company Project Bar"));
  }

  @SonarLintTest
  void it_should_return_the_list_of_projects_on_sonarcloud(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarCloudServer()
      .withOrganization("myOrg", organization -> organization
        .withProject("projectKey1", project -> project.withName("MyProject1"))
        .withProject("projectKey2", project -> project.withName("MyProject2")))
      .start();
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(server.baseUrl())
      .start();

    var response = getAllProjects(backend, new TransientSonarCloudConnectionDto("myOrg", Either.forLeft(new TokenDto("token")), SonarCloudRegion.EU));

    assertThat(response.getSonarProjects())
      .extracting(SonarProjectDto::getKey, SonarProjectDto::getName)
      .containsOnly(tuple("projectKey1", "MyProject1"), tuple("projectKey2", "MyProject2"));
  }

  @SonarLintTest
  void it_should_support_cancellation(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer().start();
    server.getMockServer().stubFor(get("/api/components/search.protobuf?qualifiers=TRK&ps=500&p=1").willReturn(aResponse()
      .withStatus(200)
      .withFixedDelay(2000)));
    var client = harness.newFakeClient().build();
    var backend = harness.newBackend().start(client);

    var connectionDto = new TransientSonarQubeConnectionDto(server.baseUrl(), Either.forLeft(new TokenDto(null)));

    var future = backend.getConnectionService().getAllProjects(new GetAllProjectsParams(connectionDto));
    await().untilAsserted(() -> server.getMockServer().verify(getRequestedFor(urlEqualTo("/api/components/search.protobuf?qualifiers=TRK&ps=500&p=1"))));

    future.cancel(true);

    await().untilAsserted(() -> assertThat(client.getLogMessages()).contains("Request cancelled"));
  }

  @SonarLintTest
  void it_should_throw_ResponseErrorException_when_getAllProjects_unauthorized(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer().start();
    server.getMockServer().stubFor(get("/api/components/search.protobuf?qualifiers=TRK&ps=500&p=1").willReturn(aResponse()
      .withStatus(401)
      .withBody("Unauthorized")));
    var backend = harness.newBackend().start();

    var connectionDto = new TransientSonarQubeConnectionDto(server.baseUrl(), Either.forLeft(new TokenDto("invalid-token")));

    var future = backend.getConnectionService().getAllProjects(new GetAllProjectsParams(connectionDto));

    assertThatThrownBy(future::join)
      .hasCauseInstanceOf(ResponseErrorException.class);
  }

  private GetAllProjectsResponse getAllProjects(SonarLintTestRpcServer backend, TransientSonarQubeConnectionDto connectionDto) {
    return backend.getConnectionService().getAllProjects(new GetAllProjectsParams(connectionDto)).join();
  }

  private GetAllProjectsResponse getAllProjects(SonarLintTestRpcServer backend, TransientSonarCloudConnectionDto connectionDto) {
    return backend.getConnectionService().getAllProjects(new GetAllProjectsParams(connectionDto)).join();
  }

}
