/*
ACR-b170a5da1d1b40c1a11f60fc1315d9fd
ACR-24357e98998a4af39acba05cfeb2eabe
ACR-2739c1fd98e442d587e91e04c5123854
ACR-9aa905a4b50f44fd914eae259bf993df
ACR-9f3b7c9f37e54a0b985c9c8b77c70994
ACR-c39daf713c004fa88ccf1afec7c30244
ACR-b1f12010fb984f7589f8d7f2db017f0d
ACR-e2fbce8dfbd2497db787360065046f28
ACR-d7fb148b5492438b85230d1a9828085b
ACR-64288aceffa4428283fda7dd59e87e0d
ACR-0b32fe240d614bcb903c9b2ab6995405
ACR-77bcc98f60a549a89122974f17df90cc
ACR-763c1c2070fb40ef89d02a13c8e478b7
ACR-d7ab403cbb264fda9b5c27c8a714f53b
ACR-c61b68f6114548e296331a91ce5115b3
ACR-5d5f3672600b45f5bef1233ea8a0c1e5
ACR-856161a59d9945639e42f32f3f543b46
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
