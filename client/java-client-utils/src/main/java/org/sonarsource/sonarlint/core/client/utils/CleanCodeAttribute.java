/*
ACR-6667c9655072427ba3599e9f80f347e3
ACR-e588210eab9a4098944d10f19e60253e
ACR-1c769b8da6cd4051a4a239579f33c02b
ACR-7b30cbc044d4497b8acd0384fe10bd40
ACR-1790dc6d66c54860ab38c6f8de98e595
ACR-cd8cc43f58c34ca88db14aba52a67734
ACR-06106c8f1aeb473881924061fac8a8fa
ACR-e61b7f3440964de588fc82b8b0afa583
ACR-29325a16f2254f66a4fd8f4f4059cbad
ACR-86e4e8753b704c388ee33ac07a30de38
ACR-f20d234a5fe747f1a96d84dfa6016f8c
ACR-94a0afd287a2466cb97c8523b34bd157
ACR-51c4f3781b734f80b13b9e434e72e754
ACR-20247308075545c88f30b0cf87c1a637
ACR-16429721a1bd4a95830ca49bd89062b0
ACR-a7011f278bd347618147fc28de8486d5
ACR-3f4adfe6c8694e4f991dd005b54aed48
 */
package org.sonarsource.sonarlint.core.client.utils;

public enum CleanCodeAttribute {

  CONVENTIONAL("Not conventional", CleanCodeAttributeCategory.CONSISTENT),
  FORMATTED("Not formatted", CleanCodeAttributeCategory.CONSISTENT),
  IDENTIFIABLE("Not identifiable", CleanCodeAttributeCategory.CONSISTENT),

  CLEAR("Not clear", CleanCodeAttributeCategory.INTENTIONAL),
  COMPLETE("Not complete", CleanCodeAttributeCategory.INTENTIONAL),
  EFFICIENT("Not efficient", CleanCodeAttributeCategory.INTENTIONAL),
  LOGICAL("Not logical", CleanCodeAttributeCategory.INTENTIONAL),

  DISTINCT("Not distinct", CleanCodeAttributeCategory.ADAPTABLE),
  FOCUSED("Not focused", CleanCodeAttributeCategory.ADAPTABLE),
  MODULAR("Not modular", CleanCodeAttributeCategory.ADAPTABLE),
  TESTED("Not tested", CleanCodeAttributeCategory.ADAPTABLE),

  LAWFUL("Not lawful", CleanCodeAttributeCategory.RESPONSIBLE),
  RESPECTFUL("Not respectful", CleanCodeAttributeCategory.RESPONSIBLE),
  TRUSTWORTHY("Not trustworthy", CleanCodeAttributeCategory.RESPONSIBLE);


  private final String label;
  private final CleanCodeAttributeCategory category;

  CleanCodeAttribute(String label, CleanCodeAttributeCategory category) {
    this.label = label;
    this.category = category;
  }

  public String getLabel() {
    return label;
  }

  public CleanCodeAttributeCategory getCategory() {
    return category;
  }

  public static CleanCodeAttribute fromDto(org.sonarsource.sonarlint.core.rpc.protocol.common.CleanCodeAttribute rpcEnum) {
    switch (rpcEnum) {
      case CONVENTIONAL:
        return CONVENTIONAL;
      case FORMATTED:
        return FORMATTED;
      case IDENTIFIABLE:
        return IDENTIFIABLE;
      case CLEAR:
        return CLEAR;
      case COMPLETE:
        return COMPLETE;
      case EFFICIENT:
        return EFFICIENT;
      case LOGICAL:
        return LOGICAL;
      case DISTINCT:
        return DISTINCT;
      case FOCUSED:
        return FOCUSED;
      case MODULAR:
        return MODULAR;
      case TESTED:
        return TESTED;
      case LAWFUL:
        return LAWFUL;
      case RESPECTFUL:
        return RESPECTFUL;
      case TRUSTWORTHY:
        return TRUSTWORTHY;
      default:
        throw new IllegalArgumentException("Unknown attribute: " + rpcEnum);
    }
  }

}
