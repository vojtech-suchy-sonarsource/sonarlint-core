/*
ACR-8c9eb520e53f43d688aa819e0146f982
ACR-d5a2d8b056a2466780ae12dded8cac07
ACR-7ad67ba2bc0644d99fd503169a2ffc9b
ACR-7aa4b4a1d6e94a05990f648103c07d24
ACR-2f296c595f0a4956a298e6fb70512d3b
ACR-59d1049bd42b46b4ae1ac86cf5f35c1f
ACR-1073e74e8e5f453ab6cf42a0d05ac85b
ACR-725efdffa9f2401b9b0dca3f5e94f682
ACR-207fa0daa8124723bc06aad7a5a67a28
ACR-537d26397c664b67b5ead4ee9ef8dc3f
ACR-ffdb987f5a63443985271a4585ad7f5a
ACR-66178e7a5a604a14be52858fa7753165
ACR-8016c290e09a4653a5a68dd3e9c0967b
ACR-78fc62abc594405587e7999cd44b14dd
ACR-c91183eb66584967a043a31c8c867829
ACR-653d3e6caac24eb08a255a40535900e8
ACR-a51bda95d6e2404b90ce97f8531d276a
 */
package org.sonarsource.sonarlint.core.rules;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.commons.ImpactSeverity;
import org.sonarsource.sonarlint.core.commons.SoftwareQuality;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.rules.RulesRepository;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.RuleDefinitionDto;
import org.sonarsource.sonarlint.core.serverapi.push.parsing.common.ImpactPayload;
import org.sonarsource.sonarlint.core.storage.StorageService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.sonarsource.sonarlint.core.rules.RulesFixtures.aRule;

class RulesServiceTests {

  private RulesRepository rulesRepository;
  private RulesExtractionHelper extractionHelper;

  @BeforeEach
  void prepare() {
    extractionHelper = mock(RulesExtractionHelper.class);
    var configurationRepository = mock(ConfigurationRepository.class);
    var storageService = mock(StorageService.class);
    rulesRepository = new RulesRepository(extractionHelper, configurationRepository, storageService);
  }

  @Test
  void it_should_return_all_embedded_rules_from_the_repository() {
    when(extractionHelper.extractEmbeddedRules()).thenReturn(List.of(aRule()));
    var rulesService = new RulesService(rulesRepository);

    var embeddedRules = rulesService.listAllStandaloneRulesDefinitions().values();

    assertThat(embeddedRules)
      .extracting(RuleDefinitionDto::getKey, RuleDefinitionDto::getName)
      .containsExactly(tuple("repo:ruleKey", "ruleName"));
  }

  @Test
  void it_should_only_override_overridden_impact_quality() {
    Map<SoftwareQuality, ImpactSeverity> defaultImpacts = Map.of(
      SoftwareQuality.MAINTAINABILITY, ImpactSeverity.LOW,
      SoftwareQuality.RELIABILITY, ImpactSeverity.MEDIUM);

    List<ImpactPayload> overriddenImpacts = List.of(
      new ImpactPayload("MAINTAINABILITY", "HIGH"));

    Map<SoftwareQuality, ImpactSeverity> result = RuleDetails.mergeImpacts(defaultImpacts, overriddenImpacts);
    assertThat(result)
      .containsEntry(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.HIGH)
      .containsEntry(SoftwareQuality.RELIABILITY, ImpactSeverity.MEDIUM);
  }

  @Test
  void it_should_work_when_no_overridden_impacts() {
    Map<SoftwareQuality, ImpactSeverity> defaultImpacts = Map.of(
      SoftwareQuality.MAINTAINABILITY, ImpactSeverity.LOW,
      SoftwareQuality.RELIABILITY, ImpactSeverity.MEDIUM);

    Map<SoftwareQuality, ImpactSeverity> result = RuleDetails.mergeImpacts(defaultImpacts, List.of());

    assertThat(result).isEqualTo(defaultImpacts);
  }

}
