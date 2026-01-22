/*
ACR-9009e58e7277400da53b8ab8d3bbb6e9
ACR-38c491aff20e403e8c2a70acfcb8e268
ACR-0b595e2ffad74a348804361b58be90ca
ACR-1d53a20d9fa14afa8caf87a5176b2603
ACR-9393bb82dea44356b43e28c064133866
ACR-c55ea822b3e348edbeceb426896163ac
ACR-7b69c34ae111496c88ac00d4743b6daf
ACR-4f87ba38f976435b858da9cd528880f7
ACR-a92a940e936b43058116b8d2e82de8ec
ACR-3c692c624fcc4fadbe7f6f5c5f489c40
ACR-0b493cc4598f457ea0b4ad221cab369b
ACR-5229624601b44ee980e4b91bc820701c
ACR-3472f7c3161544779be268e20c41c33b
ACR-30a4709391c24f649288e38045e41747
ACR-f1798ae8d8aa41a8941cb999596439ff
ACR-b82da3666615485abade1cdc82d34443
ACR-4c2775ea54384c9a987111007d2f6527
 */
package org.sonarsource.sonarlint.core.commons;

import static org.sonarsource.sonarlint.core.commons.CleanCodeAttributeCategory.ADAPTABLE;
import static org.sonarsource.sonarlint.core.commons.CleanCodeAttributeCategory.CONSISTENT;
import static org.sonarsource.sonarlint.core.commons.CleanCodeAttributeCategory.INTENTIONAL;
import static org.sonarsource.sonarlint.core.commons.CleanCodeAttributeCategory.RESPONSIBLE;

public enum CleanCodeAttribute {

  CONVENTIONAL(CONSISTENT),
  FORMATTED(CONSISTENT),
  IDENTIFIABLE(CONSISTENT),

  CLEAR(INTENTIONAL),
  COMPLETE(INTENTIONAL),
  EFFICIENT(INTENTIONAL),
  LOGICAL(INTENTIONAL),

  DISTINCT(ADAPTABLE),
  FOCUSED(ADAPTABLE),
  MODULAR(ADAPTABLE),
  TESTED(ADAPTABLE),

  LAWFUL(RESPONSIBLE),
  RESPECTFUL(RESPONSIBLE),
  TRUSTWORTHY(RESPONSIBLE);

  private final CleanCodeAttributeCategory attributeCategory;


  CleanCodeAttribute(CleanCodeAttributeCategory attributeCategory) {
    this.attributeCategory = attributeCategory;
  }


  public CleanCodeAttributeCategory getAttributeCategory() {
    return attributeCategory;
  }

  public static CleanCodeAttribute defaultCleanCodeAttribute() {
    return CONVENTIONAL;
  }
}
