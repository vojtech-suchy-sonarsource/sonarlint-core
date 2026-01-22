/*
ACR-5e6ca4ad02314eed9e3687ce8c040180
ACR-c8c0342566e84f8797baa196b64c2b19
ACR-0a8131933c1044deb10b7c1e5ba866fa
ACR-c2c928d6c7fd4966984a1b691f36deac
ACR-a70198f5115240d697f54ce065f1a512
ACR-eb0c5eedc8ac46adabe91b3ce9053e65
ACR-37afc64b9b2642509935df842cc88e68
ACR-a4b878d826b84df1a275eda64e98cc34
ACR-97fbf70c19e5402aa740ecb986044401
ACR-3eac00fbc2d4401d8a8a4ebb1bd64f78
ACR-60e1cc10d1f64c898d552843a9996844
ACR-60ea9b0a633947cead69f2ac66895155
ACR-e447dccb375047c6a6e02374fee1b5fa
ACR-661c3fd693a648c3865cb0edc6094555
ACR-a01b598dcea44d15a5ab4571b9f1f770
ACR-16e89cbc072e446ba19e99182bf16ab7
ACR-d082543a8ba44523b35f37f1de846962
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
