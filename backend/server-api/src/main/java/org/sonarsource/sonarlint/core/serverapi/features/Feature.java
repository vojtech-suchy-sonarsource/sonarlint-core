/*
ACR-dfec02135a03406283a3911e9639c8e2
ACR-e999e57e1b8c47c5852da730a7df6184
ACR-60949586e40c4753985ad81d6834cf48
ACR-5bda6ad741184079bee2bad06f5ba7ea
ACR-6f2185d4943744d1ba43cbf08a6e36af
ACR-c3584d7a23ec495e9e83b8a630a6a791
ACR-db3212f51f1b48b0ab79899942116e0a
ACR-f005bc5edba74d5181d45be956f87b1b
ACR-7ba2d7d2d2f440249235680be139f03c
ACR-203dfd09bb034701b3114205e66709f9
ACR-dce4bd80f5ec4e3f8dc0d23a60ff83e9
ACR-6757cdff3ec3434ea8d4b6b65be760aa
ACR-6141ada2812f4a07bcbb5ea955d0166f
ACR-494d3d0571fc4d89be852d22277611e3
ACR-a21b93eaf0e2408b863fc37860c72ef3
ACR-6907f28ab93a49fe9757b04f6ba6bb3f
ACR-828fb297f8f94969affc23988b5a28ea
 */
package org.sonarsource.sonarlint.core.serverapi.features;

import java.util.Arrays;
import java.util.Optional;

public enum Feature {
  AI_CODE_FIX("fix-suggestions"),
  SCA("sca");

  public static Optional<Feature> fromKey(String key) {
    return Arrays.stream(values()).filter(f -> f.key.equals(key)).findFirst();
  }

  private final String key;

  Feature(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
