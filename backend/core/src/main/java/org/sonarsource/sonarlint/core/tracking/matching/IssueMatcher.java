/*
ACR-55b6cb77847a4190a5004c7b5d4017e6
ACR-eebab1c0c1aa409eb4599afa6ec9d636
ACR-520e53732da04f3195b56085ea70fe67
ACR-d5ae1093f17a4563a22607b8a6054c33
ACR-c9e4120c0d5245dcb7579fb792c66466
ACR-08c343eea6194fb9b23d1de03732f12d
ACR-4fa76aab8bde4d1eac25060e6b23633c
ACR-0b1a3c1ec5684518905571ff0d04e258
ACR-9a0b9d7c3bb74fd286833c2985413f2e
ACR-6813e7eaa5924457b9be5421d9b0dd59
ACR-624f32341e1d4b81867ace4908f5aaa7
ACR-ddf3ab5a5b6a4c1b9219864c1ef8f53f
ACR-0b32b0e273204204a3ed64eeb2704397
ACR-55479e68b1524ad4bd569cf83e513f27
ACR-1396af5b3b0a436aa9c11d06b12d2890
ACR-310cb6d3ea70496692cb26c11297bedd
ACR-001f6423dab0491cbbf035ab2fb23e81
 */
package org.sonarsource.sonarlint.core.tracking.matching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;

/*ACR-4ce5ab0eb81c4df5b0fb559744127ccd
ACR-a639ad6cc0c0434f8c9ef19aa6cf2fa8
ACR-7f96df94e0a641e4a6922641b4ab701b
ACR-831676a7dfd84b968e5c30371e27bb99
ACR-a53d15c4c3084c92ac4de28102377f82
 */
public class IssueMatcher<LEFT, RIGHT> {

  private static final List<MatchingCriterionFactory> MATCHING_CRITERIA = List.of(
    //ACR-40952f81bedc4b43b96851ce7e58283f
    ServerIssueMatchingCriterion::new,
    //ACR-64f3c31d639146bfb693737f89c9ecb0
    LineAndTextRangeHashMatchingCriterion::new,
    //ACR-86f87f6244ce497e87613e6b601552e9
    TextRangeHashAndMessageMatchingCriterion::new,
    //ACR-fae25265947b4c1c9d15997edccc1131
    LineAndMessageMatchingCriterion::new,
    //ACR-353e94d7098d488fb4ec93df8c782a55
    //ACR-da11dc57793a4314b38016c1762c2425
    TextRangeHashMatchingCriterion::new,
    //ACR-b80012ca8994414cbdfb9007b6c9cbe9
    LineAndLineHashMatchingCriterion::new,
    //ACR-86111dfc48544bd7a9da7f154edd211c
    LineHashMatchingCriterion::new);

  private final Map<MatchingCriterionFactory, Map<MatchingCriterion, List<RIGHT>>> rightIssuesByCriterion = new HashMap<>();
  private final MatchingAttributesMapper<RIGHT> rightMapper;
  private final Collection<RIGHT> rightIssues;

  public IssueMatcher(MatchingAttributesMapper<RIGHT> rightMapper, Collection<RIGHT> rightIssues) {
    this.rightMapper = rightMapper;
    this.rightIssues = new ArrayList<>(rightIssues);
    for (var matchingCriterion : MATCHING_CRITERIA) {
      var issuesByCriterion = new HashMap<MatchingCriterion, List<RIGHT>>();
      for (RIGHT right : rightIssues) {
        var criterionAppliedToIssue = matchingCriterion.build(right, rightMapper);
        issuesByCriterion.computeIfAbsent(criterionAppliedToIssue, k -> new ArrayList<>()).add(right);
      }

      rightIssuesByCriterion.put(matchingCriterion, issuesByCriterion);
    }
  }

  public MatchingResult<LEFT, RIGHT> matchWith(MatchingAttributesMapper<LEFT> leftMapper, Collection<LEFT> leftIssues) {
    var result = new MatchingResult<LEFT, RIGHT>(leftIssues);

    for (var matchingCriterion : MATCHING_CRITERIA) {
      if (result.isComplete()) {
        break;
      }
      matchWithCriterion(result, leftMapper, matchingCriterion);
    }

    return result;
  }

