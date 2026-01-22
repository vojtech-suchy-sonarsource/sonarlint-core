/*
ACR-138159e878b54201be92cca0be1121e0
ACR-7926b66d6dbe47a4b88106067902cf6f
ACR-b45d72307d7842359249ddeb862dfc01
ACR-c2505d1a857842e2bca54d843484d8dc
ACR-4fd1a94cc64a4a699a3e3aaaefa56d90
ACR-a9ba5a84fea046928699be0fbefa6cc4
ACR-768fb554e04d4bcabee091f71cb284d7
ACR-c88dd5bec23f4d358aa4e43ec0990f58
ACR-8a04a6b2460940fba4cc1267ee6262df
ACR-a3a6616a32cf4c6f93d92013c73d9f50
ACR-208d987536054182999cd9fa15b121ef
ACR-92208388a83543d2b56d6e99a8b5c52e
ACR-156c3b8988984903b85e5af36c440474
ACR-4499e827d92a47aeabc12d1e627306c2
ACR-fcc52a94901840e6af72a8546823c926
ACR-eb0554bdf03f4b1abd1d7aefd063e9c1
ACR-e36013d5df7a411898d18f2d18189ffb
 */
package org.sonarsource.sonarlint.core.serverapi.component;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.CheckForNull;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;
import org.sonarsource.sonarlint.core.serverapi.UrlUtils;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Components;

public class ComponentApi {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private static final String ORGANIZATION_PARAM = "&organization=";

  private final ServerApiHelper helper;

  public ComponentApi(ServerApiHelper helper) {
    this.helper = helper;
  }

  public List<String> getAllFileKeys(String projectKey, SonarLintCancelMonitor cancelMonitor) {
    var path = buildAllFileKeysPath(projectKey);
    List<String> files = new ArrayList<>();

    helper.getPaginated(path,
      Components.TreeWsResponse::parseFrom,
      r -> r.getPaging().getTotal(),
      Components.TreeWsResponse::getComponentsList,
      component -> files.add(component.getKey()), false, cancelMonitor);
    return files;
  }

  private String buildAllFileKeysPath(String projectKey) {
    var url = new StringBuilder();
    url.append("api/components/tree.protobuf?qualifiers=FIL,UTS&");
    url.append("component=").append(UrlUtils.urlEncode(projectKey));
    helper.getOrganizationKey().ifPresent(org -> url.append(ORGANIZATION_PARAM).append(UrlUtils.urlEncode(org)));
    return url.toString();
  }

  public Optional<ServerProject> getProject(String projectKey, SonarLintCancelMonitor cancelMonitor) {
    return fetchComponent(projectKey, cancelMonitor).map(component -> new ServerProject(component.key(), component.name(), component.isAiCodeFixEnabled()));
  }

  public List<ServerProject> getAllProjects(SonarLintCancelMonitor cancelMonitor) {
    List<ServerProject> serverProjects = new ArrayList<>();
    helper.getPaginated(getAllProjectsUrl(),
      Components.SearchWsResponse::parseFrom,
      r -> r.getPaging().getTotal(),
      Components.SearchWsResponse::getComponentsList,
      project -> serverProjects.add(new ServerProject(project.getKey(), project.getName(), project.getIsAiCodeFixEnabled())),
      true,
      cancelMonitor);
    return serverProjects;
  }

  private String getAllProjectsUrl() {
    var searchUrl = new StringBuilder();
    searchUrl.append("api/components/search.protobuf?qualifiers=TRK");
    helper.getOrganizationKey()
      .ifPresent(org -> searchUrl.append(ORGANIZATION_PARAM).append(UrlUtils.urlEncode(org)));
    return searchUrl.toString();
  }

  @CheckForNull
  public SearchProjectResponse searchProjects(String projectId, SonarLintCancelMonitor cancelMonitor) {
    var encodedProjectId = UrlUtils.urlEncode(projectId);
    var organization = helper.getOrganizationKey();

    if (organization.isEmpty()) {
      LOG.warn("Organization key is not set, cannot search projects for ID: {}", projectId);
      return null;
    }
    var path = "/api/components/search_projects?projectIds=" + encodedProjectId + ORGANIZATION_PARAM + organization.get();

    try (var response = helper.rawGet(path, cancelMonitor)) {
      if (response.isSuccessful()) {
        var searchResponse = new Gson().fromJson(response.bodyAsString(), SearchProjectResponseDto.class);

        return searchResponse.components().stream()
          .findFirst()
          .map(component -> new SearchProjectResponse(component.key(), component.name()))
          .orElse(null);
      } else {
        LOG.warn("Failed to retrieve project for ID: {} (status: {})", projectId, response.code());
      }
    } catch (Exception e) {
      LOG.error("Error retrieving project for ID: {}", projectId, e);
    }

    return null;
  }

  private Optional<Component> fetchComponent(String componentKey, SonarLintCancelMonitor cancelMonitor) {
    return fetchComponent(componentKey, response -> {
      var wsComponent = response.getComponent();
      return new Component(wsComponent.getKey(), wsComponent.getName(), wsComponent.getIsAiCodeFixEnabled());
    }, cancelMonitor);
  }

  public Optional<String> fetchFirstAncestorKey(String componentKey, SonarLintCancelMonitor cancelMonitor) {
    return fetchComponent(componentKey, response -> response.getAncestorsList().stream().map(Components.Component::getKey).findFirst().orElse(null), cancelMonitor);
  }

  private <T> Optional<T> fetchComponent(String componentKey, Function<Components.ShowWsResponse, T> responseConsumer, SonarLintCancelMonitor cancelMonitor) {
    return ServerApiHelper.processTimed(
      () -> helper.rawGet("api/components/show.protobuf?component=" + UrlUtils.urlEncode(componentKey), cancelMonitor),
      response -> {
        if (response.isSuccessful()) {
          var wsResponse = Components.ShowWsResponse.parseFrom(response.bodyAsStream());
          return Optional.ofNullable(responseConsumer.apply(wsResponse));
        }
        return Optional.empty();
      },
      duration -> LOG.debug("Downloaded project details in {}ms", duration));
  }
}
