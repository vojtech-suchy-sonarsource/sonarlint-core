/*
ACR-f2eaded05ceb4fd3b6c29b232a039f31
ACR-ee94ce01739e4a11bbc632cb93535289
ACR-f1d810d75d43421abf2bf8ffee2a8470
ACR-5ab06ae20f1748d8bbe924d0867d830c
ACR-2a8373f51e2d4c51a699935c26113355
ACR-812bcbdecded4f7eb70276bbda347958
ACR-1b6a6e9146b44ac8b44c222528ebfa02
ACR-28a6e7520408419cbd47fbf11434e7b8
ACR-1a203268e6d046a299d2e8ecf8af97c2
ACR-789a993c311744f0b1cae0a0bc074e93
ACR-575eadc1babb4625b4a9f6f487bfaa18
ACR-99a99c1c8fd44632b8745f28a9c7fb33
ACR-f65ccb1aebb8442bb9ead00e25a1d481
ACR-08f6ada77a7c4633bf1e92936298b49d
ACR-271975438aa74172adb4f08231e68989
ACR-9c74914d58bd4563a87eaab94f69f795
ACR-db358f0107c145698f172f50e4861da0
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
