/*
ACR-7f063c86e8d24cb3a4a9851ecc3de1e9
ACR-75effc5d4b0a4d8abb869baef5267ae8
ACR-44612e6fb6e441e2980a1d1662c91c06
ACR-7bf3bb7152ea4ca8bcdfcdea9f5a1874
ACR-ccc0e19f7dd4445791e6462bc7fde6ed
ACR-711defe04e5a422fb1c936b6a68c9826
ACR-df6cce8b59aa4efa99d3f5c2191b0edc
ACR-4ca0d638c36643b1a645e2e82863e044
ACR-b4fa498961be4d6f9977ed1438795e3f
ACR-3d68230bb1b1485986e8494a1ba9a0ad
ACR-0cd598fafdc44b0689713fc9774767ea
ACR-d47febae67174e37a53a0cfb2ab7452f
ACR-0c80f5f71e44427d9a436fe189495132
ACR-5dbd677ce1274c7bacdd9518396b005a
ACR-9a0f4026e66f4c77994448b334d990d6
ACR-07e540f71ce94ec2b60cd9fe419a2a1c
ACR-b4b6a182b3004f3b979fc268933fc63a
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.pattern;

import org.junit.jupiter.api.Test;
import org.sonar.api.rules.Rule;

import static org.assertj.core.api.Assertions.assertThat;

class IssuePatternTests {

  @Test
  void shouldMatchJavaFile() {
    var javaFile = "org/foo/Bar.java";
    assertThat(new IssuePattern("org/foo/Bar.java", "*").matchFile(javaFile)).isTrue();
    assertThat(new IssuePattern("org/foo/*", "*").matchFile(javaFile)).isTrue();
    assertThat(new IssuePattern("**Bar.java", "*").matchFile(javaFile)).isTrue();
    assertThat(new IssuePattern("**", "*").matchFile(javaFile)).isTrue();
    assertThat(new IssuePattern("org/*/?ar.java", "*").matchFile(javaFile)).isTrue();

    assertThat(new IssuePattern("org/other/Hello.java", "*").matchFile(javaFile)).isFalse();
    assertThat(new IssuePattern("org/foo/Hello.java", "*").matchFile(javaFile)).isFalse();
    assertThat(new IssuePattern("org/*/??ar.java", "*").matchFile(javaFile)).isFalse();
    assertThat(new IssuePattern("org/*/??ar.java", "*").matchFile(null)).isFalse();
    assertThat(new IssuePattern("org/*/??ar.java", "*").matchFile("plop")).isFalse();
  }

  @Test
  void shouldMatchRule() {
    var rule = Rule.create("checkstyle", "IllegalRegexp", "").ruleKey();
    assertThat(new IssuePattern("*", "*").matchRule(rule)).isTrue();
    assertThat(new IssuePattern("*", "checkstyle:*").matchRule(rule)).isTrue();
    assertThat(new IssuePattern("*", "checkstyle:IllegalRegexp").matchRule(rule)).isTrue();
    assertThat(new IssuePattern("*", "checkstyle:Illegal*").matchRule(rule)).isTrue();
    assertThat(new IssuePattern("*", "*:*Illegal*").matchRule(rule)).isTrue();

    assertThat(new IssuePattern("*", "pmd:IllegalRegexp").matchRule(rule)).isFalse();
    assertThat(new IssuePattern("*", "pmd:*").matchRule(rule)).isFalse();
    assertThat(new IssuePattern("*", "*:Foo*IllegalRegexp").matchRule(rule)).isFalse();
  }

}
