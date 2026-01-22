/*
ACR-7865115245e34357970c31bab4058334
ACR-787897d766a84726866a9b0f9863195b
ACR-bf29a07bbec6479d8c10283d839605af
ACR-6a355885f607416c9fda3396826279eb
ACR-c2125b7da09b4290b77a7e156e020947
ACR-088836faa60a4bbfb5096fbd54ec80db
ACR-197d6ebbfcfe44c787f69fe6ed406f2b
ACR-00a81e381772420c9a23a5ebf097ca9f
ACR-a6ee093e898c4adb954b8399cf55d911
ACR-d221fd89d7b94b3588b290bf6053ac5e
ACR-b40753df46a74a8f8b446692290b16fe
ACR-92cdd38130fb452ca8af14bea502ee2f
ACR-af301bce5d9e420593e269446a403946
ACR-23a43790cfeb4e97b54425066193c38d
ACR-30807648c80c4f3fae03d834dcdc6e89
ACR-7f0b2d13ee3c4345aa9881cd4a6d7dce
ACR-3529ae6907bf4b9b8dc442db57270735
 */
package org.sonarsource.sonarlint.core.commons.validation;

import java.util.Map;
import java.util.regex.PatternSyntaxException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegexpValidatorTest {

  public static final String TEST_REGEXP = "[0-9]+";

  @Test
  void should_throw_exception_on_invalid_regexp() {
    assertThatThrownBy(() -> new RegexpValidator("[4-[8)"))
      .isInstanceOf(PatternSyntaxException.class);
  }

  @Test
  void should_return_empty_invalid_fields() {
    RegexpValidator validator = new RegexpValidator(TEST_REGEXP);

    InvalidFields invalidFields = validator.validateAll(Map.of(
      "field1", "12345",
      "field2", "455668",
      "field3", "0"
    ));

    assertThat(invalidFields.hasInvalidFields()).isFalse();
    assertThat(invalidFields.getNames()).isEmpty();
  }

  @Test
  void should_return_one_invalid_field() {
    RegexpValidator validator = new RegexpValidator(TEST_REGEXP);

    InvalidFields invalidFields = validator.validateAll(Map.of(
      "field1", "12345",
      "field2", "-455668",
      "field3", "0"
    ));

    assertThat(invalidFields.hasInvalidFields()).isTrue();
    assertThat(invalidFields.getNames())
      .containsExactlyInAnyOrder("field2");
  }

  @Test
  void should_return_all_invalid_fields() {
    RegexpValidator validator = new RegexpValidator(TEST_REGEXP);

    InvalidFields invalidFields = validator.validateAll(Map.of(
      "field1", "sqrt(12345)",
      "field2", "-455668",
      "field3", "^0^"
    ));

    assertThat(invalidFields.hasInvalidFields()).isTrue();
    assertThat(invalidFields.getNames())
      .containsExactlyInAnyOrder("field1", "field2", "field3");
  }
}
