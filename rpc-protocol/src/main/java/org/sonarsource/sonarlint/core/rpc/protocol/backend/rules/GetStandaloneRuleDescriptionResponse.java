/*
ACR-633fb19bfd5c46e8830f4d9540fd92c1
ACR-3610717701d1485792946ac858861441
ACR-96b0a69ac82f4136924ef252fe9bd0ca
ACR-a0c6c67dc8d249be9925fca71f0ff41a
ACR-a92507e86b704607818de127e3a4ae9d
ACR-960a653b45264790a144cf468b30f9c3
ACR-b839ca6fdfc847958c31975085ff1b7c
ACR-f3405ad8dcd64ff5b509245c8be895e9
ACR-f9333067803f42d78b0adc8263676db4
ACR-b5f0b3c437594bd6ab6f6a100c76f521
ACR-eaaee7c15c2a464699688df110922990
ACR-81692699ddc04a728a43631a2a16b3f3
ACR-8af2c45612964cc8a512bf3630b1e3e6
ACR-31d2b1e7769842cca533b28b39236e36
ACR-a7463fb8e353426fb01763e630dfe736
ACR-00bc2c59be9e4eb0845dfc49fc29874a
ACR-69b022c14a024efc85829ed590d88e9f
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.rules;

import com.google.gson.annotations.JsonAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.adapter.EitherRuleDescriptionAdapterFactory;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;

public class GetStandaloneRuleDescriptionResponse {

  private final RuleDefinitionDto ruleDefinition;
  @JsonAdapter(EitherRuleDescriptionAdapterFactory.class)
  private final Either<RuleMonolithicDescriptionDto, RuleSplitDescriptionDto> description;

  public GetStandaloneRuleDescriptionResponse(RuleDefinitionDto ruleDefinition, Either<RuleMonolithicDescriptionDto, RuleSplitDescriptionDto> description) {
    this.ruleDefinition = ruleDefinition;
    this.description = description;
  }

  public RuleDefinitionDto getRuleDefinition() {
    return ruleDefinition;
  }

  public Either<RuleMonolithicDescriptionDto, RuleSplitDescriptionDto> getDescription() {
    return description;
  }
}
