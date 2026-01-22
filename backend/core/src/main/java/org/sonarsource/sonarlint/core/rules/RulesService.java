/*
ACR-a747b6d76df14d98a5f3485525359129
ACR-32a1da2adea741ddb96ba3c28ca88c37
ACR-4906fafcdd0746bf94f67cec6a58eb0d
ACR-bfa6abe8a2af473c80470a32c0f9c26c
ACR-ffb82c6047664ab2aab8d0a26921ca94
ACR-1b7abc76d2db4686be6e59d4aba68118
ACR-f81b1e4a11864d2691a207671265e9ac
ACR-c0138007126e4b84913e164622f3cf56
ACR-4f90f44d903c4c37946b3465409a0aa9
ACR-6e1fa9fd781f4995b3d46a8a5964217b
ACR-7dc72557b2754a0c92c9f952de682aaf
ACR-13f5b51d0c8445c3a588be2c2ddb4b23
ACR-f58809f3bbe349e69384a9a8546f84c2
ACR-50f7bed5af8e47cc9f5d236e9b7925ab
ACR-22f4cfea63c34685ab6374bbc1504938
ACR-def884877309443a825d0aa044f74112
ACR-b6bb140228424c8b8fc33bc3516c505c
 */
package org.sonarsource.sonarlint.core.rules;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.repository.rules.RulesRepository;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.RuleDefinitionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.RuleParamDefinitionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.RuleParamType;
import org.sonarsource.sonarlint.core.rpc.protocol.common.CleanCodeAttribute;
import org.sonarsource.sonarlint.core.rule.extractor.SonarLintRuleDefinition;
import org.sonarsource.sonarlint.core.rule.extractor.SonarLintRuleParamDefinition;
import org.sonarsource.sonarlint.core.rule.extractor.SonarLintRuleParamType;

import static org.sonarsource.sonarlint.core.rules.RuleDetailsAdapter.adapt;
import static org.sonarsource.sonarlint.core.rules.RuleDetailsAdapter.toDto;

@Named
@Singleton
public class RulesService {

  private static final SonarLintLogger LOG = SonarLintLogger.get();
  public static final String IN_EMBEDDED_RULES = "' in embedded rules";
  private final RulesRepository rulesRepository;
  public static final String COULD_NOT_FIND_RULE = "Could not find rule '";

  public RulesService(RulesRepository rulesRepository) {
    this.rulesRepository = rulesRepository;
  }

  public Map<String, RuleDefinitionDto> listAllStandaloneRulesDefinitions() {
    return rulesRepository.getEmbeddedRules()
      .stream()
      .map(RulesService::convert)
      .collect(Collectors.toMap(RuleDefinitionDto::getKey, r -> r));
  }

  @NotNull
  public static RuleDefinitionDto convert(SonarLintRuleDefinition r) {
    var cleanCodeAttribute = r.getCleanCodeAttribute().map(RuleDetailsAdapter::adapt).orElse(CleanCodeAttribute.CONVENTIONAL);
    return new RuleDefinitionDto(r.getKey(), r.getName(), cleanCodeAttribute, toDto(r.getDefaultImpacts()),
      convert(r.getParams()), r.isActiveByDefault(), adapt(r.getLanguage()));
  }

  private static Map<String, RuleParamDefinitionDto> convert(Map<String, SonarLintRuleParamDefinition> params) {
    return params.values().stream().map(RulesService::convert).collect(Collectors.toMap(RuleParamDefinitionDto::getKey, r -> r));
  }

  private static RuleParamDefinitionDto convert(SonarLintRuleParamDefinition paramDef) {
    return new RuleParamDefinitionDto(paramDef.key(), paramDef.name(), paramDef.description(), paramDef.defaultValue(), convert(paramDef.type()), paramDef.multiple(),
      paramDef.possibleValues());
  }

  private static RuleParamType convert(SonarLintRuleParamType type) {
    try {
      return RuleParamType.valueOf(type.name());
    } catch (IllegalArgumentException unknownType) {
      LOG.warn("Unknown parameter type: {}", type.name());
      return RuleParamType.STRING;
    }
  }

}
