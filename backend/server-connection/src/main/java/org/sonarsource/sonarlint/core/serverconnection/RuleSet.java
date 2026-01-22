/*
ACR-7707ca505fc448e9924e67c6474e595e
ACR-066ed0c9430444dca3bdbaeedbda47b8
ACR-990c92b95f8649a28d1d83774e575300
ACR-7348abb78887470699ac9e97bca2f63d
ACR-dcaf2d26babf4906af8e63c638e5959f
ACR-4810d88d73a74544a830bcc1b3d7eb02
ACR-0b2a00bc495145078df7ec62f67ff6fd
ACR-45ac6cc139ab4cc78491ced394dcc8d6
ACR-b0b7ba2abaa3479bb3489de7bee400d5
ACR-bf5b5fdf69084564810ad3fe7c5d05e1
ACR-2c219b6a438a4172924ba69adaeb5ffd
ACR-2782ecfa98684527b8fb9c0a81069f3c
ACR-ae3aec0a718044a7b6fae91e44360cb3
ACR-529c5adc67964c7baff230cf37fcf6ef
ACR-b018c6a685064dc983767d36da27d5a0
ACR-622cf039754b4685a42bb4926048f5ec
ACR-1b7eb52fe55545549edb01469a1da3eb
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.sonarsource.sonarlint.core.serverapi.rules.ServerActiveRule;

public class RuleSet {
  private final Collection<ServerActiveRule> rules;
  private final Map<String, ServerActiveRule> rulesByKey;
  private final String lastModified;

  public RuleSet(Collection<ServerActiveRule> rules, String lastModified) {
    this.rules = rules;
    this.rulesByKey = rules.stream().collect(Collectors.toMap(ServerActiveRule::getRuleKey, Function.identity()));
    this.lastModified = lastModified;
  }

  public Collection<ServerActiveRule> getRules() {
    return rules;
  }

  public Map<String, ServerActiveRule> getRulesByKey() {
    return rulesByKey;
  }

  public String getLastModified() {
    return lastModified;
  }
}
