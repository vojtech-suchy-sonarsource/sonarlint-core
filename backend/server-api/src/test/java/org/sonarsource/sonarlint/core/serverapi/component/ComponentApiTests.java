/*
ACR-7bd24277c46543efb6fe1affc34087db
ACR-4f22830f565c44bd9f6d6b715a9a21ef
ACR-cabb63d899644b5f9a9d5672be5493f6
ACR-5f4f97e95ec8463aba857157bb99a2b4
ACR-dc44fd844c7149f584e7784632d73172
ACR-63de4f7a07814f249903bbe2cb8acf8d
ACR-e82bc21d5f4341f1bf3782e897c9766b
ACR-3b5697c5c91d4a0b8fe2cfd5d975b454
ACR-fe94b9522fd745789a92e7c88135c13c
ACR-fd56f24cebba421ab67d326a25290fcd
ACR-d8ed70e4ef654e56a9eb25b1504fd565
ACR-b8273cb06fff417382adb89491d993fc
ACR-95b423ead38744c8b67f9518bcec6d3f
ACR-110e52f522bb4d2592fc46a5d687268e
ACR-a4c554919eaf4d20b8b16d05f3b2ee11
ACR-7a6bd5b0144642d285480a5e537e4445
ACR-bef2fe61ddf54dee9cebff9e60c22737
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
