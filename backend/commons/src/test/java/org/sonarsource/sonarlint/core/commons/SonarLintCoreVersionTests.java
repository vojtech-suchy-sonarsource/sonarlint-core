/*
ACR-af3bfbad3cf047d1862789373f72eaa3
ACR-4b1a500000fa4176acd7ecb085457ba5
ACR-f2ae090607a24fdf9bf29684c515101c
ACR-8427464343fd48818b899a544e8726e4
ACR-72565da3aa8c4a9f953170316284d532
ACR-6f45c0256fad4483b82a594e6283c892
ACR-e7cc4c8dad834b259b8f2a548eb194dd
ACR-32faee33ad0a41fb963bf0fbba2d9462
ACR-e5ff5199cded4fa68ba37899b52ad343
ACR-7d0b8cd25db84f7db45a76335b8e38a8
ACR-dc49c981d8d94c3a8cad11284c325f4c
ACR-34b91d50b2ee48ac8374b275bea1cd74
ACR-aa299bc9e83841eab781671f9b499deb
ACR-05dc7c6be02e4e6da9c4ed3bf43da0c7
ACR-366aeb4d9bfc4717b5348e6251a56c1c
ACR-7e6367421c824b0c9adbab5b6dd88368
ACR-01b6ad4dbdff486392ef66201712c1d8
 */
package org.sonarsource.sonarlint.core.commons;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SonarLintCoreVersionTests {

  @Test
  void testVersionFallback() {
    var version = SonarLintCoreVersion.getLibraryVersion();
    assertThat(isVersion(version)).isTrue();
  }

  @Test
  void testVersion() {
    var version = SonarLintCoreVersion.get();
    assertThat(isVersion(version)).isTrue();
  }

  @Test
  void testVersionAssert() {
    assertThat(isVersion("2.1")).isTrue();
    assertThat(isVersion("2.0-SNAPSHOT")).isTrue();
    assertThat(isVersion("2.0.0-SNAPSHOT")).isTrue();
    assertThat(isVersion("2-SNAPSHOT")).isFalse();
    assertThat(isVersion("unknown")).isFalse();
    assertThat(isVersion(null)).isFalse();
  }

  private boolean isVersion(String version) {
    if (version == null) {
      return false;
    }
    var regex = "(\\d+\\.\\d+(?:\\.\\d+)*).*";
    var pattern = Pattern.compile(regex);
    var matcher = pattern.matcher(version);

    return matcher.find();
  }
}
