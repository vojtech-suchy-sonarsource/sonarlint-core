/*
ACR-592676b35c5c42a5bc67864c60918517
ACR-41994daa630f4e25ab04e0c6aef47fc8
ACR-3b4b1e5a65ae4860858232eeed59d22f
ACR-26c95af7a20243708e166890e472c40a
ACR-b2c7b6c7c3dc4d869c6f49a553460c0c
ACR-cda85e301bd84070a321dbc388134f27
ACR-34d34e00341743428aa29fa9716d1874
ACR-a60ab51e09d045a4a052b893424725aa
ACR-dc7fc7aafd1e4077aa8158ad8abb8ae8
ACR-b9ef44e70e9d47d2b2a2de1dbc56aba0
ACR-cfb9263d7d1045278a53318fc6e63406
ACR-db8aab87a7844f84805f5cf9a9f64268
ACR-e892d4772b144c30bd1ef56a3715b50f
ACR-5d21b1fd2c2943d6b7a0e4b4a1d0c25a
ACR-004ee39766b9444faf4360837f8d5fa0
ACR-2a43a696dd084e9ca80245db6144c5f6
ACR-2bbf7c8e52df45e89267029b895dbeb0
 */
package org.sonarsource.sonarlint.core.rule.extractor;

import java.util.Optional;
import org.sonar.api.config.Configuration;

public class EmptyConfiguration implements Configuration {
  @Override
  public Optional<String> get(String key) {
    return Optional.empty();
  }

  @Override
  public boolean hasKey(String key) {
    return false;
  }

  @Override
  public String[] getStringArray(String key) {
    return new String[0];
  }
}
