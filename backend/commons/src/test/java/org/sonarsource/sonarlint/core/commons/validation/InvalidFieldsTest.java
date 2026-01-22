/*
ACR-628682f983b94f67a9e8f3f3aad00276
ACR-9e836822e3114e3f852455a8f45a4653
ACR-bd55ecb52d4643ebae20e8c54937a4b8
ACR-8ec3692bcc4e4ae69eac831649b6bf89
ACR-b31c08b72ded4742aa7f588526854fe2
ACR-79d89734b8f64a0196d5992dfe2edb51
ACR-209c3191c50f4a15a7e0d46d7b889c5c
ACR-206921ebceb749c3a8e9ac9205625092
ACR-1d4e571df7114c8eb1aafffbe0608b3b
ACR-8c4317585e854a158fc4389b733685ea
ACR-ecbc2e7842d74d45bcce2645954fa0c2
ACR-623349d10c3c4f16b34bcecd4e2efd5f
ACR-754fa185a2b94732a39901ee1fbdaaa8
ACR-ac93eb21726749129d947bb9e60be8e8
ACR-67b0015492d54e8c8e87088735ba2332
ACR-3e201065aa514ba19734d01a767fb606
ACR-d3ee965c87fa43b8bffa8bee6ce74013
 */
package org.sonarsource.sonarlint.core.commons.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidFieldsTest {

  public static final String[] EXPECTED = {"name1", "name2", "name3"};

  @Test
  void should_have_no_invalid_fields_initially() {
    InvalidFields tested = new InvalidFields();

    assertThat(tested.hasInvalidFields()).isFalse();
  }

  @Test
  void should_have_invalid_fields_after_adding_one() {
    InvalidFields tested = new InvalidFields();

    tested.add("name1");

    assertThat(tested.hasInvalidFields()).isTrue();
  }

  @Test
  void should_include_all_added_fields() {
    InvalidFields tested = new InvalidFields();

    tested.add("name1");
    tested.add("name2");
    tested.add("name3");
    String[] names = tested.getNames();

    assertThat(names).containsExactly(EXPECTED);
  }
}
