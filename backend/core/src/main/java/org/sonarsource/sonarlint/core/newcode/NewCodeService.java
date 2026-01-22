/*
ACR-898e7d845cbe41b0a84f888d126e7b44
ACR-8ab72d1f46984ee689c7fdd072ff3e35
ACR-2f636ba4b1b34db581fab766ac3f7cd2
ACR-2d92a8fe24984bf1ab3c7da0d32976cf
ACR-0e7b7cedc5104f06b3e5657968278a8e
ACR-592486d520ed4dff9f8c6aca47afeee8
ACR-e63a730328854eeca0eb1340113f63d6
ACR-bb3a40dc44474be5986126a585e97c41
ACR-aa602ebae56947bdb72485c36c9975a8
ACR-0d3bf502f9ed415ba2581f94ac0e38db
ACR-69535d56d10a4a1f8f289993f33833bf
ACR-e20ced105c8e4e2db0a28964e8d5bc48
ACR-da57e28c205940ba9f5a450c6a0acb7f
ACR-95c216c93ad940218d03d25ba86b511d
ACR-a0d33430100b4bc6b182b46616a067d3
ACR-4caf56eb4d574dda9cc17501c121aba3
ACR-f3d6a2383697436e9551557eac4e1e2d
 */
package org.sonarsource.sonarlint.core.newcode;

import java.util.Optional;
import org.sonarsource.sonarlint.core.commons.NewCodeDefinition;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.newcode.GetNewCodeDefinitionResponse;
import org.sonarsource.sonarlint.core.storage.StorageService;
import org.sonarsource.sonarlint.core.telemetry.TelemetryService;

public class NewCodeService {
  private static final NewCodeDefinition STANDALONE_NEW_CODE_DEFINITION = NewCodeDefinition.withExactNumberOfDays(30);
  private final ConfigurationRepository configurationRepository;
  private final StorageService storageService;
  private final TelemetryService telemetryService;

  public NewCodeService(ConfigurationRepository configurationRepository, StorageService storageService, TelemetryService telemetryService) {
    this.configurationRepository = configurationRepository;
    this.storageService = storageService;
    this.telemetryService = telemetryService;
  }

  public GetNewCodeDefinitionResponse getNewCodeDefinition(String configScopeId) {
    return getFullNewCodeDefinition(configScopeId)
      .map(newCodeDefinition -> new GetNewCodeDefinitionResponse(newCodeDefinition.toString(), newCodeDefinition.isSupported()))
      .orElse(new GetNewCodeDefinitionResponse("No new code definition found", false));
  }

  public Optional<NewCodeDefinition> getFullNewCodeDefinition(String configScopeId) {
    var effectiveBinding = configurationRepository.getEffectiveBinding(configScopeId);
    if (effectiveBinding.isEmpty()) {
      return Optional.of(STANDALONE_NEW_CODE_DEFINITION);
    }
    var binding = effectiveBinding.get();
    var sonarProjectStorage = storageService.binding(binding);
    return sonarProjectStorage.newCodeDefinition().read();
  }

  public void didToggleFocus() {
    telemetryService.newCodeFocusChanged();
  }
}
