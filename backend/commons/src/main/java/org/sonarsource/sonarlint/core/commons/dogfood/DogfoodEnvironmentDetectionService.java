/*
ACR-da00ab807ae34de7b417d03c75421f87
ACR-7dfed6b96823475f9b296af7758bf558
ACR-2afabb3f23864c29986c20334fe46672
ACR-c6f05599f5ed4f78b1d551aa222fab54
ACR-1f44246a089f43ff96a021e43f3ce3f3
ACR-058ce1f37b7d4f2485c1e4afccb6d7d9
ACR-2b1a54065b14441f8a1c9e070d08602a
ACR-492e8092426a409daf4fa60780c7c776
ACR-c1fb447e1e7e47d2a70bc814610294f2
ACR-e01a2c5c22874752bba1334a60243449
ACR-cb45a6b62ee5454eaeb5b6f7f9c32728
ACR-53b4bc171d134e6bb955ddbb3c506dd3
ACR-1e08990ec5044479a003c2ddbef6a8b3
ACR-2cb4de4e1d8d4da7843e411089b9da1d
ACR-6891f26f4d26451b9e3879768c9c2b93
ACR-3dd095da6a38472499e415fdc6f89f33
ACR-539f5fc3615a45e3bc223b2ab5412ad4
 */
package org.sonarsource.sonarlint.core.commons.dogfood;

import org.apache.commons.lang3.SystemUtils;

public class DogfoodEnvironmentDetectionService {
  public static final String SONARSOURCE_DOGFOODING_ENV_VAR_KEY = "SONARSOURCE_DOGFOODING";

  public boolean isDogfoodEnvironment() {
    return "1".equals(SystemUtils.getEnvironmentVariable(SONARSOURCE_DOGFOODING_ENV_VAR_KEY, "0"));
  }
}
