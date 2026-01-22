/*
ACR-0c4f2ee818d64cf7a9e709f6784a7f87
ACR-4343ac41ccd34b51893c617ce7e92d1a
ACR-f2cdd21588e14e259303e6c4f7eee60c
ACR-1ba0de7f5026496bbd95a9539c2813f2
ACR-840e13610bfd4e92a27a502bc556545e
ACR-7f5a94fca2e744238aea4c8ab6c6a670
ACR-13357107cbd745a0b9151862a5417ee4
ACR-b1c58eaf66c64b83a599de4b3e1925ce
ACR-67ba8f387bdd418fa42a063e0505de2d
ACR-79c16f7e4fe84ec8bb2357e8108448da
ACR-b095eba1b6b848d88b49e3ee5bf80a46
ACR-5195ffc189f54a8c92dc5b64eedc0ace
ACR-a50581210dc24b38af6086bd2deec1e2
ACR-b83262ff299548ba88de05f904c61c51
ACR-e4070e73085149b68270ce16f8fb9378
ACR-7d18f48e3ac14caca12e95b2185cdcf3
ACR-40e9001640d046d798e5e36ab23902d9
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
