/*
ACR-0f23fe1e2495410a9c4b2659f68cb5fe
ACR-8566e10485f54f76a317aa467bba3da5
ACR-78e2117c0c794302b55a6045758d32ed
ACR-3f3215d817764f2981ab6684e74f6577
ACR-6ae816a5f2d2440f927af6d2e68044d5
ACR-a7c4fde9c24243e38994bfa4c8f2e832
ACR-6c602c0696e545f49e02251bbe5a933d
ACR-c48306248b41454a860840cac935376a
ACR-c4a6b871467c49b884336eb58a8cbe04
ACR-a2151825d3084cfbb16c226fdb792fd9
ACR-01b3a6150e9245a983c53116d4c80172
ACR-6ff9aae3e39a4dea9c11fefe1a07fde7
ACR-e72f9353990643d9a950ae71fc72e97c
ACR-1d99b47b93da432e8859778de519cc74
ACR-0d90e9e4a33046c8b0be009e9cbe296c
ACR-65d8f3dacca9451ab9ace592e7699014
ACR-ff4136056f6846bfaee382cd0af1541c
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
