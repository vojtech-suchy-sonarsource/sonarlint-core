/*
ACR-394a15ef364946c89788e8be8e582667
ACR-a27d4b4d8fa64c6a8f40b113b1d04981
ACR-c6d241ee4fad4280a78c5dc89c9e3d90
ACR-fcccadd7b21a4d428cd7826a4789fa80
ACR-263eaacdafa14403a5b474ef6ca982c7
ACR-d8bc57f1e6ec4bb08e60b9d5e0a35c88
ACR-c9f79d7bb9ba4f00b97945c6be80642e
ACR-06a1fd66ac0c4659b6c70b693c2670c7
ACR-93514db4c28d4cc3aca6a8fb2b026501
ACR-c33d1012c4e34e5fa9913f5cae2ff7a6
ACR-db60ceb2cce1462499e7764836e69234
ACR-509783b5b7114128afd97579a579001a
ACR-7424b1f8166d43daa0a2ca413a25fcef
ACR-07219de237ec4ed69e3ffd12cb8b8717
ACR-81bfbdafed5e48d48379a152786415b1
ACR-ee7c5517a6c64352b2ee8fc9d59f080b
ACR-652c934e675442d68d78ce4c6eedec15
 */
package org.sonarsource.sonarlint.core.serverapi.organization;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;
import org.sonarsource.sonarlint.core.serverapi.UrlUtils;
import org.sonarsource.sonarlint.core.serverapi.exception.UnexpectedBodyException;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarcloud.ws.Organizations;

public class OrganizationApi {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final ServerApiHelper helper;

  public OrganizationApi(ServerApiHelper helper) {
    this.helper = helper;
  }

  public List<ServerOrganization> listUserOrganizations(SonarLintCancelMonitor cancelMonitor) {
    var url = "api/organizations/search.protobuf?member=true";
    return getPaginatedOrganizations(url, cancelMonitor);
  }

  public Optional<ServerOrganization> searchOrganization(String organizationKey, SonarLintCancelMonitor cancelMonitor) {
    var url = "api/organizations/search.protobuf?organizations=" + UrlUtils.urlEncode(organizationKey);
    return getPaginatedOrganizations(url, cancelMonitor)
      .stream()
      .findFirst();
  }

  public GetOrganizationsResponseDto getOrganizationByKey(SonarLintCancelMonitor cancelMonitor) {
    var organizationKey = helper.getOrganizationKey().orElseThrow(() -> new IllegalArgumentException("Organizations are only supported for SonarQube Cloud"));
    try (var response = helper.apiGet("/organizations/organizations?organizationKey=" + UrlUtils.urlEncode(organizationKey) + "&excludeEligibility=true", cancelMonitor)) {
      return new Gson().fromJson(response.bodyAsString(), GetOrganizationsResponseDto[].class)[0];
    } catch (Exception e) {
      LOG.error("Error while fetching the organization", e);
      throw new UnexpectedBodyException(e);
    }
  }

  private List<ServerOrganization> getPaginatedOrganizations(String url, SonarLintCancelMonitor cancelMonitor) {
    List<ServerOrganization> result = new ArrayList<>();

    helper.getPaginated(url,
      Organizations.SearchWsResponse::parseFrom,
      r -> r.getPaging().getTotal(),
      Organizations.SearchWsResponse::getOrganizationsList,
      org -> result.add(new ServerOrganization(org)),
      false,
      cancelMonitor);

    return result;
  }
}
