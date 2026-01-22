/*
ACR-c64388c86e294f28801b25af9b335ff2
ACR-bdb5f10f9f9947eebb4f480e0acd75ee
ACR-697ef59c6b234ac497e58186bdf8e69d
ACR-2c4e129651394e4b8fa4e39008e67321
ACR-8e9b2cbc5a98420cbb433a1fdcad0f8a
ACR-a4c5b5d706364efbbdda62364e69abe6
ACR-cdf06c4d586a4e75a86e261e22fab4d3
ACR-f6c709d123094e87bd91efb6a9f8be37
ACR-3e6b703774f84d6a85b3ccb7d3f0b2b5
ACR-2ea6f815f22a4425b3c735bfa2bc028d
ACR-04ae0c6b76354cd0b9fd6c6850e1f0f2
ACR-741450715a4a4b8cb9c336f684ff67ad
ACR-7d109903832443e4bfeaaa5e22cafc14
ACR-b2f4063f9b8f446584807917705c24e6
ACR-e952007359ce418794d98e8cdc66dad7
ACR-2971bdf4c3794760a2d22b5eb3f78904
ACR-3bea89801cdd4c2a96a4734e52e1d461
 */
package org.sonarsource.sonarlint.core.commons.dogfood;

import org.apache.commons.lang3.SystemUtils;

public class DogfoodEnvironmentDetectionService {
  public static final String SONARSOURCE_DOGFOODING_ENV_VAR_KEY = "SONARSOURCE_DOGFOODING";

  public boolean isDogfoodEnvironment() {
    return "1".equals(SystemUtils.getEnvironmentVariable(SONARSOURCE_DOGFOODING_ENV_VAR_KEY, "0"));
  }
}
