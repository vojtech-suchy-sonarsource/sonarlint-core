/*
ACR-7545ccda9bc3445cb38eb10ac62fade5
ACR-ad57d8e7c5b643e3840bef1a98f6bdb0
ACR-cd432dafdc874ff2bf3b5b4f51b4b899
ACR-3b99fa4cad0e4dfc9926670408c7af63
ACR-a1ca678f18a348b183d5a22806a6abb9
ACR-169e8ca342cf4207b72ac39218c26bc4
ACR-c9adde999edc4b678ae5b49ef53312ee
ACR-685cfecd363b496da376f2d5a1573370
ACR-85499639b8014db49ca9bbc4c1717232
ACR-e9c7349d63c44ad78e080426fddf712c
ACR-7719335e5d8a4db18ab8c10b344578a8
ACR-39149d737e6e4071a509b93e8e22ac08
ACR-9ceaca596c9f41ee9b4ff21eafb0f73a
ACR-d0aaad09419b4600a225d8e0b46b0390
ACR-8c84932251824f658f8ac12e99d1d82d
ACR-0f13767fba5149ceab4caecc2cdd4b75
ACR-0272bc1b6fb34039ad87ee52aad9ff35
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
