/*
ACR-e1d11716d0aa4cb58dfdb1befae257c5
ACR-69280850143f4984a7954021fd3f69d7
ACR-4eb0c9698904401db2142133c148b148
ACR-96fd54bfa9ba4d7b91bbcc670b1b16b8
ACR-53df1ad0469a4cb2b53511bf8ab70c54
ACR-198b023ac5574b649a4efd4c143a49e2
ACR-1eaec76b675e403396e9b9820613c110
ACR-f0387975987744a58473e5b2c12443b8
ACR-407feb25fb7c4a4f9d95548e1551079c
ACR-f7122f9cb3aa44ad8b4b5a04c78506ad
ACR-2548edb9db8a475e9c50c49d025534e8
ACR-b4c00fadb7e54bc5b9ccc99bf96b5bd7
ACR-15e245f068854a24b481c09328ca84ec
ACR-b5bfa21fba844b4a977fae742606e995
ACR-6a8d81402d3b48ba948e5ad8d4f6b86c
ACR-4b015ce9e2e24a668fcb3bba62d44a06
ACR-0124b1d8d620410cb87ace31cd0f8cc8
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
