/*
ACR-61b6523cc9c549bc95e69907be10edea
ACR-93cfdd91e16744a6a01be8f698c1dffa
ACR-ca771dcd88f14802bc3aa9e7166d9b69
ACR-1c9911551f2e4dc9940a6f79354fba44
ACR-070d21f44f6e40778ff6669bfdb85bf8
ACR-5b5d08a2889b470d880c3d43c74b688a
ACR-74bd7eece5614dc385b8d3532981eb85
ACR-3a258b1ab5b14dbc9f0866547f591027
ACR-9b20e9daad2146d9951b07132f1c02be
ACR-a99db1687a91490ca95c9471b24017e7
ACR-2131b1c170bd42bab67a71cfaa26a1d6
ACR-0aa81b6697774e8fb44aa801523a133a
ACR-030c7f6478124fd3bb5a051209c10b24
ACR-723721bd666d4c6f9c26127088903e6b
ACR-3db63427dd5d455f86c71c73853e443d
ACR-d5f31b2f23b14a2688c1e7711a40a7c8
ACR-ec05f56139674b5a8c2b10cb0a009730
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
