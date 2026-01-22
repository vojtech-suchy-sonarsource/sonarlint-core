/*
ACR-c1646cd34cb84536a8f66a989fdfb2ae
ACR-c9c249a72ec64ed188bf74f9149363d3
ACR-69832e84e04646248c772679f6b44ab6
ACR-561f86324a624b06b263e2bec08e2c0c
ACR-63c3b832e1f140a39d73946561843739
ACR-a8e6d79cffdf4a16add87bd02808e7cb
ACR-aeec686f9cff419ba1bdb95c01362425
ACR-1e27ebe08f4f4b2880245e56cdc9aebc
ACR-9411e04f8b9f41bea7d5f79cc751eb0b
ACR-00615454ca3f43d7a053ed20fe901c31
ACR-4501f607159c4cde8590b77c502c2bae
ACR-994f57fc59f543eeb8612fdda7de105e
ACR-6ab8bb6e04aa41caa1748f3867a35cc2
ACR-bd2b874a8eec416aa3412a8efa0efd72
ACR-8e2145b236f845f8a89812d3c76e8ce9
ACR-b94fbd068cdd4bf8a4526839ffb0641e
ACR-f30ff1b86bde4226aa3a575b55dbfad2
 */
package org.sonarsource.sonarlint.core.rule.extractor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.Param;
import org.sonar.markdown.Markdown;
import org.sonarsource.sonarlint.core.commons.CleanCodeAttribute;
import org.sonarsource.sonarlint.core.commons.ImpactSeverity;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.SoftwareQuality;
import org.sonarsource.sonarlint.core.commons.VulnerabilityProbability;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;

import static java.util.stream.Collectors.toSet;
import static org.sonarsource.sonarlint.core.rule.extractor.SecurityStandards.fromSecurityStandards;

public class SonarLintRuleDefinition {

  private final String key;
  private final String name;
  private final IssueSeverity defaultSeverity;
  private final RuleType type;
  private final CleanCodeAttribute cleanCodeAttribute;
  private final Map<SoftwareQuality, ImpactSeverity> defaultImpacts;
  private final String description;
  private final List<SonarLintRuleDescriptionSection> descriptionSections;
  private final Map<String, SonarLintRuleParamDefinition> params;
  private final Map<String, String> defaultParams = new HashMap<>();
  private final boolean isActiveByDefault;
  private final SonarLanguage language;
  private final String[] tags;
  private final Set<String> deprecatedKeys;
  private final Set<String> educationPrincipleKeys;
  private final Optional<String> internalKey;
  //ACR-96c7f468d66b4817a7bedfb085e16f0d
  private final Optional<VulnerabilityProbability> vulnerabilityProbability;

  public SonarLintRuleDefinition(RulesDefinition.Rule rule) {
    this.key = RuleKey.of(rule.repository().key(), rule.key()).toString();
    this.name = rule.name();
    this.defaultSeverity = IssueSeverity.valueOf(rule.severity());
    this.type = RuleType.valueOf(rule.type().name());
    this.cleanCodeAttribute = Optional.ofNullable(rule.cleanCodeAttribute()).map(Enum::name).map(CleanCodeAttribute::valueOf)
      .orElse(CleanCodeAttribute.defaultCleanCodeAttribute());
    this.defaultImpacts = rule.defaultImpacts().entrySet()
      .stream()
      .map(e -> Map.entry(SoftwareQuality.valueOf(e.getKey().name()), ImpactSeverity.valueOf(e.getValue().name())))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    var htmlDescription = rule.htmlDescription() != null ? rule.htmlDescription() : Markdown.convertToHtml(rule.markdownDescription());
    if (rule.type() == org.sonar.api.rules.RuleType.SECURITY_HOTSPOT) {
      this.description = null;
      this.descriptionSections = LegacyHotspotRuleDescriptionSectionsGenerator.extractDescriptionSectionsFromHtml(htmlDescription);
    } else {
      this.description = htmlDescription;
      this.descriptionSections = rule.ruleDescriptionSections().stream().map(s -> new SonarLintRuleDescriptionSection(s.getKey(), s.getHtmlContent(),
        s.getContext().map(c -> new SonarLintRuleDescriptionSection.Context(c.getKey(), c.getDisplayName())))).toList();
    }

    this.isActiveByDefault = rule.activatedByDefault();
    this.language = SonarLanguage.forKey(rule.repository().language()).orElseThrow(() -> new IllegalStateException("Unknown language with key: " + rule.repository().language()));
    this.tags = rule.tags().toArray(new String[0]);
    this.deprecatedKeys = rule.deprecatedRuleKeys().stream().map(RuleKey::toString).collect(toSet());
    this.educationPrincipleKeys = rule.educationPrincipleKeys();
    this.vulnerabilityProbability =
      rule.type() == org.sonar.api.rules.RuleType.SECURITY_HOTSPOT ?
        Optional.of(fromSecurityStandards(rule.securityStandards()).getSlCategory().getVulnerability()) : Optional.empty();
    Map<String, SonarLintRuleParamDefinition> builder = new HashMap<>();
    for (Param param : rule.params()) {
      var paramDefinition = new SonarLintRuleParamDefinition(param);
      builder.put(param.key(), paramDefinition);
      var defaultValue = paramDefinition.defaultValue();
      if (defaultValue != null) {
        defaultParams.put(param.key(), defaultValue);
      }
    }
    params = Collections.unmodifiableMap(builder);
    this.internalKey = Optional.ofNullable(rule.internalKey());
  }

  public String getKey() {
    return key;
  }

  public String getName() {
    return name;
  }

  public IssueSeverity getDefaultSeverity() {
    return defaultSeverity;
  }

  public RuleType getType() {
    return type;
  }

  public Optional<CleanCodeAttribute> getCleanCodeAttribute() {
    return Optional.ofNullable(cleanCodeAttribute);
  }

  public Map<SoftwareQuality, ImpactSeverity> getDefaultImpacts() {
    return defaultImpacts;
  }

  public Map<String, SonarLintRuleParamDefinition> getParams() {
    return params;
  }

  public Map<String, String> getDefaultParams() {
    return defaultParams;
  }

  public boolean isActiveByDefault() {
    return isActiveByDefault;
  }

  public String getHtmlDescription() {
    return description;
  }

  public List<SonarLintRuleDescriptionSection> getDescriptionSections() {
    return descriptionSections;
  }

  public SonarLanguage getLanguage() {
    return language;
  }

  public String[] getTags() {
    return tags;
  }

  public Set<String> getDeprecatedKeys() {
    return deprecatedKeys;
  }

  public Set<String> getEducationPrincipleKeys() {
    return educationPrincipleKeys;
  }

  public Optional<String> getInternalKey() {
    return internalKey;
  }

  public Optional<VulnerabilityProbability> getVulnerabilityProbability() {
    return vulnerabilityProbability;
  }
}
