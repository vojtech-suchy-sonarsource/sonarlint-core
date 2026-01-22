/*
ACR-4bedc479d3e84bc99a646528a0fdabd7
ACR-7899bbd4e7e14ff9bbc9ff5b6beec558
ACR-56eed6977400414b8459b83cc160169d
ACR-65caf983f9504f0aaf524b62f54b526c
ACR-262bfee92c184af1a1de27c412df1bc0
ACR-1ef6a349440446448fa1b3bded58642d
ACR-047c9ef9078146688c9db41fc49c943a
ACR-603674cce7b44cc3bb59aca528a25c3c
ACR-5dc07026d5a4426f80fd0492c8f73c5d
ACR-cb45c19fced047bcb418785c493223b5
ACR-5ff4db2e711c437b9a0a76842a02ca3d
ACR-a393bb51b92549eb8dc2f61862fbce36
ACR-6b1aff8cf2c04d388cbfe307d9f6c3fd
ACR-993a5ba30fda4162badfbbc530af9395
ACR-158874756b5b4e47b923ca3b336143e4
ACR-2de3f4130a93487eb1461391369b696d
ACR-da515860590045798384d7326cd599b9
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
