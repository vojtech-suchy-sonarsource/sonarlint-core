/*
ACR-2e888b6543ca4d6eb69a8014bd5b4c30
ACR-51001054c9f04b4e92b363e1aaa60f6b
ACR-918ab75cbcd04bfeb4d4bb67cc37a873
ACR-5b8e4192b09f4a4e8dbe20ae5b701478
ACR-782ef620eb9749468ba7ac2b4a961dde
ACR-7b42272f8a3a4a979486f4f6cfbdb5e6
ACR-46974e5e9e164d4f931a455bf9df2444
ACR-aa49a5544eb54ba2a0646e8fafb2296a
ACR-40c7d136d27a44708c3baa2e8a9ad159
ACR-495d0ffff8854fb39140f30096566eb9
ACR-34e0fe90bffe40eeb0bfec4f23c67e5e
ACR-9d5a0f9115f94e2aa6431ba28d9e5664
ACR-8e7fdce43d4543338a9cd2c92c7fe895
ACR-4eafd9806b88432e8602cd00623d2afa
ACR-f1f32ae2f51d485080a3f731e52bf9e8
ACR-579f2bb86ec642d7ab17ac406d00b95a
ACR-0c03666492e44a4d839fe867cd2348cb
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
