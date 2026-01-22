/*
ACR-d33aa2786b7d4c7cbd85e9d513bd4011
ACR-f517fbe31fd9479185b0921469c35843
ACR-c4a6774fb66d4aeda91744e97ddd0fac
ACR-390c31cadb8e444291d2196d8cbac5c8
ACR-74732e1d65804e96a93200e6d54d4ae0
ACR-37e4ac22b3344198858b0b9d33e138e1
ACR-2ce706ddfe07409a8fc7a8f119c1d9fd
ACR-de28545f8d984abdbaeb61534129cb32
ACR-b6f2794e509f4da382006c4b97d002e2
ACR-58dfd34256974a649104fd766a0f4c6b
ACR-983ab134a4bb4241b77bd44c6d16fd75
ACR-0eee3a20c3544a87ba27ca94f458afac
ACR-18dae90e376a4b86b2ac83d98d664d59
ACR-e65961d679c34ad59f703c93d323149a
ACR-90cea1d8c8c34bf49258b47a2b30a95e
ACR-ec88d43d9f564f2bad48253224948fcf
ACR-05adeda800d1492a808893f392bc0124
 */
package org.sonarsource.sonarlint.core;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.event.ConnectionConfigurationRemovedEvent;
import org.sonarsource.sonarlint.core.event.ConnectionConfigurationUpdatedEvent;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.projects.SonarProjectDto;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import org.sonarsource.sonarlint.core.serverapi.component.ServerProject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SonarProjectsCacheTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  public static final String SQ_1 = "sq1";
  public static final String PROJECT_KEY_1 = "projectKey1";
  public static final String PROJECT_KEY_2 = "projectKey2";
  public static final String PROJECT_NAME_1 = "Project 1";
  public static final String PROJECT_NAME_2 = "Project 2";
  public static final ServerProject PROJECT_1 = new ServerProject(PROJECT_KEY_1, PROJECT_NAME_1, false);
  public static final ServerProject PROJECT_1_CHANGED = new ServerProject(PROJECT_KEY_1, PROJECT_NAME_2, false);
  public static final ServerProject PROJECT_2 = new ServerProject(PROJECT_KEY_2, PROJECT_NAME_2, false);
  private final ServerApi serverApi = mock(ServerApi.class, Mockito.RETURNS_DEEP_STUBS);
  private final SonarQubeClientManager sonarQubeClientManager = mock(SonarQubeClientManager.class);
  private final SonarProjectsCache underTest = new SonarProjectsCache(sonarQubeClientManager);

  @BeforeEach
  void setup() {
    when(sonarQubeClientManager.withActiveClientAndReturn(any(), any())).thenAnswer(
      invocation -> Optional.ofNullable(((Function<ServerApi, Object>) invocation.getArguments()[1]).apply(serverApi)));
  }

  @Test
  void getSonarProject_should_query_server_once() {
    when(serverApi.component().getProject(eq(PROJECT_KEY_1), any(SonarLintCancelMonitor.class)))
      .thenReturn(Optional.of(PROJECT_1))
      .thenThrow(new AssertionError("Should only be called once"));

    var sonarProjectCall1 = underTest.getSonarProject(SQ_1, PROJECT_KEY_1, new SonarLintCancelMonitor());

    assertThat(sonarProjectCall1).isPresent();
    assertThat(sonarProjectCall1.get().key()).isEqualTo(PROJECT_KEY_1);
    assertThat(sonarProjectCall1.get().name()).isEqualTo(PROJECT_NAME_1);

    var sonarProjectCall2 = underTest.getSonarProject(SQ_1, PROJECT_KEY_1, new SonarLintCancelMonitor());

    assertThat(sonarProjectCall2).isPresent();
    assertThat(sonarProjectCall2.get().key()).isEqualTo(PROJECT_KEY_1);
    assertThat(sonarProjectCall2.get().name()).isEqualTo(PROJECT_NAME_1);
    verify(serverApi.component(), times(1)).getProject(eq(PROJECT_KEY_1), any(SonarLintCancelMonitor.class));
  }

  @Test
  void getSonarProject_should_cache_failure() {
    when(serverApi.component().getProject(eq(PROJECT_KEY_1), any(SonarLintCancelMonitor.class)))
      .thenThrow(new RuntimeException("Unable to fetch project"))
      .thenReturn(Optional.of(PROJECT_1));

    var sonarProjectCall1 = underTest.getSonarProject(SQ_1, PROJECT_KEY_1, new SonarLintCancelMonitor());

    assertThat(sonarProjectCall1).isEmpty();

    var sonarProjectCall2 = underTest.getSonarProject(SQ_1, PROJECT_KEY_1, new SonarLintCancelMonitor());

    assertThat(sonarProjectCall2).isEmpty();
    verify(serverApi.component(), times(1)).getProject(eq(PROJECT_KEY_1), any(SonarLintCancelMonitor.class));
  }

  @Test
  void evict_cache_if_connection_removed_to_save_memory() {
    when(serverApi.component().getProject(eq(PROJECT_KEY_1), any(SonarLintCancelMonitor.class)))
      .thenReturn(Optional.of(PROJECT_1));

    var sonarProjectCall1 = underTest.getSonarProject(SQ_1, PROJECT_KEY_1, new SonarLintCancelMonitor());

    assertThat(sonarProjectCall1).isPresent();
    assertThat(sonarProjectCall1.get().key()).isEqualTo(PROJECT_KEY_1);
    assertThat(sonarProjectCall1.get().name()).isEqualTo(PROJECT_NAME_1);

    underTest.connectionRemoved(new ConnectionConfigurationRemovedEvent(SQ_1));

    var sonarProjectCall2 = underTest.getSonarProject(SQ_1, PROJECT_KEY_1, new SonarLintCancelMonitor());

    assertThat(sonarProjectCall2).isPresent();
    assertThat(sonarProjectCall2.get().key()).isEqualTo(PROJECT_KEY_1);
    assertThat(sonarProjectCall2.get().name()).isEqualTo(PROJECT_NAME_1);
    verify(serverApi.component(), times(2)).getProject(eq(PROJECT_KEY_1), any(SonarLintCancelMonitor.class));
  }

  @Test
  void evict_cache_if_connection_updated_to_refresh_on_next_get() {
    when(serverApi.component().getProject(eq(PROJECT_KEY_1), any(SonarLintCancelMonitor.class)))
      .thenReturn(Optional.of(PROJECT_1))
      .thenReturn(Optional.of(PROJECT_1_CHANGED));

    var sonarProjectCall1 = underTest.getSonarProject(SQ_1, PROJECT_KEY_1, new SonarLintCancelMonitor());

    assertThat(sonarProjectCall1).isPresent();
    assertThat(sonarProjectCall1.get().key()).isEqualTo(PROJECT_KEY_1);
    assertThat(sonarProjectCall1.get().name()).isEqualTo(PROJECT_NAME_1);

    underTest.connectionUpdated(new ConnectionConfigurationUpdatedEvent(SQ_1));

    var sonarProjectCall2 = underTest.getSonarProject(SQ_1, PROJECT_KEY_1, new SonarLintCancelMonitor());

    assertThat(sonarProjectCall2).isPresent();
    assertThat(sonarProjectCall2.get().key()).isEqualTo(PROJECT_KEY_1);
    assertThat(sonarProjectCall2.get().name()).isEqualTo(PROJECT_NAME_2);
    verify(serverApi.component(), times(2)).getProject(eq(PROJECT_KEY_1), any(SonarLintCancelMonitor.class));
  }

  @Test
  void getTextSearchIndex_should_query_server_once() {
    when(serverApi.component().getAllProjects(any()))
      .thenReturn(List.of(PROJECT_1, PROJECT_2))
      .thenThrow(new AssertionError("Should only be called once"));

    var searchIndex1 = underTest.getTextSearchIndex(SQ_1, new SonarLintCancelMonitor());

    assertThat(searchIndex1.size()).isEqualTo(2);

    var searchIndex2 = underTest.getTextSearchIndex(SQ_1, new SonarLintCancelMonitor());

    assertThat(searchIndex2.size()).isEqualTo(2);
    verify(serverApi.component(), times(1)).getAllProjects(any());
  }

  @Test
  void getTextSearchIndex_should_return_empty_index_if_no_projects() {
    when(serverApi.component().getAllProjects(any()))
      .thenReturn(List.of())
      .thenThrow(new AssertionError("Should only be called once"));

    var searchIndex1 = underTest.getTextSearchIndex(SQ_1, new SonarLintCancelMonitor());

    assertThat(searchIndex1.isEmpty()).isTrue();

    underTest.getTextSearchIndex(SQ_1, new SonarLintCancelMonitor());

    assertThat(searchIndex1.isEmpty()).isTrue();
    verify(serverApi.component(), times(1)).getAllProjects(any());
  }

  @Test
  void getTextSearchIndex_should_cache_failure() {
    when(serverApi.component().getAllProjects(any()))
      .thenThrow(new RuntimeException("Unable to fetch projects"))
      .thenReturn(List.of(PROJECT_1, PROJECT_2));

    var searchIndex1 = underTest.getTextSearchIndex(SQ_1, new SonarLintCancelMonitor());

    assertThat(searchIndex1.isEmpty()).isTrue();

    underTest.getTextSearchIndex(SQ_1, new SonarLintCancelMonitor());

    assertThat(searchIndex1.isEmpty()).isTrue();
    verify(serverApi.component(), times(1)).getAllProjects(any());
  }

  @Test
  void fuzzySearchProjects_should_search_by_both_key_and_name_splitting_by_underscore() {
    var project1 = new ServerProject("mySearchTerm", "project", false);
    var project2 = new ServerProject("key", "searchTerm__00", false);
    var projectNotFound = new ServerProject("SonarSource_peachee-dotnet", "DriAutomation.NET", false);
    var projectFound = new ServerProject("SonarSource_sonarsource-infra-peach", "sonarsource-infra-peach", false);
    when(serverApi.component().getAllProjects(any()))
      .thenReturn(List.of(project1, project2, projectNotFound, projectFound));

    var actual = underTest.fuzzySearchProjects(SQ_1, "peach", new SonarLintCancelMonitor());

    assertThat(actual).containsExactlyInAnyOrder(
      new SonarProjectDto("SonarSource_peachee-dotnet", "DriAutomation.NET"),
      new SonarProjectDto("SonarSource_sonarsource-infra-peach", "sonarsource-infra-peach")
    );
  }
}
