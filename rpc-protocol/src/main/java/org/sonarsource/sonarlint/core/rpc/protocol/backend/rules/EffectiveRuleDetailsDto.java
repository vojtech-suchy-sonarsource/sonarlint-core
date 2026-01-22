/*
ACR-21564f64c9f14274b3d94c1fb9f2aa0f
ACR-8ee347d315574817837363e588130fa6
ACR-c03d015cf1914bdab31d15011c5c8c63
ACR-e5b2917fd4224a448f1305921aa9cbb6
ACR-9fb2459bad5743d09a6aaa833e7b54af
ACR-f993318cfb2c47c083fc44edbb0d6c06
ACR-8ce5d67893ec4eb8be171986ff8b5601
ACR-08def59a5e964960bfd8fbef1b3e4775
ACR-6bec40fa109e4c248f9db2ecdf1fb221
ACR-4892cc48e9df418d86a9846fbeb15e52
ACR-cfe612d1ffad437193f17797e0bd6744
ACR-ce9c5294cd76442f8d741b812a5e0bf4
ACR-ddc59037ccab46d48b64a96ddff912c9
ACR-ef8986b156104357a8ef64d2df7d21bc
ACR-6995ac85ae5d42808b0eb5b5fb1a687e
ACR-39eeda8c74df41d5ae0f49688d334094
ACR-aa9d330f22f241498be871bd4018aef1
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
