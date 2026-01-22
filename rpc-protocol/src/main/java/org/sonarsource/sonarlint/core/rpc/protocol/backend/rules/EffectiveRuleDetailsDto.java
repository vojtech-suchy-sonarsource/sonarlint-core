/*
ACR-11c89a5fbaa84c15a0bc04615a5fe469
ACR-1330ca946b694b308cd17c2ace41c4ec
ACR-f9d0119888434140b20e86c3bf241d9e
ACR-64ca559d7fcf4226bd06b139751e1aff
ACR-aabc7c237e1b4939976c7adc9b589999
ACR-ea9dbea4c236487b9a6091d3170af606
ACR-3466d7eaf583449fb2068b6ee18b59a5
ACR-9ba6c75f536f427983c4d184dd2ef9c0
ACR-05efa8491dbf4cbbb6fcc412e8e873ab
ACR-76e5af7b268a4cbc8e699810e1126eec
ACR-af850d3715eb4634a67ea4db1f347406
ACR-94b395fc6b4c404aacccdd5d4ea0e87b
ACR-55a02307b55f4c189f448ca0995e8df2
ACR-4f553070e8cf442ba3fd976270d99c77
ACR-8c94ab6d67bf4cda981a2f4b3555f06f
ACR-ffe9088f5eae4053ad9aa9b2072367e8
ACR-8a342bf0c98e41ec9ea0d4dcfd45b9b0
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.rules;

import com.google.gson.annotations.JsonAdapter;
import java.util.Collection;
import java.util.List;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.adapter.EitherRuleDescriptionAdapterFactory;
import org.sonarsource.sonarlint.core.rpc.protocol.common.CleanCodeAttribute;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.IssueSeverity;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;
import org.sonarsource.sonarlint.core.rpc.protocol.common.MQRModeDetails;
import org.sonarsource.sonarlint.core.rpc.protocol.common.RuleType;
import org.sonarsource.sonarlint.core.rpc.protocol.common.StandardModeDetails;

public class EffectiveRuleDetailsDto {
  private final String key;
  private final String name;
  private final Either<StandardModeDetails, MQRModeDetails> severityDetails;
  private final Language language;
  private final VulnerabilityProbability vulnerabilityProbability;
  @JsonAdapter(EitherRuleDescriptionAdapterFactory.class)
  private final Either<RuleMonolithicDescriptionDto, RuleSplitDescriptionDto> description;
  private final Collection<EffectiveRuleParamDto> params;

  public EffectiveRuleDetailsDto(String key, String name, Either<StandardModeDetails, MQRModeDetails> severityDetails,
    Either<RuleMonolithicDescriptionDto, RuleSplitDescriptionDto> description, Collection<EffectiveRuleParamDto> params,
    Language language, @Nullable VulnerabilityProbability vulnerabilityProbability) {
    this.key = key;
    this.name = name;
    this.severityDetails = severityDetails;
    this.language = language;
    this.vulnerabilityProbability = vulnerabilityProbability;
    this.description = description;
    this.params = params;
  }

  public Either<RuleMonolithicDescriptionDto, RuleSplitDescriptionDto> getDescription() {
    return description;
  }

  public Collection<EffectiveRuleParamDto> getParams() {
    return params;
  }

  public String getKey() {
    return key;
  }

  public String getName() {
    return name;
  }

  public Either<StandardModeDetails, MQRModeDetails> getSeverityDetails() {
    return severityDetails;
  }

  @CheckForNull
  public IssueSeverity getSeverity() {
    return this.severityDetails.isLeft() ?
      this.severityDetails.getLeft().getSeverity() : null;
  }

  @CheckForNull
  public RuleType getType() {
    return this.severityDetails.isLeft() ?
      this.severityDetails.getLeft().getType() : null;
  }

  public List<ImpactDto> getDefaultImpacts() {
    return this.severityDetails.isRight() ?
      this.severityDetails.getRight().getImpacts() : List.of();
  }

  @CheckForNull
  public CleanCodeAttribute getCleanCodeAttribute() {
    return this.severityDetails.isRight() ?
      this.severityDetails.getRight().getCleanCodeAttribute() : null;
  }

  public Language getLanguage() {
    return language;
  }

  public VulnerabilityProbability getVulnerabilityProbability() {
    return vulnerabilityProbability;
  }
}
