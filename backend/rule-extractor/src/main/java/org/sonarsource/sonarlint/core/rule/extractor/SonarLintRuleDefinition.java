/*
ACR-7289e414debc4240a94c7972cdf50ddd
ACR-409415ee88ca4ccf923cb7b743d48c02
ACR-2ec253a378984954bf1039055f64528b
ACR-8f4a892d795e4e2f9c11a011145ee301
ACR-bc939483a71e41b6b892121050411a33
ACR-57fd55f3d6e649b0b166748be5071433
ACR-1b3f0a0f7d88413e99bdda8576e2ec7e
ACR-635814354b0044a7b7a22c499156ec7d
ACR-f8f8dea462ee460c99a8e86fa83c9017
ACR-f1429f15d82b4529b6b2d9a9c3ac0247
ACR-b99519860f904bd0a633fee4229c383f
ACR-1442e8f07c1c4c3fb9725a476cea3604
ACR-8a9f3b2a33f54c3caa70e3764eaf1265
ACR-731dce9541e74cbf8a8cbdc1b906131f
ACR-e95f1642c0514519b697cb76e5de8a28
ACR-b2751952342d49aa971542cc785a7cfe
ACR-0a800500f02b4699ae9f1c9946e999b8
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
  //ACR-bbbcef8fc3c7467392b40ef771b6d7a3
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
