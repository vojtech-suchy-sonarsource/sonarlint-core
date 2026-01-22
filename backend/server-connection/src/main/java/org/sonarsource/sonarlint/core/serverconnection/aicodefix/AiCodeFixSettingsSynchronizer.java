/*
ACR-709c06c3f3a04cbebf971988e6d96342
ACR-9b8152ce9b37422eafaabbd07db974c7
ACR-11257afd6e4f4ddc9e53b48a226f85b7
ACR-c09abc0c66d84c69af6b1415815f3c08
ACR-3f906af77e6740a19523e43930dca67a
ACR-fe34db7d3c3344cdbd23be9225f1abf9
ACR-a43aaa34fa8e4a439f96752404e15d13
ACR-b6e05fdaff3840d5ab207210e527ea3b
ACR-6cce6baebfac4f61a60af8e305645287
ACR-bca45ced974645cdbeb0d0abf6935573
ACR-87d4202ea4134902b514d836d7e8cfea
ACR-65f0ee34673c43749134a1a703f7e7bc
ACR-f85f3e9d57f2466baab776b1d4640bb9
ACR-868af4399caf44f3be88a339416f2260
ACR-a099b7f939174987b950cc7d5bc9dec9
ACR-517532ff70bd4b7e96acde9f50e4c325
ACR-07bfe5efb81b40bea69a55861f98bb78
 */
package org.sonarsource.sonarlint.core.serverconnection.aicodefix;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import org.sonarsource.sonarlint.core.serverapi.component.ServerProject;
import org.sonarsource.sonarlint.core.serverapi.features.Feature;
import org.sonarsource.sonarlint.core.serverapi.organization.ServerOrganization;
import org.sonarsource.sonarlint.core.serverconnection.ConnectionStorage;
import org.sonarsource.sonarlint.core.serverconnection.OrganizationSynchronizer;

public class AiCodeFixSettingsSynchronizer {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  public static final Version MIN_SQS_VERSION_SUPPORTING_AI_CODEFIX = Version.create("2025.3");

  private final ConnectionStorage storage;
  private final OrganizationSynchronizer organizationSynchronizer;
  private final AiCodeFixRepository aiCodeFixRepository;

  public AiCodeFixSettingsSynchronizer(ConnectionStorage storage, OrganizationSynchronizer organizationSynchronizer,
    AiCodeFixRepository aiCodeFixRepository) {
    this.storage = storage;
    this.organizationSynchronizer = organizationSynchronizer;
    this.aiCodeFixRepository = aiCodeFixRepository;
  }

  public void synchronize(ServerApi serverApi, Version serverVersion, Set<String> projectKeys, SonarLintCancelMonitor cancelMonitor) {
    if (serverApi.isSonarCloud()) {
      synchronizeForSonarQubeCloud(serverApi, cancelMonitor);
    } else {
      synchronizeForSonarQubeServer(serverApi, serverVersion, projectKeys, cancelMonitor);
    }
  }

  private void synchronizeForSonarQubeCloud(ServerApi serverApi, SonarLintCancelMonitor cancelMonitor) {
    var userOrganizations = serverApi.isSonarCloud() ? serverApi.organization().listUserOrganizations(cancelMonitor) : List.<ServerOrganization>of();
    if (userBelongsToOrganization(serverApi, userOrganizations)) {
      try {
        var supportedRules = serverApi.fixSuggestions().getSupportedRules(cancelMonitor);
        var organization = organizationSynchronizer.readOrSynchronizeOrganization(serverApi, cancelMonitor);
        var organizationConfig = serverApi.fixSuggestions().getOrganizationConfigs(organization.id(), cancelMonitor);
        var aiCodeFixConfiguration = organizationConfig.aiCodeFix();
        var enabledProjectKeys = aiCodeFixConfiguration.enabledProjectKeys();
        var enabled = enabledProjectKeys == null ? Set.<String>of() : enabledProjectKeys;
        var entity = new AiCodeFix(
          storage.connectionId(),
          supportedRules.rules(),
          aiCodeFixConfiguration.organizationEligible(),
          AiCodeFix.Enablement.valueOf(aiCodeFixConfiguration.enablement().name()),
          enabled);
        aiCodeFixRepository.upsert(entity);
      } catch (Exception e) {
        LOG.error("Error synchronizing AI CodeFix settings for SonarQube Cloud", e);
      }
    }
  }

  private void synchronizeForSonarQubeServer(ServerApi serverApi, Version serverVersion, Set<String> projectKeys, SonarLintCancelMonitor cancelMonitor) {
    try {
      if (serverVersion.satisfiesMinRequirement(MIN_SQS_VERSION_SUPPORTING_AI_CODEFIX) && serverApi.features().list(cancelMonitor).contains(Feature.AI_CODE_FIX)) {
        var supportedRules = serverApi.fixSuggestions().getSupportedRules(cancelMonitor);
        var enabledProjectKeys = projectKeys.stream()
          .filter(projectKey -> serverApi.component().getProject(projectKey, cancelMonitor).filter(ServerProject::isAiCodeFixEnabled).isPresent()).collect(Collectors.toSet());
        var entity = new AiCodeFix(
          storage.connectionId(),
          supportedRules.rules(),
          true,
          AiCodeFix.Enablement.ENABLED_FOR_SOME_PROJECTS,
          enabledProjectKeys);
        aiCodeFixRepository.upsert(entity);
      }
    } catch (Exception e) {
      LOG.error("Error synchronizing AI CodeFix settings for SonarQube Server", e);
    }
  }

  private static boolean userBelongsToOrganization(ServerApi serverApi, List<ServerOrganization> userOrganizations) {
    return serverApi.getOrganizationKey().filter(orgKey -> userOrganizations.stream().anyMatch(org -> org.getKey().equals(orgKey))).isPresent();
  }
}
