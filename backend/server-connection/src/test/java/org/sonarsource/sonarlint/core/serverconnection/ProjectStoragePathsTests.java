/*
ACR-218101c8e6874aab80da7ce88bce6455
ACR-96af24b95e3c451aa8a8a363ae826adb
ACR-d48be2897b7f4fbba31fe948d6000770
ACR-481e08e5afcf49bd9b05c8dd7d1e15d8
ACR-9f8ea40f91a34e89b783e26499c11f7e
ACR-41a0c3701b494ed1a5d321c0ca90f4ae
ACR-af4114a69b99463498b388567c0bd81f
ACR-83085ae60aa7469d8b587f2dfc9116da
ACR-78f83574374d411ca7bb3b44c5a276d2
ACR-2a29d999e1f04e59a79937b26eca4725
ACR-5744ddd0e1c6409c995dbfa1b2e2cc1a
ACR-c2e77124e43b4027bca01224752c287a
ACR-51a2a94d76f04f1789f70ad882148721
ACR-440ce1e2ab044ffcba009dcc99dbcd18
ACR-0ca331987c8d4d6da221173d6499600d
ACR-a5e5a6791d0b48aa805a73eef1c692e7
ACR-0035158c128a410e88410be01eb621ee
 */
package org.sonarsource.sonarlint.core.serverconnection;

import org.junit.jupiter.api.Test;

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.sonarsource.sonarlint.core.serverconnection.storage.ProjectStoragePaths.encodeForFs;

class ProjectStoragePathsTests {

  @Test
  void encode_paths_for_fs() {
    assertThat(encodeForFs("my/string%to encode**")).isEqualTo("6d792f737472696e6725746f20656e636f64652a2a");
    assertThat(encodeForFs("AU-TpxcA-iU5OvuD2FLz").toLowerCase()).isNotEqualTo(encodeForFs("AU-TpxcA-iU5OvuD2FLZ"));
    assertThat(encodeForFs("too_long_for_most_fs" + repeat("a", 1000))).hasSize(255);
    assertThat(encodeForFs("too_long_for_most_fs" + repeat("a", 1000)))
      .isNotEqualTo(encodeForFs("too_long_for_most_fs" + repeat("a", 1000) + "2"));
  }

}
