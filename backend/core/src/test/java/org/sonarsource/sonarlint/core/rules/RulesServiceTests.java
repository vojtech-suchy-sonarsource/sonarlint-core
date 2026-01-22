/*
ACR-2e8839d30147428daea108dd45476cc4
ACR-69038a02ca704bf6a563cc9091a1c69d
ACR-796a18bcbd69472eb3b6484a559dccf5
ACR-06dcdb9d883844dfbf4db844fea852c7
ACR-627273c21acc48e086f387cdb3ce19b7
ACR-456715b519984681a31c203384baf598
ACR-a76420db08414153bcc1f50bf56bd8b8
ACR-7faa31f0e0e349c19f2e831c8ec54952
ACR-1114fd691df949d49be4921c28abc0a3
ACR-7a43ab631a804ad5a0dbb124871ece2d
ACR-dd4036bf868b42e3b193cf662a3616cd
ACR-76ca3ed90b9943c384a1b9b2bc74a1bc
ACR-c00b59def7b44c1481663407f0be7e26
ACR-169cd3a0273d4160826625f0bb042d3d
ACR-a745f0929b33415a9fe1f318935a3287
ACR-ddaddddf53fe43d4915c1554f464c426
ACR-1287dabfed6a434793d85ae592fac556
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
