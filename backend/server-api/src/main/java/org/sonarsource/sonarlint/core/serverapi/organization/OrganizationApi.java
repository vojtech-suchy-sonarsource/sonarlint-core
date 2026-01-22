/*
ACR-3d3b3858633b405d88917142a96b24c0
ACR-cc5411517f6e480cb20cc40cc4e46030
ACR-f85cc3c5311a4df0bd0408450795e548
ACR-9efa470b24824e40bfc4ecabc21363d6
ACR-c6a3847e84f24ce49e6d3ff38547bca2
ACR-5044f397960c489ca5e5b037b6f06538
ACR-4218b3340a2e4815ba32b133f22b43ed
ACR-9c47afbce43f4ea1a534f366901e60bd
ACR-6a3c422f9f024743b2f0bb140da32b96
ACR-3eb2a3a383834310849786340ff7d2d5
ACR-299460243c2e4fffa94e7297c0a94a3f
ACR-fa252e1a7c7d4dd784054f6edfadc680
ACR-d6231e7680a0429291f816b339800458
ACR-293e607e4675491cb69c1311d2e9c9da
ACR-db8c4575d4bd4546a16526f3f9f326e2
ACR-32747685ad094902b3c8173e6f50d130
ACR-b6ef1c5b0a684a52adeffce21e010dfd
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
