/*
ACR-b1d4da8d2a7e4dbd8d11db0f853f152a
ACR-acd29047b7c141588e25c8d35f765821
ACR-57bedcd3cc2b4064810d447ed18cdb85
ACR-a29af2299b734b0c97c76584f8a520bd
ACR-fc8a7b5ebad34aac8649fd1128829a0a
ACR-b02a6c0ba65640279635abe8040e21a1
ACR-2e101a2a22064be9bedfa75a7b22c521
ACR-c4569804484645bcb2ee56190c8ebc94
ACR-6148dd8363874a1ea1ed5a63b21e2d41
ACR-05d3cb3841574f379146c03fcc998b57
ACR-a3f6dbcb30e24eb595946285cf03acfc
ACR-3f2fb821c26c4948b48fe5524dc465fc
ACR-d2621cbf0a9943cfa3c7262af8f7df47
ACR-fb75648372464503b555fdf97d3c470f
ACR-7daab0cd3d4c41a48f5959e2de261a3a
ACR-720746d0d5dd4d89a880ec971adb1e63
ACR-638fa64373e443e2a8a4f50bc134e941
 */
package org.sonarsource.sonarlint.core.serverapi.branches;

import java.util.List;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;
import org.sonarsource.sonarlint.core.serverapi.UrlUtils;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Common.BranchType;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.ProjectBranches;

public class ProjectBranchesApi {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private static final String LIST_ALL_PROJECT_BRANCHES_URL = "/api/project_branches/list.protobuf";
  private final ServerApiHelper helper;

  public ProjectBranchesApi(ServerApiHelper helper) {
    this.helper = helper;
  }

  public List<ServerBranch> getAllBranches(String projectKey, SonarLintCancelMonitor cancelMonitor) {
    ProjectBranches.ListWsResponse response;
    try (var wsResponse = helper.get(LIST_ALL_PROJECT_BRANCHES_URL + "?project=" + UrlUtils.urlEncode(projectKey), cancelMonitor); var is = wsResponse.bodyAsStream()) {
      response = ProjectBranches.ListWsResponse.parseFrom(is);
    } catch (Exception e) {
      LOG.error("Error while fetching project branches", e);
      return List.of();
    }
    return response.getBranchesList().stream()
      .filter(b -> b.getType() == BranchType.BRANCH || b.getType() == BranchType.LONG)
      .map(branchWs -> new ServerBranch(branchWs.getName(), branchWs.getIsMain())).toList();
  }

}
