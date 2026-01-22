/*
ACR-dddf654b7aec495fbf1ab60030107a61
ACR-6ab603ffc605448bb166ab13eecc0ca2
ACR-ec2647f205404a6ab68b83b9493741ea
ACR-37b912739c5b41b1a400d0fe98031d0b
ACR-4022e6e4b01b49d6966946025283bd7a
ACR-be11169dbb3843d88a94fde63853c692
ACR-770a798103824960b46b5e307c490808
ACR-8acb124733524d29924d002bbb435006
ACR-65790e08dd354b848762fa45a8fec119
ACR-5aece4c291c749bcba10605a59a3e41e
ACR-afe5058499bd4fb1a74d5042860ad4b9
ACR-1ddc0975eefb4fbf93fe4c6149bc3c6d
ACR-e69a5d2ea054494b8f98998d62911f10
ACR-a388afc158224497a0fa137122b24270
ACR-a3187c28458c48e3837349c1e119cbcb
ACR-520856a3cc514525987247797c0b55db
ACR-471b52d323e04591b216ef6bfa15af77
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
