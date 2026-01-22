/*
ACR-1013376582fc416c81f9d88f7a52d9d9
ACR-dd07fa0818924735b58da82810dfb39e
ACR-368d36ff7e914357bbbd5a4d98976eb0
ACR-a508e2f6947a4c70b9ba3068f35fe2a7
ACR-a697ca19cefa4cf684a6ec0d189ac092
ACR-4e7d0c7826964ac3bffbaacff8583c38
ACR-026d0c6d32f94bcd8690cea5657ea9fc
ACR-326fe125a4304239827795883da33c2d
ACR-6e8b003dd0544ac4a067f978946ce46e
ACR-1c6f705f5d52406bb43fdd2296f5c8a0
ACR-ed277034075842f9b60c2b97fe1a4f8b
ACR-730c56b599d74651930448370205be1e
ACR-2812f8e9884d472289596e39973c7429
ACR-fd2b3e9e047e45f0a622b4913d97576c
ACR-8260ddfd5d3c4ddabc084a4855a74f85
ACR-7cb07ad07cd0461cb51515d5ab2a3168
ACR-e09f9ae15a1e4102847e3a87478c5634
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
