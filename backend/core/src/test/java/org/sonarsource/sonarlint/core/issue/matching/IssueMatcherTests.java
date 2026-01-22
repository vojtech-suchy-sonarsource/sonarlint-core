/*
ACR-1add302ce7fe41229cdbab311cbd345a
ACR-7bc2a53c8aaf494fbf3ffa92190d96da
ACR-9cd995d20b5c46c1b79edbc94f5eb0b3
ACR-e7044e8574554d229d87a4451ed3e9d6
ACR-745844a8b9464285afe0aa8c9ffc0e47
ACR-392538d6baa94a15819ff6c56191140d
ACR-1b9d00a68afe49e1a79f34ec776b8539
ACR-f4ba010ffe6741a1b26818f0b43885f2
ACR-63ad1cde75324bc180d293a2e072188f
ACR-6a3c0901c68243f09bb733353eb015c8
ACR-83e56a24ba624329b33ce19926e94c1b
ACR-3aaa34004c3f47059cea48caf8d0626b
ACR-e48b35d97ac0467a921287cab594c503
ACR-e9144772e9af435e8aa7ed709d2c3352
ACR-3f5f79e65a9a4485bd7c62e531b730dc
ACR-a25e28ab2b474dea9e5af0ae3e8dfa50
ACR-6871f2755fb84066aef67e6cb5c11f55
 */
package org.sonarsource.sonarlint.core.issue.matching;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.tracking.matching.IssueMatcher;
import org.sonarsource.sonarlint.core.tracking.matching.MatchingAttributesMapper;

import static org.assertj.core.api.Assertions.assertThat;

class IssueMatcherTests {

  private IssueMatcher<FakeIssueType, FakeIssueType> underTest;

  private static class FakeIssueType {
    private String ruleKey = "dummy rule key";
    private Integer line;
    private String textRangeHash;
    private String lineHash;
    private String message = "dummy message";
    private String serverKey;

    public FakeIssueType setRuleKey(String ruleKey) {
      this.ruleKey = ruleKey;
      return this;
    }

    public FakeIssueType setLine(Integer line) {
      this.line = line;
      return this;
    }

    public FakeIssueType setTextRangeHash(String hash) {
      this.textRangeHash = hash;
      return this;
    }

    public FakeIssueType setLineHash(String hash) {
      this.lineHash = hash;
      return this;
    }

    public FakeIssueType setMessage(String message) {
      this.message = message;
      return this;
    }

    public FakeIssueType setServerKey(String key) {
      this.serverKey = key;
      return this;
    }
  }

  private static class FakeIssueMatchingAttributeMapper implements MatchingAttributesMapper<FakeIssueType> {

    @Override
    public String getRuleKey(FakeIssueType issue) {
      return issue.ruleKey;
    }

    @Override
    public Optional<Integer> getLine(FakeIssueType issue) {
      return Optional.ofNullable(issue.line);
    }

    @Override
    public Optional<String> getTextRangeHash(FakeIssueType issue) {
      return Optional.ofNullable(issue.textRangeHash);
    }

    @Override
    public Optional<String> getLineHash(FakeIssueType issue) {
      return Optional.ofNullable(issue.lineHash);
    }

    @Override
    public String getMessage(FakeIssueType issue) {
      return issue.message;
    }

    @Override
    public Optional<String> getServerIssueKey(FakeIssueType issue) {
      return Optional.ofNullable(issue.serverKey);
    }

  }

  @Test
  void should_not_match_issues_with_different_rule_key() {
    var issueForRuleA = new FakeIssueType().setRuleKey("ruleA");
    var issueForRuleB = new FakeIssueType().setRuleKey("ruleB");
    underTest = new IssueMatcher<>(new FakeIssueMatchingAttributeMapper(), List.of(issueForRuleB));

    var result = underTest.matchWith(new FakeIssueMatchingAttributeMapper(), List.of(issueForRuleA));

    assertThat(result.getMatchedLefts()).isEmpty();
  }

  @Test
  void should_match_by_line_and_text_range_hash() {
    var baseIssue = new FakeIssueType().setLine(7).setTextRangeHash("same range hash");

    var differentLine = new FakeIssueType().setLine(8).setTextRangeHash("same range hash");
    var differentTextRangeHash = new FakeIssueType().setLine(7).setTextRangeHash("different range hash");
    var differentBoth = new FakeIssueType().setLine(8).setTextRangeHash("different range hash");
    var same = new FakeIssueType().setLine(7).setTextRangeHash("same range hash");
    underTest = new IssueMatcher<>(new FakeIssueMatchingAttributeMapper(), List.of(baseIssue));

    var result = underTest.matchWith(new FakeIssueMatchingAttributeMapper(), List.of(differentLine, differentTextRangeHash, differentBoth, same));

    assertThat(result.getMatchedLefts()).hasSize(1);
    assertThat(result.getMatch(same)).isEqualTo(baseIssue);
  }