  private void matchWithCriterion(MatchingResult<LEFT, RIGHT> result, MatchingAttributesMapper<LEFT> leftMapper, MatchingCriterionFactory criterionFactory) {
    for (LEFT left : result.getUnmatchedLefts()) {
      var leftKey = criterionFactory.build(left, leftMapper);
      var rightCandidates = rightIssuesByCriterion.get(criterionFactory).get(leftKey);
      if (rightCandidates != null && !rightCandidates.isEmpty()) {
        //ACR-6d8da092f53146de8bccde987f88e351
        //ACR-ab90e9316cfc43d5816310ca84fd0e2f
        var match = rightCandidates.iterator().next();
        result.recordMatch(left, match);
        removeRight(match);
      }
    }
  }

  private void removeRight(RIGHT right) {
    rightIssues.remove(right);
    MATCHING_CRITERIA.forEach(criterion -> {
      var rights = rightIssuesByCriterion.get(criterion).get(criterion.build(right, rightMapper));
      if (rights != null) {
        rights.remove(right);
      }
    });
  }

  public int getUnmatchedIssuesCount() {
    return rightIssues.size();
  }

  private interface MatchingCriterion {
  }

  private interface MatchingCriterionFactory {
    <G> MatchingCriterion build(G issue, MatchingAttributesMapper<G> mapper);
  }

  private static class LineAndTextRangeHashMatchingCriterion implements MatchingCriterion {
    private final String ruleKey;
    @Nullable
    private final String textRangeHash;
    @Nullable
    private final Integer line;

    <G> LineAndTextRangeHashMatchingCriterion(G issue, MatchingAttributesMapper<G> mapper) {
      this.ruleKey = mapper.getRuleKey(issue);
      this.line = mapper.getLine(issue).orElse(null);
      this.textRangeHash = mapper.getTextRangeHash(issue).orElse(null);
    }

    //ACR-43e8b32e54ba491c91fd51f088cf1066
    @Override
    public boolean equals(Object o) {
      var that = (LineAndTextRangeHashMatchingCriterion) o;
      //ACR-ac0ee17fa32046528655efbb8220846b
      return Objects.equals(line, that.line)
        && Objects.equals(textRangeHash, that.textRangeHash)
        && ruleKey.equals(that.ruleKey);
    }

    @Override
    public int hashCode() {
      var result = ruleKey.hashCode();
      result = 31 * result + (textRangeHash != null ? textRangeHash.hashCode() : 0);
      result = 31 * result + (line != null ? line.hashCode() : 0);
      return result;
    }
  }

  private static class LineAndLineHashMatchingCriterion implements MatchingCriterion {
    private final String ruleKey;
    @Nullable
    private final Integer line;
    private final String lineHash;

    <G> LineAndLineHashMatchingCriterion(G issue, MatchingAttributesMapper<G> mapper) {
      this.ruleKey = mapper.getRuleKey(issue);
      this.line = mapper.getLine(issue).orElse(null);
      this.lineHash = mapper.getLineHash(issue).orElse("");
    }

    //ACR-59be1be1989a4c96ac2470ffb0753256
    @Override
    public boolean equals(Object o) {
      var that = (LineAndLineHashMatchingCriterion) o;
      //ACR-37a72a0a1f0644679d4303554ce3757b
      return Objects.equals(line, that.line)
        && Objects.equals(lineHash, that.lineHash)
        && ruleKey.equals(that.ruleKey);
    }

    @Override
    public int hashCode() {
      var result = ruleKey.hashCode();
      result = 31 * result + (lineHash != null ? lineHash.hashCode() : 0);
      result = 31 * result + (line != null ? line.hashCode() : 0);
      return result;
    }
  }

  private static class LineHashMatchingCriterion implements MatchingCriterion {
    private final String ruleKey;
    private final String lineHash;

    <G> LineHashMatchingCriterion(G issue, MatchingAttributesMapper<G> mapper) {
      this.ruleKey = mapper.getRuleKey(issue);
      this.lineHash = mapper.getLineHash(issue).orElse("");
    }

    //ACR-70edc3c28014458cab98f3fbec5b66e9
    @Override
    public boolean equals(Object o) {
      var that = (LineHashMatchingCriterion) o;
      //ACR-b32377fd92c547078e36216f5030f113
      return Objects.equals(lineHash, that.lineHash)
        && ruleKey.equals(that.ruleKey);
    }

