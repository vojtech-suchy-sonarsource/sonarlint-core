/*
ACR-3cb460f6ca834f04971f998c5f533568
ACR-9f11583844604dcf8db39d2471663576
ACR-95aa507ea5de48e4a403791fc25778ba
ACR-a4f85733c7aa41d598ba5830fa9e7c7e
ACR-dbe866844baa4811ac5eabf7e019e1fc
ACR-66e37e09b9c242e0b072f69381da1bbe
ACR-c04e497da3ec44fea585087b48423651
ACR-476cd61839bd4f8696a72041057a33ad
ACR-201f069132994fcb8fef980c7739cfc0
ACR-fe9c2bb7bd3b4de499603a45cc9859a1
ACR-4bc597f090ec42c6b14f7d53d95b5f4a
ACR-feb233dff6da433b951fafd92027c19b
ACR-3aebcc7f8b7c429f913aa8057f2662a2
ACR-8771636a7152418fa0a4b9cf2a3adebc
ACR-ecb1c36912974b5b959a384522bc7460
ACR-7ab9a758001e441abdc963f634d2b6b0
ACR-43d5ca7e6dce456d9c7765e1d357beb7
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
