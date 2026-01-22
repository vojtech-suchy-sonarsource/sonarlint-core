/*
ACR-7636042dbc224d1a90a371089d520151
ACR-112779cf9bb044f5b95d9aee0c1093e7
ACR-7bd88a5e24cb4fd5833108801951f236
ACR-af3e0422a6d4488f8ff28bd18ce86458
ACR-777f7ba965764cf9985f0e6248d86a67
ACR-711e607b99a5439cbb2da88e40b744c1
ACR-096f145aae7f4d618abe9ebb903092f2
ACR-23ce9768c25940cc8a4d01ddf68b0a0b
ACR-58632fea337148e8832228b44a061720
ACR-e1c7fdefbc474d30b54bdf1a0caac28d
ACR-673c0aaa7a7b4b88b3fffded342b40a7
ACR-3182ffc14d5d452d8c7dd49261157e17
ACR-2faaf57fe09e4f0ea299b105c5a058ff
ACR-13fff93808d94b1ea2efe1bb320c9db2
ACR-78bf59f668314c38b125beda9425ae72
ACR-7aaba8f106f0432c8c44e5e4b58d03bf
ACR-68e1629dce44420881157db9523c06f1
 */
package mediumtest.connection;

import java.util.List;
import java.util.Map;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.common.TransientSonarCloudConnectionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.common.TransientSonarQubeConnectionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.projects.GetProjectNamesByKeyParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.projects.GetProjectNamesByKeyResponse;
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
import static org.assertj.core.api.Assertions.tuple;
import static org.awaitility.Awaitility.await;

class ConnectionGetProjectNameByKeyMediumTests {

  @SonarLintTest
  void it_should_return_null_if_no_projects_in_sonarqube(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer().start();
    var backend = harness.newBackend().start();

    var response = getProjectNamesByKey(backend, new TransientSonarQubeConnectionDto(server.baseUrl(), Either.forLeft(new TokenDto("token"))),
      List.of("myProject"));

    assertThat(response.getProjectNamesByKey().entrySet()).extracting(Map.Entry::getKey, Map.Entry::getValue)
      .containsExactlyInAnyOrder(tuple("myProject", null));
  }

  @SonarLintTest
  void it_should_return_null_if_no_projects_in_sonarcloud_organization(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarCloudServer().start();
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(server.baseUrl())
      .start();

    var response = getProjectNamesByKey(backend, new TransientSonarCloudConnectionDto("myOrg", Either.forLeft(new TokenDto("token")), SonarCloudRegion.EU), List.of(
      "myProject"));

    assertThat(response.getProjectNamesByKey().entrySet()).extracting(Map.Entry::getKey, Map.Entry::getValue)
      .containsExactlyInAnyOrder(tuple("myProject", null));
  }

  @SonarLintTest
  void it_should_find_project_name_if_available_on_sonarqube(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarQubeServer()
      .withProject("project-foo1", project -> project.withName("My Company Project Foo 1"))
      .withProject("project-foo2", project -> project.withName("My Company Project Foo 2"))
      .withProject("project-foo3", project -> project.withName("My Company Project Foo 3"))
      .start();
    var backend = harness.newBackend()
      .withSonarQubeConnection("connectionId", server.baseUrl())
      .start();

    var response = getProjectNamesByKey(backend, new TransientSonarQubeConnectionDto(server.baseUrl(), Either.forLeft(new TokenDto("token"))),
      List.of("project-foo2", "project-foo3", "project-foo4"));

    assertThat(response.getProjectNamesByKey().entrySet()).extracting(Map.Entry::getKey, Map.Entry::getValue)
      .containsExactlyInAnyOrder(tuple("project-foo4", null), tuple("project-foo2", "My Company Project Foo 2"), tuple("project-foo3",
        "My Company Project Foo 3"));
  }

  @SonarLintTest
  void it_should_find_project_names_if_available_on_sonarcloud(SonarLintTestHarness harness) {
    var server = harness.newFakeSonarCloudServer()
      .withOrganization("myOrg", organization -> organization
        .withProject("projectKey1", project -> project.withName("MyProject1"))
        .withProject("projectKey2", project -> project.withName("MyProject2"))
        .withProject("projectKey3", project -> project.withName("MyProject3")))
      .start();
    var backend = harness.newBackend()
      .withSonarQubeCloudEuRegionUri(server.baseUrl())
      .start();

    var response = getProjectNamesByKey(backend, new TransientSonarCloudConnectionDto("myOrg", Either.forLeft(new TokenDto("token")), SonarCloudRegion.EU),
      List.of("projectKey2", "projectKey3", "projectKey4"));

    assertThat(response.getProjectNamesByKey().entrySet()).extracting(Map.Entry::getKey, Map.Entry::getValue)
      .containsExactlyInAnyOrder(tuple("projectKey4", null), tuple("projectKey2", "MyProject2"), tuple("projectKey3", "MyProject3"));
  }

  @SonarLintTest
  void it_should_support_cancellation(SonarLintTestHarness harness) {
    var myProjectKey = "myProjectKey";
    var server = harness.newFakeSonarQubeServer().start();
    server.getMockServer().stubFor(get("/api/components/show.protobuf?component=" + myProjectKey).willReturn(aResponse()
      .withStatus(200)
      .withFixedDelay(2000)));
    var client = harness.newFakeClient().build();
    var backend = harness.newBackend().start(client);

    var connectionDto = new TransientSonarQubeConnectionDto(server.baseUrl(), Either.forLeft(new TokenDto(null)));

    var future = backend.getConnectionService().getProjectNamesByKey(new GetProjectNamesByKeyParams(connectionDto, List.of(myProjectKey)));
    await().untilAsserted(() -> server.getMockServer().verify(getRequestedFor(urlEqualTo("/api/components/show.protobuf?component=" + myProjectKey))));

    future.cancel(true);

    await().untilAsserted(() -> assertThat(client.getLogMessages()).contains("Request cancelled"));
  }

  private GetProjectNamesByKeyResponse getProjectNamesByKey(SonarLintTestRpcServer backend, TransientSonarQubeConnectionDto connectionDto, List<String> projectKey) {
    return backend.getConnectionService().getProjectNamesByKey(new GetProjectNamesByKeyParams(connectionDto, projectKey)).join();
  }

  private GetProjectNamesByKeyResponse getProjectNamesByKey(SonarLintTestRpcServer backend, TransientSonarCloudConnectionDto connectionDto, List<String> projectKey) {
    return backend.getConnectionService().getProjectNamesByKey(new GetProjectNamesByKeyParams(connectionDto, projectKey)).join();
  }
}
