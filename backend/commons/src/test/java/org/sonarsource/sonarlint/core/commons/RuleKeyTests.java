/*
ACR-3d48499ca1f54567a4f5a900a6592667
ACR-f6c0089239164a9da22976d4c8140005
ACR-8d6755ff43d349868f0fd98140598273
ACR-cf0781ac500248b5a5210a24c7371476
ACR-62783294603746578105116e16053cfb
ACR-78068ae19f2843ee98b55b1edec57ba4
ACR-18b4aa3227fe4303b4a428deee1be49d
ACR-abf8cdefbbe94a038068cfffb54e3cbe
ACR-584447841bee45d7aab6ad09eb30d4d0
ACR-1da733557bf843ec892eceba50619d6c
ACR-b7c6c4103cf3476a928c5ae4430f950b
ACR-f9bad724ab2f4d3bb010cf14e7e3b1b3
ACR-2a13195d9c114bb2b21e685746de4614
ACR-fc848fad724149089272a4a708613fbf
ACR-66907cb3e5a04e6fa4e01ef65401639a
ACR-230f48dff4b0445f92f2a93b85267824
ACR-2f30f3709aa041a89c5d1fdb9c815bae
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
