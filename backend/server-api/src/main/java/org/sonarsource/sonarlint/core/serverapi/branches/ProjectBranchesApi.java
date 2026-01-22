/*
ACR-3c5a3c33077146b7a7e4c2d1fe6a40d3
ACR-3db4cec954104f9fa422f52e35bb49d4
ACR-5577e29ae8dd414298b100c8792b964f
ACR-a4ab286d6b7a4fc19a1816a7c986f77c
ACR-5505821c10a2455aa472c152b86019e5
ACR-f6ae83b476b844fbb5a517dbe8de4e6f
ACR-ff28960d9f36416ea1b52b4ef3b27dde
ACR-aa1a84e0c7bf413b959311d87e0ef128
ACR-2eac0a09b15d452da6b23fd62c71de8c
ACR-d3a499c7c98b4dc7873150680d854989
ACR-cf80ac2cdacd46ac8dbed80ee46b4007
ACR-632b77b28cd746f7bbef7caabac011e3
ACR-c83efef8d28a468eaa52e9c4e35cc6ab
ACR-c717f8f13c744cf0ad720a13ab821a46
ACR-03628f44d84c4110a1e74baac2a9d924
ACR-d5cf3dd0c73844e3a35b3e276a0bb271
ACR-c1d71ae5d63a4a4f94f77fcdfcf214b3
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