  @Test
  void should_match_by_line_and_line_hash_even_if_different_message_and_text_range() {
    var baseIssue = new FakeIssueType().setLine(7).setLineHash("same line hash").setMessage("different message").setTextRangeHash("different range hash");

    var differentLine = new FakeIssueType().setLine(8).setLineHash("same line hash");
    var differentLineHash = new FakeIssueType().setLine(7).setLineHash("different line hash");
    var differentBoth = new FakeIssueType().setLine(8).setLineHash("different line hash");
    var same = new FakeIssueType().setLine(7).setLineHash("same line hash");
    underTest = new IssueMatcher<>(new FakeIssueMatchingAttributeMapper(), List.of(baseIssue));

    var result = underTest.matchWith(new FakeIssueMatchingAttributeMapper(), List.of(differentLine, differentLineHash, differentBoth, same));

    assertThat(result.getMatchedLefts()).hasSize(1);
    assertThat(result.getMatch(same)).isEqualTo(baseIssue);
  }

  @Test
  void should_match_by_line_and_message_even_if_different_hash() {
    var baseIssue = new FakeIssueType().setLine(7).setMessage("same message").setTextRangeHash("different range hash");
    var differentLine = new FakeIssueType().setLine(8).setMessage("same message");
    var differentMessage = new FakeIssueType().setLine(7).setMessage("different message");
    var differentBoth = new FakeIssueType().setLine(8).setMessage("different message");
    var same = new FakeIssueType().setLine(7).setMessage("same message");
    underTest = new IssueMatcher<>(new FakeIssueMatchingAttributeMapper(), List.of(baseIssue));

    var result = underTest.matchWith(new FakeIssueMatchingAttributeMapper(), List.of(differentLine, differentMessage, differentBoth, same));

    assertThat(result.getMatchedLefts()).hasSize(1);
    assertThat(result.getMatch(same)).isEqualTo(baseIssue);
  }

  @Test
  void should_match_by_text_range_hash_even_if_no_line_number_before() {
    var baseIssueWithNoLine = new FakeIssueType().setTextRangeHash("same range hash");
    var differentLine = new FakeIssueType().setLine(8).setTextRangeHash("same range hash");
    underTest = new IssueMatcher<>(new FakeIssueMatchingAttributeMapper(), List.of(baseIssueWithNoLine));

    var result = underTest.matchWith(new FakeIssueMatchingAttributeMapper(), List.of(differentLine));

    assertThat(result.getMatchedLefts()).hasSize(1);
    assertThat(result.getMatch(differentLine)).isEqualTo(baseIssueWithNoLine);
  }

  @Test
  void should_match_by_text_range_hash_even_if_different_line_number() {
    var baseIssue = new FakeIssueType().setLine(7).setTextRangeHash("same range hash");
    var differentLine = new FakeIssueType().setLine(8).setTextRangeHash("same range hash");
    underTest = new IssueMatcher<>(new FakeIssueMatchingAttributeMapper(), List.of(baseIssue));

    var result = underTest.matchWith(new FakeIssueMatchingAttributeMapper(), List.of(differentLine));

    assertThat(result.getMatchedLefts()).hasSize(1);
    assertThat(result.getMatch(differentLine)).isEqualTo(baseIssue);
  }

  @Test
  void should_match_by_line_hash_even_if_no_line_number_before() {
    var baseIssueWithNoLine = new FakeIssueType().setLineHash("same line hash");
    var differentLine = new FakeIssueType().setLine(8).setLineHash("same line hash");
    underTest = new IssueMatcher<>(new FakeIssueMatchingAttributeMapper(), List.of(baseIssueWithNoLine));

    var result = underTest.matchWith(new FakeIssueMatchingAttributeMapper(), List.of(differentLine));

    assertThat(result.getMatchedLefts()).hasSize(1);
    assertThat(result.getMatch(differentLine)).isEqualTo(baseIssueWithNoLine);
  }

  @Test
  void should_match_by_line_hash_even_if_different_line_number() {
    var baseIssue = new FakeIssueType().setLine(7).setLineHash("same line hash");
    var differentLine = new FakeIssueType().setLine(8).setLineHash("same line hash");
    underTest = new IssueMatcher<>(new FakeIssueMatchingAttributeMapper(), List.of(baseIssue));

    var result = underTest.matchWith(new FakeIssueMatchingAttributeMapper(), List.of(differentLine));

    assertThat(result.getMatchedLefts()).hasSize(1);
    assertThat(result.getMatch(differentLine)).isEqualTo(baseIssue);
  }

  @Test
  void should_match_by_serverKey_even_if_no_line_number_before() {
    var baseIssueWithNoLine = new FakeIssueType().setServerKey("same key");
    var differentLine = new FakeIssueType().setLine(8).setServerKey("same key");
    underTest = new IssueMatcher<>(new FakeIssueMatchingAttributeMapper(), List.of(baseIssueWithNoLine));

    var result = underTest.matchWith(new FakeIssueMatchingAttributeMapper(), List.of(differentLine));

    assertThat(result.getMatchedLefts()).hasSize(1);
    assertThat(result.getMatch(differentLine)).isEqualTo(baseIssueWithNoLine);
  }

  @Test
  void should_match_by_serverKey_even_if_different_line_number() {
    var baseIssue = new FakeIssueType().setLine(7).setServerKey("same key");
    var differentLine = new FakeIssueType().setLine(8).setServerKey("same key");
    underTest = new IssueMatcher<>(new FakeIssueMatchingAttributeMapper(), List.of(baseIssue));

    var result = underTest.matchWith(new FakeIssueMatchingAttributeMapper(), List.of(differentLine));

    assertThat(result.getMatchedLefts()).hasSize(1);
    assertThat(result.getMatch(differentLine)).isEqualTo(baseIssue);
  }

}
