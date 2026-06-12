/*
 * SonarLint Core - Implementation
 * Copyright (C) 2016-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarsource.sonarlint.core.rules;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import org.sonarsource.sonarlint.core.commons.CleanCodeAttribute;
import org.sonarsource.sonarlint.core.commons.ImpactSeverity;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.SoftwareQuality;
import org.sonarsource.sonarlint.core.commons.VulnerabilityProbability;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.StandaloneRuleConfigDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking.TaintVulnerabilityDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedFindingDto;
import org.sonarsource.sonarlint.core.rule.extractor.SonarLintRuleDefinition;
import org.sonarsource.sonarlint.core.rule.extractor.SonarLintRuleParamDefinition;
import org.sonarsource.sonarlint.core.serverapi.push.parsing.common.ImpactPayload;
import org.sonarsource.sonarlint.core.serverapi.rules.ServerActiveRule;
import org.sonarsource.sonarlint.core.serverapi.rules.ServerRule;

public class RuleDetails {

  public static final String DEFAULT_SECTION = "default";

  private final String key;
  private final SonarLanguage language;
  private final String name;
  private final String htmlDescription;
  private final Map<String, List<DescriptionSection>> descriptionSectionsByKey;
  private final IssueSeverity defaultSeverity;
  private final RuleType type;
  private final CleanCodeAttribute cleanCodeAttribute;
  private final Map<SoftwareQuality, ImpactSeverity> impacts;
  private final Collection<EffectiveRuleParam> params;
  private final String extendedDescription;
  private final Set<String> educationPrincipleKeys;
  private final VulnerabilityProbability vulnerabilityProbability;

  private RuleDetails(Builder builder) {
    this.key = builder.key;
    this.language = builder.language;
    this.name = builder.name;
    this.htmlDescription = builder.htmlDescription;
    this.descriptionSectionsByKey = builder.descriptionSectionsByKey;
    this.defaultSeverity = builder.defaultSeverity;
    this.type = builder.type;
    this.cleanCodeAttribute = builder.cleanCodeAttribute;
    this.impacts = builder.impacts;
    this.params = builder.params;
    this.extendedDescription = builder.extendedDescription;
    this.educationPrincipleKeys = builder.educationPrincipleKeys;
    this.vulnerabilityProbability = builder.vulnerabilityProbability;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String key;
    private SonarLanguage language;
    private String name;
    private String htmlDescription;
    private Map<String, List<DescriptionSection>> descriptionSectionsByKey = Map.of();
    private IssueSeverity defaultSeverity;
    private RuleType type;
    private CleanCodeAttribute cleanCodeAttribute;
    private Map<SoftwareQuality, ImpactSeverity> impacts = Map.of();
    private Collection<EffectiveRuleParam> params = Collections.emptyList();
    private String extendedDescription;
    private Set<String> educationPrincipleKeys = Set.of();
    private VulnerabilityProbability vulnerabilityProbability;

    public Builder setKey(String key) {
      this.key = key;
      return this;
    }

    public Builder setLanguage(SonarLanguage language) {
      this.language = language;
      return this;
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setHtmlDescription(String htmlDescription) {
      this.htmlDescription = htmlDescription;
      return this;
    }

    public Builder setDescriptionSectionsByKey(Map<String, List<DescriptionSection>> descriptionSectionsByKey) {
      this.descriptionSectionsByKey = descriptionSectionsByKey;
      return this;
    }

    public Builder setDefaultSeverity(@Nullable IssueSeverity defaultSeverity) {
      this.defaultSeverity = defaultSeverity;
      return this;
    }

    public Builder setType(@Nullable RuleType type) {
      this.type = type;
      return this;
    }

    public Builder setCleanCodeAttribute(@Nullable CleanCodeAttribute cleanCodeAttribute) {
      this.cleanCodeAttribute = cleanCodeAttribute;
      return this;
    }

    public Builder setImpacts(Map<SoftwareQuality, ImpactSeverity> impacts) {
      this.impacts = impacts;
      return this;
    }

    public Builder setParams(Collection<EffectiveRuleParam> params) {
      this.params = params;
      return this;
    }

    public Builder setExtendedDescription(@Nullable String extendedDescription) {
      this.extendedDescription = extendedDescription;
      return this;
    }

    public Builder setEducationPrincipleKeys(Set<String> educationPrincipleKeys) {
      this.educationPrincipleKeys = educationPrincipleKeys;
      return this;
    }

    public Builder setVulnerabilityProbability(@Nullable VulnerabilityProbability vulnerabilityProbability) {
      this.vulnerabilityProbability = vulnerabilityProbability;
      return this;
    }

    public RuleDetails build() {
      return new RuleDetails(this);
    }
  }

  public static RuleDetails from(SonarLintRuleDefinition ruleDefinition, @Nullable StandaloneRuleConfigDto ruleConfig) {
    return builder()
      .setKey(ruleDefinition.getKey())
      .setLanguage(ruleDefinition.getLanguage())
      .setName(ruleDefinition.getName())
      .setHtmlDescription(ruleDefinition.getHtmlDescription())
      .setDescriptionSectionsByKey(ruleDefinition.getDescriptionSections().stream()
        .map(s -> new DescriptionSection(s.getKey(), s.getHtmlContent(), s.getContext().map(c -> new DescriptionSection.Context(c.getKey(), c.getDisplayName()))))
        .collect(Collectors.groupingBy(DescriptionSection::getKey)))
      .setImpacts(ruleDefinition.getDefaultImpacts())
      .setDefaultSeverity(ruleDefinition.getDefaultSeverity())
      .setType(ruleDefinition.getType())
      .setCleanCodeAttribute(ruleDefinition.getCleanCodeAttribute().orElse(CleanCodeAttribute.defaultCleanCodeAttribute()))
      .setParams(transformParams(ruleDefinition.getParams(), ruleConfig != null ? ruleConfig.getParamValueByKey() : Map.of()))
      .setEducationPrincipleKeys(ruleDefinition.getEducationPrincipleKeys())
      .setVulnerabilityProbability(ruleDefinition.getVulnerabilityProbability().orElse(null))
      .build();
  }

  @NotNull
  private static List<EffectiveRuleParam> transformParams(Map<String, SonarLintRuleParamDefinition> ruleDefinitionParams, Map<String, String> ruleConfigParams) {
    return ruleDefinitionParams.values()
      .stream()
      .map(p -> new EffectiveRuleParam(p.name(), p.description(), ruleConfigParams.getOrDefault(p.key(), p.defaultValue()), p.defaultValue()))
      .toList();
  }

  public static RuleDetails merging(ServerActiveRule activeRuleFromStorage, ServerRule serverRule) {
    return builder()
      .setKey(activeRuleFromStorage.getRuleKey())
      .setLanguage(serverRule.getLanguage())
      .setName(serverRule.getName())
      .setHtmlDescription(serverRule.getHtmlDesc())
      .setDescriptionSectionsByKey(serverRule.getDescriptionSections().stream()
        .map(s -> new DescriptionSection(s.getKey(), s.getHtmlContent(), s.getContext().map(c -> new DescriptionSection.Context(c.getKey(), c.getDisplayName()))))
        .collect(Collectors.groupingBy(DescriptionSection::getKey)))
      .setImpacts(serverRule.getImpacts())
      .setDefaultSeverity(Optional.ofNullable(activeRuleFromStorage.getSeverity()).orElse(serverRule.getSeverity()))
      .setType(serverRule.getType())
      .setCleanCodeAttribute(serverRule.getCleanCodeAttribute())
      .setExtendedDescription(serverRule.getHtmlNote())
      .setEducationPrincipleKeys(serverRule.getEducationPrincipleKeys())
      // TODO get vulnerability probability from storage?
      .build();
  }

  public static RuleDetails merging(ServerRule activeRuleFromServer, SonarLintRuleDefinition ruleDefFromPlugin, boolean skipCleanCodeTaxonomy) {
    var cleanCodeAttribute = skipCleanCodeTaxonomy ? null : ruleDefFromPlugin.getCleanCodeAttribute().orElse(CleanCodeAttribute.defaultCleanCodeAttribute());
    var defaultImpacts = skipCleanCodeTaxonomy ? Map.<SoftwareQuality, ImpactSeverity>of() : ruleDefFromPlugin.getDefaultImpacts();
    return builder()
      .setKey(ruleDefFromPlugin.getKey())
      .setLanguage(ruleDefFromPlugin.getLanguage())
      .setName(ruleDefFromPlugin.getName())
      .setHtmlDescription(ruleDefFromPlugin.getHtmlDescription())
      .setDescriptionSectionsByKey(ruleDefFromPlugin.getDescriptionSections().stream()
        .map(s -> new DescriptionSection(s.getKey(), s.getHtmlContent(), s.getContext().map(c -> new DescriptionSection.Context(c.getKey(), c.getDisplayName()))))
        .collect(Collectors.groupingBy(DescriptionSection::getKey)))
      .setImpacts(defaultImpacts)
      .setDefaultSeverity(Optional.ofNullable(activeRuleFromServer.getSeverity()).orElse(ruleDefFromPlugin.getDefaultSeverity()))
      .setType(ruleDefFromPlugin.getType())
      .setCleanCodeAttribute(cleanCodeAttribute)
      .setExtendedDescription(activeRuleFromServer.getHtmlNote())
      .setEducationPrincipleKeys(ruleDefFromPlugin.getEducationPrincipleKeys())
      .setVulnerabilityProbability(ruleDefFromPlugin.getVulnerabilityProbability().orElse(null))
      .build();
  }

  public static RuleDetails merging(ServerActiveRule activeRuleFromStorage, ServerRule serverRule, SonarLintRuleDefinition templateRuleDefFromPlugin,
    boolean skipCleanCodeTaxonomy) {
    var cleanCodeAttribute = skipCleanCodeTaxonomy ? null : templateRuleDefFromPlugin.getCleanCodeAttribute().orElse(CleanCodeAttribute.defaultCleanCodeAttribute());
    var defaultImpacts = skipCleanCodeTaxonomy ? Map.<SoftwareQuality, ImpactSeverity>of() : templateRuleDefFromPlugin.getDefaultImpacts();
    return builder()
      .setKey(activeRuleFromStorage.getRuleKey())
      .setLanguage(templateRuleDefFromPlugin.getLanguage())
      .setName(serverRule.getName())
      .setHtmlDescription(serverRule.getHtmlDesc())
      .setDescriptionSectionsByKey(serverRule.getDescriptionSections().stream()
        .map(s -> new DescriptionSection(s.getKey(), s.getHtmlContent(), s.getContext().map(c -> new DescriptionSection.Context(c.getKey(), c.getDisplayName()))))
        .collect(Collectors.groupingBy(DescriptionSection::getKey)))
      .setImpacts(mergeImpacts(defaultImpacts, activeRuleFromStorage.getOverriddenImpacts()))
      .setDefaultSeverity(serverRule.getSeverity())
      .setType(serverRule.getType())
      .setCleanCodeAttribute(cleanCodeAttribute)
      .setExtendedDescription(serverRule.getHtmlNote())
      .setEducationPrincipleKeys(templateRuleDefFromPlugin.getEducationPrincipleKeys())
      .setVulnerabilityProbability(templateRuleDefFromPlugin.getVulnerabilityProbability().orElse(null))
      .build();
  }

  public static Map<SoftwareQuality, ImpactSeverity> mergeImpacts(Map<SoftwareQuality, ImpactSeverity> defaultImpacts,
    List<ImpactPayload> overriddenImpacts) {
    var mergedImpacts = new EnumMap<SoftwareQuality, ImpactSeverity>(SoftwareQuality.class);
    if (!defaultImpacts.isEmpty()) {
      mergedImpacts = new EnumMap<>(defaultImpacts);
    }

    for (var impact : overriddenImpacts) {
      var quality = SoftwareQuality.valueOf(impact.getSoftwareQuality());
      var severity = ImpactSeverity.mapSeverity(impact.getSeverity());
      mergedImpacts.put(quality, severity);
    }

    return Collections.unmodifiableMap(mergedImpacts);
  }

  public static RuleDetails merging(RuleDetails serverActiveRuleDetails, RaisedFindingDto raisedFindingDto) {
    var isMQRMode = raisedFindingDto.getSeverityMode().isRight();
    var softwareImpacts = new EnumMap<SoftwareQuality, ImpactSeverity>(SoftwareQuality.class);
    if (isMQRMode) {
      raisedFindingDto.getSeverityMode().getRight().getImpacts().forEach(
        i -> softwareImpacts.put(SoftwareQuality.valueOf(i.getSoftwareQuality().name()),
          ImpactSeverity.valueOf(i.getImpactSeverity().name()))
      );
    }
    return builder()
      .setKey(serverActiveRuleDetails.getKey())
      .setLanguage(serverActiveRuleDetails.getLanguage())
      .setName(serverActiveRuleDetails.getName())
      .setHtmlDescription(serverActiveRuleDetails.getHtmlDescription())
      .setDescriptionSectionsByKey(serverActiveRuleDetails.getDescriptionSectionsByKey())
      .setImpacts(softwareImpacts)
      .setDefaultSeverity(isMQRMode ? null : IssueSeverity.valueOf(raisedFindingDto.getSeverityMode().getLeft().getSeverity().toString()))
      .setType(isMQRMode ? null : RuleType.valueOf(raisedFindingDto.getSeverityMode().getLeft().getType().toString()))
      .setCleanCodeAttribute(isMQRMode ? CleanCodeAttribute.valueOf(raisedFindingDto.getSeverityMode().getRight().getCleanCodeAttribute().name()) : null)
      .setExtendedDescription(serverActiveRuleDetails.getExtendedDescription())
      .setParams(serverActiveRuleDetails.getParams())
      .setEducationPrincipleKeys(serverActiveRuleDetails.educationPrincipleKeys)
      .setVulnerabilityProbability(serverActiveRuleDetails.getVulnerabilityProbability())
      .build();
  }

  public static RuleDetails merging(RuleDetails serverActiveRuleDetails, TaintVulnerabilityDto taintVulnerabilityDto) {
    var isMQRMode = taintVulnerabilityDto.getSeverityMode().isRight();
    EnumMap<SoftwareQuality, ImpactSeverity> softwareImpacts = new EnumMap<>(SoftwareQuality.class);
    if (isMQRMode) {
      taintVulnerabilityDto.getSeverityMode().getRight().getImpacts().forEach(
        i -> softwareImpacts.put(SoftwareQuality.valueOf(i.getSoftwareQuality().name()),
          ImpactSeverity.valueOf(i.getImpactSeverity().name()))
      );
    }
    return builder()
      .setKey(serverActiveRuleDetails.getKey())
      .setLanguage(serverActiveRuleDetails.getLanguage())
      .setName(serverActiveRuleDetails.getName())
      .setHtmlDescription(serverActiveRuleDetails.getHtmlDescription())
      .setDescriptionSectionsByKey(serverActiveRuleDetails.getDescriptionSectionsByKey())
      .setImpacts(softwareImpacts)
      .setDefaultSeverity(isMQRMode ? null : IssueSeverity.valueOf(taintVulnerabilityDto.getSeverityMode().getLeft().getSeverity().toString()))
      .setType(isMQRMode ? null : RuleType.valueOf(taintVulnerabilityDto.getSeverityMode().getLeft().getType().toString()))
      .setCleanCodeAttribute(isMQRMode ? CleanCodeAttribute.valueOf(taintVulnerabilityDto.getSeverityMode().getRight().getCleanCodeAttribute().name()) : null)
      .setExtendedDescription(serverActiveRuleDetails.getExtendedDescription())
      .setParams(serverActiveRuleDetails.getParams())
      .setEducationPrincipleKeys(serverActiveRuleDetails.educationPrincipleKeys)
      .setVulnerabilityProbability(serverActiveRuleDetails.getVulnerabilityProbability())
      .build();
  }

  public String getKey() {
    return key;
  }

  public SonarLanguage getLanguage() {
    return language;
  }

  public String getName() {
    return name;
  }

  public String getHtmlDescription() {
    return htmlDescription;
  }

  public boolean hasMonolithicDescription() {
    return descriptionSectionsByKey.isEmpty() || hasOnlyDefaultSection();
  }

  private boolean hasOnlyDefaultSection() {
    return descriptionSectionsByKey.size() == 1 && descriptionSectionsByKey.containsKey(DEFAULT_SECTION);
  }

  public Map<String, List<DescriptionSection>> getDescriptionSectionsByKey() {
    return descriptionSectionsByKey;
  }

  @CheckForNull
  public IssueSeverity getDefaultSeverity() {
    return defaultSeverity;
  }

  @CheckForNull
  public RuleType getType() {
    return type;
  }

  public Optional<CleanCodeAttribute> getCleanCodeAttribute() {
    return Optional.ofNullable(cleanCodeAttribute);
  }

  public Map<SoftwareQuality, ImpactSeverity> getImpacts() {
    return impacts;
  }

  public Collection<EffectiveRuleParam> getParams() {
    return params;
  }

  public Set<String> getCleanCodePrincipleKeys() {
    return educationPrincipleKeys;
  }

  @CheckForNull
  public String getExtendedDescription() {
    return extendedDescription;
  }

  public VulnerabilityProbability getVulnerabilityProbability() {
    return vulnerabilityProbability;
  }

  public static class EffectiveRuleParam {
    private final String name;
    private final String description;
    @Nullable
    private final String value;
    @Nullable
    private final String defaultValue;

    public EffectiveRuleParam(String name, String description, @Nullable String value, @Nullable String defaultValue) {
      this.name = name;
      this.description = description;
      this.value = value;
      this.defaultValue = defaultValue;
    }

    public String getName() {
      return name;
    }

    public String getDescription() {
      return description;
    }

    @CheckForNull
    public String getValue() {
      return value;
    }

    @CheckForNull
    public String getDefaultValue() {
      return defaultValue;
    }
  }

  public static class DescriptionSection {
    private final String key;
    private final String htmlContent;
    private final Optional<Context> context;

    public DescriptionSection(String key, String htmlContent, Optional<Context> context) {
      this.key = key;
      this.htmlContent = htmlContent;
      this.context = context;
    }

    public String getKey() {
      return key;
    }

    public String getHtmlContent() {
      return htmlContent;
    }

    public Optional<Context> getContext() {
      return context;
    }

    public static class Context {
      private final String key;
      private final String displayName;

      public Context(String key, String displayName) {
        this.key = key;
        this.displayName = displayName;
      }

      public String getKey() {
        return key;
      }

      public String getDisplayName() {
        return displayName;
      }
    }

  }
}
