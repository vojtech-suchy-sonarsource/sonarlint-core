/*
ACR-0b9e3ef9ca494812a0ac7aad531a0104
ACR-dff2c3cc3aa34e3baa380c83c5a4e6c1
ACR-bd8c1be6a10d4c9ea31af51886ad6e28
ACR-41f6c0199d864cc48e042436a81e4652
ACR-1783523c5f2a43dc95825636b1d0188e
ACR-73ad27cee2ab40dda1c814d3322d474c
ACR-69e6a315907a4cf3bd43c4ea1018e874
ACR-304d41dc526e414693b43fb4b71f17e5
ACR-a39f9e766ca343fea759033cc313b8ad
ACR-74f9c27e507a49369f1cfdf18cc4a21f
ACR-698ee1ad91ff466fb8a41098fec8a588
ACR-de790f5d01c8423face63ab8dd756c66
ACR-17fee0fc04354c0a8be2b98cd8fe063e
ACR-6a37093a26fc43478974ddc6b38c237d
ACR-39ca7c69f0e24712b30f22a75974b186
ACR-1c32b727706d4db8a2387a58805b6a77
ACR-6887ca8f58c44b9da7ce51427a8e9d54
 */
package org.sonarsource.sonarlint.core.rules;

public class RuleNotFoundException extends Exception {
  private final String ruleKey;

  public RuleNotFoundException(String message, String ruleKey) {
    super(message);
    this.ruleKey = ruleKey;
  }

  public String getRuleKey() {
    return ruleKey;
  }
}
