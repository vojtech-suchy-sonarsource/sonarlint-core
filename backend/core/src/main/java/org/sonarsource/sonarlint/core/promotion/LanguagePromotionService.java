/*
ACR-84420d9cb8594120a082b290a5ea6313
ACR-2e8e0c1d7eb3401a99a656b4f6d0177c
ACR-b30ae5607687469a9c6771ac47c63b6e
ACR-7ab5fa548b0e43bdbe00332c22389006
ACR-9c11b64262774ce9b51135a6bcf155bc
ACR-838d913196ec49c8b7d061923dd96dea
ACR-97ac039c8ad54c4a84ff13d7fbe12fa1
ACR-3e6e3a46761043ec8130164f152a46a9
ACR-d47460be2a48448ebaeab06076d7dc47
ACR-fa739e00d1f143a8bc50c4beef2e2934
ACR-5157e07ea0d44077a09f1808cfdbbb4b
ACR-b3a9ebb7b59e48e38a695ccdb8a462cd
ACR-1acf206760924aec86a39e189613dde9
ACR-2c6249505ff84fe8838090b39ea046d3
ACR-471f0eb17156410b9228d4f76d2ad9ff
ACR-2a6306a0ae2c495ca718473dacdd4705
ACR-35d469d30dde47f9b20aad4a33da21d9
 */
package org.sonarsource.sonarlint.core.promotion;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.sonarsource.sonarlint.core.analysis.AnalysisFinishedEvent;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.promotion.PromoteExtraEnabledLanguagesInConnectedModeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;
import org.springframework.context.event.EventListener;

public class LanguagePromotionService {
  private final ConfigurationRepository configurationRepository;
  private final Set<Language> extraEnabledLanguagesInConnectedMode;
  private final SonarLintRpcClient client;

  public LanguagePromotionService(ConfigurationRepository configurationRepository, InitializeParams initializeParams, SonarLintRpcClient client) {
    this.configurationRepository = configurationRepository;
    this.extraEnabledLanguagesInConnectedMode = initializeParams.getExtraEnabledLanguagesInConnectedMode();
    this.client = client;
  }

  @EventListener
  public void onAnalysisFinished(AnalysisFinishedEvent event) {
    var configurationScopeId = event.getConfigurationScopeId();
    if (isStandalone(configurationScopeId)) {
      var languagesToPromote = getLanguagesToPromote(event.getDetectedLanguages());
      if (!languagesToPromote.isEmpty()) {
        client.promoteExtraEnabledLanguagesInConnectedMode(new PromoteExtraEnabledLanguagesInConnectedModeParams(configurationScopeId, languagesToPromote));
      }
    }
  }

  private boolean isStandalone(String configurationScopeId) {
    return configurationRepository.getEffectiveBinding(configurationScopeId).isEmpty();
  }

  private Set<Language> getLanguagesToPromote(Set<SonarLanguage> detectedLanguages) {
    var languagesToPromote = detectedLanguages.stream().map(sonarLanguage -> Language.valueOf(sonarLanguage.name())).collect(Collectors.toCollection(HashSet::new));
    languagesToPromote.retainAll(extraEnabledLanguagesInConnectedMode);
    return languagesToPromote;
  }
}
