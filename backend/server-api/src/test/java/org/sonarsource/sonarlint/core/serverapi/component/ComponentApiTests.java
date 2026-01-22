/*
ACR-c0da464429584e90a55d7184d82e4ea3
ACR-8ec1035a0f494d01b98ee97e70e75365
ACR-199bda917abe4f95b66778d4a12acee3
ACR-32516bac8618499ba93ae0e7fd4fa98f
ACR-ece0959b122c43ffa985546bd98cb383
ACR-d5b904a374824affa1274e1732b55705
ACR-0f44ac2bd25e42038dcd719e3693ffeb
ACR-d8ae5a457fed4ca5958f38a176f39d80
ACR-5a7174a389a144c093e05db7eb0c78fa
ACR-9d08e860b0b04d308658a09716b8481c
ACR-2ae55251d76049a780c524c9af3f045c
ACR-96e177823d8e4614a292c7a4539a6cde
ACR-b0d4ec50d45b4114b944ad38e1e4d048
ACR-71accc30d7014d6980107b4bc5d17802
ACR-67e5b760484944d3b64a39da225669b8
ACR-97fb3d6f131b4c799c32f9f0ad64baba
ACR-743d1476574741bcb74871e858334ba9
 */
package org.sonarsource.sonarlint.core.serverapi.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.MockWebServerExtensionWithProtobuf;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Components;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class ComponentApiTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();
  @RegisterExtension
  static MockWebServerExtensionWithProtobuf mockServer = new MockWebServerExtensionWithProtobuf();

  private final static String PROJECT_KEY = "project1";

  private ComponentApi underTest;

  @BeforeEach
  void setUp() {
    underTest = new ComponentApi(mockServer.serverApiHelper());
  }

  @Test
  void should_return_empty_when_no_components_returned() {
    mockServer.addStringResponse("/api/components/search_projects?projectIds=project%3Akey",
      "{\"components\":[]}");

    var result = underTest.searchProjects("project:key", new SonarLintCancelMonitor());

    assertThat(result).isNull();
  }

  @Test
  void should_return_empty_when_response_is_invalid_json() {
    mockServer.addStringResponse("/api/components/search_projects?projectIds=project%3Akey",
      "invalid json");

    var result = underTest.searchProjects("project:key", new SonarLintCancelMonitor());

    assertThat(result).isNull();
  }

  @Test
  void should_get_project_key_by_project_id() {
    var projectId = "project:key";
    var encodedProjectId = "project%3Akey";
    var organization = "my-org";
    underTest = new ComponentApi(mockServer.serverApiHelper(organization));

    mockServer.addStringResponse("/api/components/search_projects?projectIds=" + encodedProjectId + "&organization=" + organization,
      "{\"components\":[{\"key\":\"projectKey\",\"name\":\"projectName\"}]}\n");

    var result = underTest.searchProjects(projectId, new SonarLintCancelMonitor());

    assertThat(result.projectKey()).isEqualTo("projectKey");
    assertThat(result.projectName()).isEqualTo("projectName");
  }

  @Test
  void should_return_empty_if_project_not_found() {
    var result = underTest.searchProjects("project:key", new SonarLintCancelMonitor());

    assertThat(result).isNull();
  }

  @Test
  void should_get_files() {
    mockServer.addResponseFromResource("/api/components/tree.protobuf?qualifiers=FIL,UTS&component=project1&ps=500&p=1", "/update/component_tree.pb");

    var files = underTest.getAllFileKeys(PROJECT_KEY, new SonarLintCancelMonitor());

    assertThat(files).hasSize(187);
    assertThat(files.get(0)).isEqualTo("org.sonarsource.sonarlint.intellij:sonarlint-intellij:src/main/java/org/sonarlint/intellij/ui/AbstractIssuesPanel.java");
  }

  @Test
  void should_get_files_with_organization() {
    underTest = new ComponentApi(mockServer.serverApiHelper("myorg"));
    mockServer.addResponseFromResource("/api/components/tree.protobuf?qualifiers=FIL,UTS&component=project1&organization=myorg&ps=500&p=1", "/update/component_tree.pb");

    var files = underTest.getAllFileKeys(PROJECT_KEY, new SonarLintCancelMonitor());

    assertThat(files).hasSize(187);
    assertThat(files.get(0)).isEqualTo("org.sonarsource.sonarlint.intellij:sonarlint-intellij:src/main/java/org/sonarlint/intellij/ui/AbstractIssuesPanel.java");
  }

  @Test
  void should_get_empty_files_if_tree_is_empty() {
    mockServer.addResponseFromResource("/api/components/tree.protobuf?qualifiers=FIL,UTS&component=project1&ps=500&p=1", "/update/empty_component_tree.pb");

    var files = underTest.getAllFileKeys(PROJECT_KEY, new SonarLintCancelMonitor());

    assertThat(files).isEmpty();
  }

  @Test
  void should_get_all_projects() {
    mockServer.addProtobufResponse("/api/components/search.protobuf?qualifiers=TRK&ps=500&p=1", Components.SearchWsResponse.newBuilder()
      .addComponents(Components.Component.newBuilder().setKey("projectKey").setName("projectName").build()).build());
    mockServer.addProtobufResponse("/api/components/search.protobuf?qualifiers=TRK&ps=500&p=2", Components.SearchWsResponse.newBuilder().build());

    var projects = underTest.getAllProjects(new SonarLintCancelMonitor());

    assertThat(projects)
      .extracting("key", "name")
      .containsOnly(tuple("projectKey", "projectName"));
  }

  @Test
  void should_get_all_projects_with_organization() {
    mockServer.addProtobufResponse("/api/components/search.protobuf?qualifiers=TRK&organization=org%3Akey&ps=500&p=1", Components.SearchWsResponse.newBuilder()
      .addComponents(Components.Component.newBuilder().setKey("projectKey").setName("projectName").build()).build());
    mockServer.addProtobufResponse("/api/components/search.protobuf?qualifiers=TRK&organization=org%3Akey&ps=500&p=2", Components.SearchWsResponse.newBuilder().build());
    var componentApi = new ComponentApi(mockServer.serverApiHelper("org:key"));

    var projects = componentApi.getAllProjects(new SonarLintCancelMonitor());

    assertThat(projects)
      .extracting("key", "name")
      .containsOnly(tuple("projectKey", "projectName"));
  }

  @Test
  void should_get_project_details() {
    mockServer.addProtobufResponse("/api/components/show.protobuf?component=project%3Akey", Components.ShowWsResponse.newBuilder()
      .setComponent(Components.Component.newBuilder().setKey("projectKey").setName("projectName").build()).build());

    var project = underTest.getProject("project:key", new SonarLintCancelMonitor());

    assertThat(project).hasValueSatisfying(p -> {
      assertThat(p.key()).isEqualTo("projectKey");
      assertThat(p.name()).isEqualTo("projectName");
    });
  }

  @Test
  void should_get_empty_project_details_if_request_fails() {
    var project = underTest.getProject("project:key", new SonarLintCancelMonitor());

    assertThat(project).isEmpty();
  }

  @Test
  void should_get_ancestor_key() {
    mockServer.addProtobufResponse("/api/components/show.protobuf?component=project%3Akey", Components.ShowWsResponse.newBuilder()
      .addAncestors(Components.Component.newBuilder().setKey("ancestorKey").build()).build());

    var project = underTest.fetchFirstAncestorKey("project:key", new SonarLintCancelMonitor());

    assertThat(project).contains("ancestorKey");
  }
}
