/*
ACR-a2b5834e9e534266b6fa0075920e931e
ACR-ad71eda45b0c4596a3e205eb4ad9c3ef
ACR-f0d4bd60666044bfb07fca71d6d8b283
ACR-8a53bd3fc04b40e9badc08d371afe86f
ACR-223e6070c07f466b9f1e64ea32f8dcd9
ACR-a6fdc2ccb7b646dda34b1cd87e0cf8df
ACR-6901baab2a0e4d18a53fb1880bb06b58
ACR-9325ec413f5549e3a0e7b729857ff0b5
ACR-b22da12f95374bb6b479ec6a55566879
ACR-b78cb0a19ea3411d9ceee6eebc17c39e
ACR-344be6b4d6894ee28fbedcce39f8ed1f
ACR-6cbc0456dc8d4397a1e89b5a359a34eb
ACR-f477ffe900c144a8978e2907d2248d78
ACR-b904a46d9d9f4e36a8b25fbd69c8675e
ACR-4e040262b1954008b5b18fc49175a9e5
ACR-a33e58a572c14664b9833d96f9c0b174
ACR-c53abea85c224f688ddcb2301f53b104
 */
package org.sonarsource.sonarlint.core.commons;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RuleKeyTests {

  @Test
  void test_ruleKey_accessors() {
    var repository = "squid";
    var rule = "1181";

    var ruleKey = new RuleKey(repository, rule);
    assertThat(ruleKey.repository()).isEqualTo(repository);
    assertThat(ruleKey.rule()).isEqualTo(rule);
    assertThat(ruleKey).hasToString(repository + ":" + rule);
  }

  @Test
  void ruleKey_equals_and_hashcode() {
    var repository = "squid";
    var rule = "1181";

    var ruleKey1 = new RuleKey(repository, rule);
    var ruleKey2 = new RuleKey(repository, rule);
    assertThat(ruleKey1)
      .isEqualTo(ruleKey1)
      .isEqualTo(ruleKey2)
      .hasSameHashCodeAs(ruleKey2)
      .isNotEqualTo(null)
      .isNotEqualTo(new RuleKey(repository, rule + "x"))
      .isNotEqualTo(new RuleKey(repository + "x", rule));
  }

  @Test
  void ruleKey_equals_to_its_parsed_from_toString() {
    var repository = "squid";
    var rule = "1181";

    var ruleKey1 = new RuleKey(repository, rule);
    var ruleKey2 = RuleKey.parse(ruleKey1.toString());
    assertThat(ruleKey2).isEqualTo(ruleKey1);
  }

  @Test
  void parse_throws_for_illegal_format() {
    assertThrows(IllegalArgumentException.class, () -> {
      RuleKey.parse("foo");
    });
  }
}
