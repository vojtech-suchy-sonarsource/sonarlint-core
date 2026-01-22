/*
ACR-ce4e024d4fa84dbfa93cc0e51e47822f
ACR-990ddccc773245868092ef289741d230
ACR-98e9ce3cb44e415f8092657f6bd52e59
ACR-7023e22b57414962968bcbe096c55e36
ACR-1f4eb0da05ac417ab5194546ac8dca04
ACR-1408a82aee34420eb70eaa14235a9d80
ACR-cf3e36879a0e423e9b15836595b5dd13
ACR-1e6a4df2772e490e822c9cd882a6e73e
ACR-1de7f57004c34678bd3d744fc808393a
ACR-d26048f2185d449a8b889af88a1a68bc
ACR-7c664d4f2fd54aa4a3966d4f6c036179
ACR-c1d4e9070cb54c3dab20bd87effad85d
ACR-91bacf4506b34a8c8f2451f7d4394d40
ACR-b0abbeaca082475da14abe68d47c3302
ACR-0eca93b476c348edb0d9c4867f18fad4
ACR-66116d733e674dabb7ccfea7ea6a1b25
ACR-70d6391214404437a1a10f363eda3f6d
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SonarLintPathPatternTests {

  @Test
  void constructor_should_add_double_star_prefix_when_not_present() {
    assertThat(new SonarLintPathPattern("*.java")).hasToString("**/*.java");
  }

  @Test
  void constructor_should_not_add_double_star_prefix_when_already_present() {
    assertThat(new SonarLintPathPattern("**/*.java")).hasToString("**/*.java");
  }

  @Test
  void create_should_return_array_of_patterns() {
    var patterns = SonarLintPathPattern.create(new String[]{"*.java", "*.xml"});
    
    assertThat(patterns).hasSize(2);
    assertThat(patterns[0].toString()).hasToString("**/*.java");
    assertThat(patterns[1].toString()).hasToString("**/*.xml");
  }

  @Test
  void create_should_return_empty_array_when_input_is_empty() {
    assertThat(SonarLintPathPattern.create(new String[]{})).isEmpty();
  }

  @Test
  void match_should_match_java_files() {
    var pattern = new SonarLintPathPattern("*.java");
    
    assertThat(pattern.match("src/main/java/Test.java")).isTrue();
    assertThat(pattern.match("src/test/java/Test.java")).isTrue();
    assertThat(pattern.match("Test.java")).isTrue();
    assertThat(pattern.match("Test.txt")).isFalse();
  }

  @Test
  void match_should_match_xml_files() {
    var pattern = new SonarLintPathPattern("*.xml");
    
    assertThat(pattern.match("pom.xml")).isTrue();
    assertThat(pattern.match("src/main/resources/config.xml")).isTrue();
    assertThat(pattern.match("Test.java")).isFalse();
  }

  @Test
  void match_should_match_with_path_patterns() {
    var pattern = new SonarLintPathPattern("src/**/*.java");
    
    assertThat(pattern.match("src/main/java/Test.java")).isTrue();
    assertThat(pattern.match("src/test/java/Test.java")).isTrue();
    assertThat(pattern.match("Test.java")).isFalse();
  }

  @Test
  void match_should_match_test_patterns() {
    var pattern = new SonarLintPathPattern("**/test/**/*.java");
    
    assertThat(pattern.match("src/test/java/Test.java")).isTrue();
    assertThat(pattern.match("src/main/java/Test.java")).isFalse();
  }

  @Test
  void match_with_case_sensitive_should_respect_case() {
    var pattern = new SonarLintPathPattern("*.JAVA");
    
    assertThat(pattern.match("src/main/java/Test.java", true)).isFalse();
    assertThat(pattern.match("src/main/java/Test.JAVA", true)).isTrue();
  }

  @Test
  void match_should_handle_different_path_separators() {
    var pattern = new SonarLintPathPattern("*.java");
    
    assertThat(pattern.match("src\\main\\java\\Test.java")).isTrue();
    assertThat(pattern.match("src/main/java/Test.java")).isTrue();
    assertThat(pattern.match("src\\test\\java\\Test.java")).isTrue();
    assertThat(pattern.match("Test.java")).isTrue();
  }

  @Test
  void match_should_handle_path_without_extension() {
    var pattern = new SonarLintPathPattern("*.java");
    
    var result = pattern.match("src/main/java/Test");
    
    assertThat(result).isFalse();
  }

  @Test
  void match_should_handle_path_with_dot_but_no_extension() {
    var pattern = new SonarLintPathPattern("*.java");
    
    var result = pattern.match("src/main/java/Test.");
    
    assertThat(result).isFalse();
  }

  @Test
  void toString_should_return_pattern_string() {
    var pattern = new SonarLintPathPattern("*.java");
    
    var result = pattern.toString();
    
    assertThat(result).isEqualTo("**/*.java");
  }

  @Test
  void sanitizeExtension_should_handle_null() {
    assertThat(SonarLintPathPattern.sanitizeExtension(null)).isNull();
  }

  @Test
  void sanitizeExtension_should_handle_empty_string() {
    assertThat(SonarLintPathPattern.sanitizeExtension("")).isEmpty();
  }

  @Test
  void sanitizeExtension_should_remove_leading_dot() {
    assertThat(SonarLintPathPattern.sanitizeExtension(".java")).isEqualTo("java");
  }

  @Test
  void sanitizeExtension_should_convert_to_lowercase() {
    assertThat(SonarLintPathPattern.sanitizeExtension("JAVA")).isEqualTo("java");
  }

  @Test
  void sanitizeExtension_should_handle_extension_without_dot() {
    assertThat(SonarLintPathPattern.sanitizeExtension("java")).isEqualTo("java");
  }

  @Test
  void sanitizeExtension_should_handle_mixed_case_with_dot() {
    assertThat(SonarLintPathPattern.sanitizeExtension(".JaVa")).isEqualTo("java");
  }
} 
