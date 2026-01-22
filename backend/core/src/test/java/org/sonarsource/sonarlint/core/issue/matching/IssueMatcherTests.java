/*
ACR-4d48aee2a2f64c1a9cccbcd011d7a69d
ACR-dd71255fa1ba40e4aad644c254cb7385
ACR-791eb42fce834848a3a98b8d7fd803b8
ACR-fa575ab51e5246f488f4568b564fb5b5
ACR-af3e49aa04604d54b73547ef9220b617
ACR-dbc5f9572d1a4d00aff89e78545c1512
ACR-601d3ce3c33144bc85f33470f2225691
ACR-1fa40677886f44f1be039d328c42b1c2
ACR-676d160866f54a3ab47c04f0c9218e10
ACR-98d8f29978d54d12b641c92a2fcd69e6
ACR-586f5ef8a1e948beac4fe81638cbfd7c
ACR-bb2bf6fc3e294ea1a07c945a73215dfe
ACR-fe2a8f290e07469e9f0e64746bb6f481
ACR-1822a6dece644f6cb3e4a6ea6257606f
ACR-9218a47bd26e42758a8ba6ce3709abbb
ACR-82444c5992524f128a4898021a29285d
ACR-8cf6249d793441578ea8f2094ac3d301
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
