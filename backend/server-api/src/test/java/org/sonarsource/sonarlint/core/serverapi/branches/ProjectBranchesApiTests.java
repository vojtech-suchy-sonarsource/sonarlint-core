/*
ACR-f35cf55202374aa1aec3e8eb07d7c8bc
ACR-6ac22971e0d543b0b09bfcc2f0501d75
ACR-9a0319a38dce4a5082d643523435391b
ACR-8eb24bfabf0e4579a17cb54f692360c6
ACR-6cb1abd2acb6455898120113546d114c
ACR-08f26da04e384036a2f68d7d3a7c6424
ACR-14214bbeae8f4eba8765c5d0cfac368e
ACR-f529f06af2824dc9b940f61f57c3777e
ACR-d58df77043694ad4bce6fff50532ed9d
ACR-530a678a1cac47bba7b5c671d83952a0
ACR-2f9c7f10557046a1be3fcfe048e5bbac
ACR-bd61a23363ad40a89088e1c8d3c4b644
ACR-4b696381d89d49a08acc8e2226f2d892
ACR-21295b48c7e84b90b6847d58651e14bb
ACR-3229dc6a0f7a4e2ab6886d1102e7caed
ACR-a4239cca05aa435785c31690c6f1f8e3
ACR-11ec9f7946ad47bdbbd0296d1db8cc6d
 */
package org.sonarsource.sonarlint.core.serverapi.branches;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.MockWebServerExtensionWithProtobuf;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Common.BranchType;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.ProjectBranches;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class ProjectBranchesApiTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  @RegisterExtension
  static MockWebServerExtensionWithProtobuf mockServer = new MockWebServerExtensionWithProtobuf();

  private final static String PROJECT_KEY = "project1";

  private ProjectBranchesApi underTest;

  @BeforeEach
  void setUp() {
    underTest = new ProjectBranchesApi(mockServer.serverApiHelper());
  }

  @Test
  void shouldDownloadBranches() {
    mockServer.addProtobufResponse("/api/project_branches/list.protobuf?project=" + PROJECT_KEY, ProjectBranches.ListWsResponse.newBuilder()
      .addBranches(ProjectBranches.Branch.newBuilder().setName("feature/foo").setIsMain(false).setType(BranchType.BRANCH))
      .addBranches(ProjectBranches.Branch.newBuilder().setName("master").setIsMain(true).setType(BranchType.BRANCH)).build());

    var branches = underTest.getAllBranches(PROJECT_KEY, new SonarLintCancelMonitor());

    assertThat(branches).extracting(ServerBranch::getName, ServerBranch::isMain).containsExactlyInAnyOrder(tuple("master", true), tuple("feature/foo", false));
  }

  @Test
  void shouldSkipShortLivingBranches() {
    var branchListResponseBuilder = ProjectBranches.ListWsResponse.newBuilder();
    branchListResponseBuilder.addBranches(ProjectBranches.Branch.newBuilder().setName("branch-1.x").setIsMain(false).setType(BranchType.BRANCH));
    branchListResponseBuilder.addBranches(ProjectBranches.Branch.newBuilder().setName("master").setIsMain(true).setType(BranchType.BRANCH));
    branchListResponseBuilder.addBranches(ProjectBranches.Branch.newBuilder().setName("feature/my-long-branch").setIsMain(false).setType(BranchType.LONG));
    branchListResponseBuilder.addBranches(ProjectBranches.Branch.newBuilder().setName("feature/my-short-branch").setIsMain(false).setType(BranchType.SHORT));

    mockServer.addProtobufResponse("/api/project_branches/list.protobuf?project=" + PROJECT_KEY, branchListResponseBuilder.build());

    var branches = underTest.getAllBranches(PROJECT_KEY, new SonarLintCancelMonitor());

    assertThat(branches).extracting(ServerBranch::getName, ServerBranch::isMain)
      .containsExactlyInAnyOrder(tuple("master", true), tuple("branch-1.x", false),
        tuple("feature/my-long-branch", false));
  }

  @Test
  void shouldReturnEmptyListOnMalformedResponse() {
    mockServer.addStringResponse("/api/project_branches/list.protobuf?project=project1",
      """
        {
          "branches": [
            { }\
          ]
        }""");

    var branches = underTest.getAllBranches(PROJECT_KEY, new SonarLintCancelMonitor());

    assertThat(branches).isEmpty();
  }

}
