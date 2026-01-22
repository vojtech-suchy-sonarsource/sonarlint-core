/*
ACR-e969369e413a4e16986ef492aef47b33
ACR-7aac6c736db9446bbbc3a38af1acadad
ACR-2f8ea0b7b54d4fc2ba895fa82867dfaa
ACR-bdc182b2a63842a78ece6139f0323d43
ACR-f6a066014d354d088943615585435890
ACR-cc8dcb7042c345e39ec375849e75a0b5
ACR-4e8dbaed7d3641afb3b4f7174f53adfd
ACR-5b9f873e724f45cd8cb0d3eb426e9dc2
ACR-9b9b4c2b952447a2a971645c0df21cc1
ACR-c58190119fa14c95b973712236d553d3
ACR-77dbe01faef1452b9ddd15fcd45cfbcd
ACR-83331cab01c54a7bb1113716aef15875
ACR-81ad738420574a2a90b4c67514132bad
ACR-5cb551fa4e854e0bb94d8cbf844e0471
ACR-56fa7268f51848f7bc1c070b6ead79a2
ACR-8502ba80fcea420997b343e67bfa5e73
ACR-7da54e62be8e45c7a4ee7eb3eea427fd
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
