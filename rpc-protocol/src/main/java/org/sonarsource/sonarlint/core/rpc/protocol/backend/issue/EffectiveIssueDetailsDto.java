/*
ACR-9a8d59ecd2ff45aa87a9a4bf2bc1b395
ACR-38f123a7d73a4eada29e465712e564be
ACR-726bef586cda46ac8aca8c42d49b0851
ACR-26e4acad0b37411e87f94538ed6d0eae
ACR-f02029245ab44c138585ed3ada68392e
ACR-4c1728288e2348818d8b8d1b76c4ab5b
ACR-fe9d40c678314b5e963d5b633812b2b9
ACR-1fcc582d6cfb4b0cae2166f106991ec7
ACR-d97ba051db0c4b0990f8330033f7ee80
ACR-25fa21efbb4b4becbf507b21a443eab0
ACR-831897ea71a94514ab506ed622a2de00
ACR-bc193dd6924f4596a12dafedf620f4ee
ACR-9246b4570afd41a692c0fab006c29641
ACR-8d7de3c1be4f4ac984083bb95f15bbf6
ACR-162bf28411dd411b87d49ee61e157e27
ACR-e7602bc8c2a84c0f806cb0984351917d
ACR-870f694ab99b4aa4b846717f19ff220c
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.issue;

import com.google.gson.annotations.JsonAdapter;
import java.util.Collection;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.adapter.EitherRuleDescriptionAdapterFactory;
import org.sonarsource.sonarlint.core.rpc.protocol.adapter.EitherStandardOrMQRModeAdapterFactory;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.EffectiveRuleParamDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.RuleMonolithicDescriptionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.RuleSplitDescriptionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.VulnerabilityProbability;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;
import org.sonarsource.sonarlint.core.rpc.protocol.common.MQRModeDetails;
import org.sonarsource.sonarlint.core.rpc.protocol.common.StandardModeDetails;

public class EffectiveIssueDetailsDto {
  private final String ruleKey;
  private final String name;
  private final Language language;
  private final VulnerabilityProbability vulnerabilityProbability;
  @JsonAdapter(EitherRuleDescriptionAdapterFactory.class)
  private final Either<RuleMonolithicDescriptionDto, RuleSplitDescriptionDto> description;
  private final Collection<EffectiveRuleParamDto> params;
  @JsonAdapter(EitherStandardOrMQRModeAdapterFactory.class)
  private final Either<StandardModeDetails, MQRModeDetails> severityDetails;
  private final String ruleDescriptionContextKey;

  public EffectiveIssueDetailsDto(String ruleKey, String name, Language language, @Nullable VulnerabilityProbability vulnerabilityProbability,
    Either<RuleMonolithicDescriptionDto, RuleSplitDescriptionDto> description,
    Collection<EffectiveRuleParamDto> params, Either<StandardModeDetails, MQRModeDetails> severityDetails,
    @Nullable String ruleDescriptionContextKey) {
    this.ruleKey = ruleKey;
    this.name = name;
    this.language = language;
    this.vulnerabilityProbability = vulnerabilityProbability;
    this.description = description;
    this.params = params;
    this.severityDetails = severityDetails;
    this.ruleDescriptionContextKey = ruleDescriptionContextKey;
  }

  public Either<StandardModeDetails, MQRModeDetails> getSeverityDetails() {
    return severityDetails;
  }

  @CheckForNull
  public String getRuleDescriptionContextKey() {
    return ruleDescriptionContextKey;
  }

  public String getRuleKey() {
    return ruleKey;
  }

  public String getName() {
    return name;
  }

  public Language getLanguage() {
    return language;
  }

  public VulnerabilityProbability getVulnerabilityProbability() {
    return vulnerabilityProbability;
  }

  public Either<RuleMonolithicDescriptionDto, RuleSplitDescriptionDto> getDescription() {
    return description;
  }

  public Collection<EffectiveRuleParamDto> getParams() {
    return params;
  }
}
