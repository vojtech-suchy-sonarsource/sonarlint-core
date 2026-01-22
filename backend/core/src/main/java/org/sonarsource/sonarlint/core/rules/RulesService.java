/*
ACR-c75e30ef901e45ccbaae3d11dc77e0db
ACR-ef5bda1fa7bc4e3c89842441ba9e5ad2
ACR-026ce2f60f6f48a68fa7a4b14b415c62
ACR-ad1d07e8213c4e8f852933bb8a964de7
ACR-835fa5835558472e9fef55dc473fef8e
ACR-323658b3345c4b9db127afad28b55dba
ACR-d0ac38a3b26242b28f8ef7b41037540b
ACR-f213260a5211423588f2657d787aad4a
ACR-f1ec41d570e14828814290cfe4c96488
ACR-18285999402346eeae7b95e4318da82b
ACR-37419a2c32cf4a64b03d74ee8ce102a0
ACR-a4e245e01d7b45e89d30bd27a7b16f76
ACR-94d862257b5e4d68ab3e99e70a1a6ad6
ACR-fcff690d582e49899ccb62d2393386d6
ACR-0d0255573d76401c8fff9d85487391ba
ACR-823f91aa5b9a40c5b5e3a59a5c1334c3
ACR-6cb8132f81f942c2b51eb4d7add7b20a
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