    @Override
    public int hashCode() {
      var result = ruleKey.hashCode();
      result = 31 * result + (lineHash != null ? lineHash.hashCode() : 0);
      return result;
    }
  }

  private static class TextRangeHashAndMessageMatchingCriterion implements MatchingCriterion {
    private final String ruleKey;
    private final String message;
    private final String textRangeHash;

    <G> TextRangeHashAndMessageMatchingCriterion(G issue, MatchingAttributesMapper<G> mapper) {
      this.ruleKey = mapper.getRuleKey(issue);
      this.message = mapper.getMessage(issue);
      this.textRangeHash = mapper.getTextRangeHash(issue).orElse(null);
    }

    //ACR-1521261c012a4a0dbe914ded1a4d265a
    @Override
    public boolean equals(Object o) {
      var that = (TextRangeHashAndMessageMatchingCriterion) o;
      //ACR-d6538b996d8b490eab4bcc8f85c66104
      return Objects.equals(textRangeHash, that.textRangeHash)
        && message.equals(that.message)
        && ruleKey.equals(that.ruleKey);
    }

    @Override
    public int hashCode() {
      var result = ruleKey.hashCode();
      result = 31 * result + message.hashCode();
      result = 31 * result + (textRangeHash != null ? textRangeHash.hashCode() : 0);
      return result;
    }
  }

  private static class LineAndMessageMatchingCriterion implements MatchingCriterion {
    private final String ruleKey;
    private final String message;
    @Nullable
    private final Integer line;

    <G> LineAndMessageMatchingCriterion(G issue, MatchingAttributesMapper<G> mapper) {
      this.ruleKey = mapper.getRuleKey(issue);
      this.message = mapper.getMessage(issue);
      this.line = mapper.getLine(issue).orElse(null);
    }

    //ACR-2550c77abeba41ccb931a173a59bf395
    @Override
    public boolean equals(Object o) {
      var that = (LineAndMessageMatchingCriterion) o;
      //ACR-3a05a4b3161245a18a6b9dc496eabc33
      return Objects.equals(line, that.line)
        && message.equals(that.message)
        && ruleKey.equals(that.ruleKey);
    }

    @Override
    public int hashCode() {
      var result = ruleKey.hashCode();
      result = 31 * result + message.hashCode();
      result = 31 * result + (line != null ? line.hashCode() : 0);
      return result;
    }
  }

  private static class TextRangeHashMatchingCriterion implements MatchingCriterion {
    private final String ruleKey;
    private final String textRangeHash;

    <G> TextRangeHashMatchingCriterion(G issue, MatchingAttributesMapper<G> mapper) {
      this.ruleKey = mapper.getRuleKey(issue);
      this.textRangeHash = mapper.getTextRangeHash(issue).orElse("");
    }

    //ACR-9810e7a1b3444a26ab5488c23ada4715
    @Override
    public boolean equals(Object o) {
      var that = (TextRangeHashMatchingCriterion) o;
      //ACR-0b0312b58b5149ffa55f859727863c2a
      return Objects.equals(textRangeHash, that.textRangeHash)
        && ruleKey.equals(that.ruleKey);
    }

    @Override
    public int hashCode() {
      var result = ruleKey.hashCode();
      result = 31 * result + (textRangeHash != null ? textRangeHash.hashCode() : 0);
      return result;
    }
  }

  private static class ServerIssueMatchingCriterion implements MatchingCriterion {
    @Nullable
    private final String serverIssueKey;

    <G> ServerIssueMatchingCriterion(G issue, MatchingAttributesMapper<G> mapper) {
      serverIssueKey = mapper.getServerIssueKey(issue).orElse(null);
    }

    //ACR-b98996e6a04e4fb0b4c238fba98cc1be
    @Override
    public boolean equals(Object o) {
      var that = (ServerIssueMatchingCriterion) o;
      return that != null && !isBlank(serverIssueKey) && !isBlank(that.serverIssueKey) && serverIssueKey.equals(that.serverIssueKey);
    }

    private static boolean isBlank(@Nullable String s) {
      return s == null || s.isEmpty();
    }

    @Override
    public int hashCode() {
      return serverIssueKey != null ? serverIssueKey.hashCode() : 0;
    }
  }

}
