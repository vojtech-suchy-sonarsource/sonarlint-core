/*
ACR-57b33cce8b934596ae2a38aefcde375f
ACR-f5b041efc9974e58a2bd93fb96346bcf
ACR-a5e23ec6b567425fa241aa2b318daf4b
ACR-8d0dad946bbe4b49bba2fa8757feab1d
ACR-ef7a7fd06f624c34bfe49f30094873c9
ACR-3ccf4badf8054e8eb32abb5bc416fcd8
ACR-16aae8077dd44645bc2bfb625ed76294
ACR-4171681c0af24451b33d23eba43f1569
ACR-90b88d02509347939e1636960718add9
ACR-d28cd9ab50d64b0f8043ba4381097790
ACR-a1722b995cba49cb8c49c99c8b95b131
ACR-40abe6067b1845269d38f313bfd85daa
ACR-f2094b8c827340289769514a5ca202b3
ACR-c544fed9c3954405b7c722b7e1977083
ACR-b70c8a5903d440f88a55e1efbfd0f15c
ACR-c7c5d8a218954e16869195b7dda1a230
ACR-ef064f2cffcb4ef3b02ac364534d16b7
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
