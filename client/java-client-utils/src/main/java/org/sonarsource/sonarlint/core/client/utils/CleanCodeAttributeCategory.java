/*
ACR-753a6f45fc36453387a421189da0aefe
ACR-b9ad2f54e812402f9bd13d397fe19969
ACR-96ffd042054e4d15ad8460e02fac1fb5
ACR-e36a085672d34f60862e28c08e3710ff
ACR-5fe210471d364044a114fa74880d4c5a
ACR-e8dfa42afbe04081ba91e387cd579931
ACR-d9791378b5a54208815d237722fb19c8
ACR-4896741450184c94872d990d28c64661
ACR-4010969a7c6844ea8117c159fcb9fe2f
ACR-c0fbe487b36a408bbd65e6681602a0cf
ACR-2f97a4d6d30b4fd2b25334c463576072
ACR-a2375cff2d3a4ac695691ea6717be551
ACR-69b1d674cf7d416380f3300331064d3f
ACR-25eb7a5cd0f34679805445e249fd0b44
ACR-caa75de7c06d448d8f18e1577885f694
ACR-cf90f592db10436785eff66d45b75f51
ACR-ad21470c3154479a9e0421c24aadbe85
 */
package org.sonarsource.sonarlint.core.client.utils;

public enum CleanCodeAttributeCategory {
  ADAPTABLE("Adaptability"),
  CONSISTENT("Consistency"),
  INTENTIONAL("Intentionality"),
  RESPONSIBLE("Responsibility");

  private final String label;

  CleanCodeAttributeCategory(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public static CleanCodeAttributeCategory fromDto(org.sonarsource.sonarlint.core.rpc.protocol.common.CleanCodeAttributeCategory rpcEnum) {
    switch (rpcEnum) {
      case ADAPTABLE:
        return ADAPTABLE;
      case CONSISTENT:
        return CONSISTENT;
      case INTENTIONAL:
        return INTENTIONAL;
      case RESPONSIBLE:
        return RESPONSIBLE;
      default:
        throw new IllegalArgumentException("Unknown category: " + rpcEnum);
    }
  }
}
