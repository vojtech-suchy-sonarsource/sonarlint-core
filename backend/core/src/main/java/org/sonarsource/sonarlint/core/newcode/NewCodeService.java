/*
ACR-80bcd371b94d4419918be55a652fe736
ACR-7d5128deedf54f8ea9de871ca635f101
ACR-fb7fd1c7b27147b19e85fb977d563778
ACR-f887beae5ad045cbaf18b24991101d53
ACR-76a858e508fd4fd09d3af11bbc951d2a
ACR-7b0ed4582c074f7fbb1673060ac25967
ACR-70890393d548471e94b08d7d9c09d666
ACR-374d0c536e6142ed88b42fa9ad9860ed
ACR-a50d1f1caf0a4053a405c2900e18cd68
ACR-c0f68889f19a454993c950f67da94617
ACR-b0c93ae89a36418e83a224fbc7268e94
ACR-bd0ba8e265ed4353b5c4e343be64b3c5
ACR-4eca1cb3ffc74041aa095559c2469ce7
ACR-3cf2f041a1814718bb36a5dee2af83bc
ACR-95509017641a4c299994dc82e7b58191
ACR-8a12e0421f354e23b55805e4e4cd7604
ACR-bdeeca095bf1424dae58bd830f822a18
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
