/*
ACR-524cc5198a6a42c0a131f50e273e6e4c
ACR-1a9cd5afa7f344dc855bd45204a9ff34
ACR-797e66c7cb0d41bebff21fda7ebcbd1f
ACR-2bc0ea2863d94f2386b9aec7f8aeedf1
ACR-689c854371c04bf088233a9b9eef9ee6
ACR-037689c7146a42e1aacdfe5adb60c177
ACR-aa54e49ca434448295cb21f4f2747dde
ACR-f5f075deff5d42d7be03cc224c8385de
ACR-acf05193a40f45c497f376325e316abc
ACR-c6dd530b92604674a4077cdee3f21cd7
ACR-750266d1d674491ba6e9d7335df7759c
ACR-928f212ff79a4b91b4691f9cc55e64cd
ACR-63767414d9204c4798c2b501ac3f3d6c
ACR-9a92fd22f8b641179e441766119c3a26
ACR-63050897cc0a4c83a2b43828b8b2ad24
ACR-160d03a98c914c01ac09c1e028a7e2b4
ACR-1ee45cd32ce34b75a6d1629b7f2e4f06
 */
package org.sonarsource.sonarlint.core.rule.extractor;

import java.util.Collections;
import java.util.List;
import javax.annotation.CheckForNull;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.api.server.rule.RulesDefinition.Param;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

public class SonarLintRuleParamDefinition {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final String key;
  private final String name;
  private final String description;
  private final String defaultValue;
  private final SonarLintRuleParamType type;
  private final boolean multiple;
  private final List<String> possibleValues;

  public SonarLintRuleParamDefinition(Param param) {
    this.key = param.key();
    this.name = param.name();
    this.description = param.description();
    this.defaultValue = param.defaultValue();
    var apiType = param.type();
    this.type = from(apiType);
    this.multiple = apiType.multiple();
    this.possibleValues = Collections.unmodifiableList(apiType.values());
  }

  private static SonarLintRuleParamType from(RuleParamType apiType) {
    try {
      return SonarLintRuleParamType.valueOf(apiType.type());
    } catch (IllegalArgumentException unknownType) {
      LOG.warn("Unknown parameter type: " + apiType.type());
      return SonarLintRuleParamType.STRING;
    }
  }

  public String key() {
    return key;
  }

  public String name() {
    return name;
  }

  public String description() {
    return description;
  }

  @CheckForNull
  public String defaultValue() {
    return defaultValue;
  }

  public SonarLintRuleParamType type() {
    return type;
  }

  public boolean multiple() {
    return multiple;
  }

  public List<String> possibleValues() {
    return possibleValues;
  }
}
