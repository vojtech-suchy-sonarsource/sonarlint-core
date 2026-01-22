/*
ACR-5af4ff35336d4b7abdad8dcc696975c8
ACR-fbf83917a49643bc8218dccce6dc812e
ACR-b9d0439ac8e74c0aa1d660708dc4a8b8
ACR-6a166ffb44d54b24a5f035f6a4d3d839
ACR-2c9fd44db61044e0b3d1d5ed4be766fa
ACR-7b09e4ec0692444aa935786f9a98e280
ACR-e69e42c2b0cd41f5886557643ba0e60d
ACR-e4213fa38c494ff39f62e7f478018b0b
ACR-c3e6d53b7e694f9f80372db8cdad6b40
ACR-3f9e5e8dd7814448bf1cc2b65fb9d90f
ACR-9be76e3181df478fb778583536b1a718
ACR-d85fc574085e44b88e4ce54fb08d2b67
ACR-8b67d72200d34a978334ef0378232242
ACR-afadd505d8e84882817057b01177d842
ACR-d8c5303aa87f41e48a897b554e212c04
ACR-bbee401acaa24809b79514560d3d47fe
ACR-ea6bca7e13d9491cadacd7c31c764290
 */
package org.sonarsource.sonarlint.core.commons;

import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SonarLintUserHomeTests {

  @Test
  void env_setting_should_override_default_home() {
    var customHome = "/custom/home";
    assertThat(SonarLintUserHome.home(customHome)).isEqualTo(Paths.get(customHome));
  }

  @Test
  void default_home_should_be_in_user_home() {
    assertThat(SonarLintUserHome.get()).isEqualTo(Paths.get(System.getProperty("user.home")).resolve(".sonarlint"));
  }
}
