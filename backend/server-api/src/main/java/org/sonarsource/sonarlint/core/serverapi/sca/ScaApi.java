/*
ACR-acf56dc8abd64eb487d3ad3ef1fa517d
ACR-a49c0c65f3cf4a21b3cbab38088e6f96
ACR-9549f3c59e5142e59f7519f8ac7be86b
ACR-3a3c52a8fd8648eb9234c8c8569a8fba
ACR-92e83508c2ae4eed942f7bbd584bab22
ACR-e799073765be41a69cb0611050e2b6d1
ACR-433bf8bb61934358b874d8cf7895ce45
ACR-89103371e6cd4cb2b482ec5bb05c5a89
ACR-4f063eb819914399ac836c1fe09cb2f1
ACR-befc46aa1bf84661a2ef4a67d9b5746a
ACR-159ba7896eb649a5acb20a089a00a214
ACR-7ef24cb58c4b495d972b0bb4a5918a9a
ACR-5a213f672f4e4cc3b0af5a9e5cfd3aff
ACR-45c7732ed8be4d73baa09c0145cd172d
ACR-4f7d4c8e5537474f9d311dd1064597fe
ACR-f21a3539b4884bb788fe6f1ed006b99f
ACR-dfb04001ddd048c4bb8b901d5a4e5acf
 */
package org.sonarsource.sonarlint.core.serverapi.sca;

import com.google.gson.Gson;
import jakarta.annotation.Nullable;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;
import org.sonarsource.sonarlint.core.serverapi.UrlUtils;

import static org.sonarsource.sonarlint.core.http.HttpClient.JSON_CONTENT_TYPE;

public class ScaApi {

  private final ServerApiHelper serverApiHelper;

  public ScaApi(ServerApiHelper serverApiHelper) {
    this.serverApiHelper = serverApiHelper;
  }

  public GetIssuesReleasesResponse getIssuesReleases(String projectKey, String branchKey, SonarLintCancelMonitor cancelMonitor) {
    var urlPrefix = serverApiHelper.isSonarCloud() ? "" : "/api/v2";
    var url = urlPrefix + "/sca/issues-releases?projectKey=" +
      UrlUtils.urlEncode(projectKey) +
      "&branchKey=" +
      UrlUtils.urlEncode(branchKey);

    var allIssuesReleases = new ArrayList<GetIssuesReleasesResponse.IssuesRelease>();

    if (serverApiHelper.isSonarCloud()) {
      serverApiHelper.apiGetPaginated(
        url,
        response -> new Gson().fromJson(new InputStreamReader(response, StandardCharsets.UTF_8), GetIssuesReleasesResponse.class),
        r -> r.page().total(),
        GetIssuesReleasesResponse::issuesReleases,
        allIssuesReleases::add,
        false,
        cancelMonitor,
        "pageIndex",
        "pageSize");
    } else {
      serverApiHelper.getPaginated(
        url,
        response -> new Gson().fromJson(new InputStreamReader(response, StandardCharsets.UTF_8), GetIssuesReleasesResponse.class),
        r -> r.page().total(),
        GetIssuesReleasesResponse::issuesReleases,
        allIssuesReleases::add,
        false,
        cancelMonitor,
        "pageIndex",
        "pageSize");
    }
    return new GetIssuesReleasesResponse(allIssuesReleases, new GetIssuesReleasesResponse.Page(allIssuesReleases.size()));
  }

  public void changeStatus(UUID issueReleaseKey, String transitionKey, @Nullable String comment, SonarLintCancelMonitor cancelMonitor) {
    var body = new ChangeStatusRequestBody(issueReleaseKey.toString(), transitionKey, comment);
    var urlPrefix = serverApiHelper.isSonarCloud() ? "" : "/api/v2";
    var url = urlPrefix + "/sca/issues-releases/change-status";

    if (serverApiHelper.isSonarCloud()) {
      serverApiHelper.apiPost(url, JSON_CONTENT_TYPE, body.toJson(), cancelMonitor);
    } else {
      serverApiHelper.post(url, JSON_CONTENT_TYPE, body.toJson(), cancelMonitor);
    }
  }

  private record ChangeStatusRequestBody(String issueReleaseKey, String transitionKey, @Nullable String comment) {
    public String toJson() {
      return new Gson().toJson(this);
    }
  }

  public GetScaEnablementResponse isScaEnabled(SonarLintCancelMonitor cancelMonitor) {
    var organizationKey = serverApiHelper.getOrganizationKey();
    if (organizationKey.isEmpty()) {
      return new GetScaEnablementResponse(false);
    }
    try (var response = serverApiHelper.apiGet("/sca/feature-enabled?organization=" + UrlUtils.urlEncode(organizationKey.get()), cancelMonitor)) {
      return new Gson().fromJson(new InputStreamReader(response.bodyAsStream(), StandardCharsets.UTF_8), GetScaEnablementResponse.class);
    } catch (Exception e) {
      return new GetScaEnablementResponse(false);
    }
  }

}
