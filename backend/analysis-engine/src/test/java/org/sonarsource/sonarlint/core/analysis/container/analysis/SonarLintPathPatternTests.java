/*
ACR-f138c056fc914d5e8fea8e52348a8591
ACR-0c0717ef9d964a5589d26110dd318c5e
ACR-7e21232d212044f2a86d414201eec672
ACR-aaf0d442e5c0455db8f2ecdf2beb3b6c
ACR-d9fa2d6dd54f424a94ecc0a80924663b
ACR-b572189fa54743fa8fc2e8e16ba9bb0a
ACR-25d08ba27a694f1c9b135e96251f086b
ACR-5496516d329f4e94a0ace20c7c4238c8
ACR-750acd489ae7430985f3abe642c19302
ACR-94e4f7d1130c4355a54d776d5cfc2d01
ACR-9e53be5ac7c04df59a56c74d8611c927
ACR-605429f4703b497b86b041a171c12e84
ACR-3209cc7457cf4c169c58d2c4124a824f
ACR-ce4f116ce1234587a8add7ea3f59e83f
ACR-c1d942e7d7314ff3a7f9ba2785b779ae
ACR-7bde6859c91a4c0792359d8e8ad11eff
ACR-f304613ab13c400191256f9bd751f8f1
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
