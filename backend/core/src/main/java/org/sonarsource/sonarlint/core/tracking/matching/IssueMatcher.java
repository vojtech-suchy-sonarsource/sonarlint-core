/*
ACR-4b26ad7ba2a547409483f761bc0cb93f
ACR-0c385f4786564c529ac8d27d73d4e259
ACR-835f687a459e47a98fbca375bae7ec4a
ACR-89ed97d8301c406ea9142bf182e6a58a
ACR-2e2922d35af54ff096f7b4fa92f60996
ACR-a1b1b542bf1e464fa3efe4e77ec427af
ACR-3e720f24589e46aea794ee239a9eeb14
ACR-357e6fa1f6404400818d082644254fd3
ACR-f70a8352f44242d6ae0daf4271b03132
ACR-25669fbc047640d1bbc190d5308c37eb
ACR-5b7da0c22ec24ba5895b7f8a1f8a1554
ACR-6afec613a8d842adb7d024a4a7900112
ACR-8773fa6e792449fead8174bd0a84780b
ACR-9c2fd17a34fa4077b6c0c8c5b68d218c
ACR-49a6cc80ece640789737dda878ed71eb
ACR-120b191c1cd14c1e839d38f1e96f20f6
ACR-88a53a8f58bb45c38e63ba4a690d7a26
 */
package org.sonarsource.sonarlint.core.tracking.matching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;

/*ACR-83ac0c057d2a483b8dbcb0f9e9303fa2
ACR-5afd0bd73fe04a9592ddfb3945e42a1e
ACR-f31828ba9d1e4353bad4d55576fd8202
ACR-aa196f4ee9684533998541808f10f7a2
ACR-e3166818574f4e0f9db8b2ecaa0f79e8
 */
public class IssueMatcher<LEFT, RIGHT> {

  private static final List<MatchingCriterionFactory> MATCHING_CRITERIA = List.of(
    //ACR-ce789dd6f20d467888c6df8c2ee86b23
    ServerIssueMatchingCriterion::new,
    //ACR-81162235f3c54991a498cd6a5791624a
    LineAndTextRangeHashMatchingCriterion::new,
    //ACR-acc065c1d7ac4f4881893da60d051058
    TextRangeHashAndMessageMatchingCriterion::new,
    //ACR-309402018a9a4ec6825cbbcef654d720
    LineAndMessageMatchingCriterion::new,
    //ACR-5fa58158edc846c4a2472f9af7c24362
    //ACR-34b28fb1bcce4ae5ad8c63820f02fba1
    TextRangeHashMatchingCriterion::new,
    //ACR-4fd6beebfd9347ec861cd4a8ffe79efe
    LineAndLineHashMatchingCriterion::new,
    //ACR-0332ecbcb489470381a9bc0760898a35
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
        //ACR-61c0a8048ed148d888f2729ec396aae3
        //ACR-5d8c486c87f74781a5b1dfcae23c273a
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

    //ACR-755c765599e54a5ab07e0ab78c35454a
    @Override
    public boolean equals(Object o) {
      var that = (LineAndTextRangeHashMatchingCriterion) o;
      //ACR-aad3c8482b154dbd98401ef399091865
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

    //ACR-14cd293b1d774aaeb169e66a6a18b77f
    @Override
    public boolean equals(Object o) {
      var that = (LineAndLineHashMatchingCriterion) o;
      //ACR-9bac8322e4544959b72c2f218640b88e
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

    //ACR-db4e7f49219f4f8e8e3d9b07140e557c
    @Override
    public boolean equals(Object o) {
      var that = (LineHashMatchingCriterion) o;
      //ACR-cd9d3fa1c75f45389749d1f0aeac840d
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

    //ACR-217349992828424aa4eaff5b0b6eaece
    @Override
    public boolean equals(Object o) {
      var that = (TextRangeHashAndMessageMatchingCriterion) o;
      //ACR-c26ed2f8dca348d28e5480284e0fb672
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

    //ACR-91e0d3f5ec764f2693964f5d5929f280
    @Override
    public boolean equals(Object o) {
      var that = (LineAndMessageMatchingCriterion) o;
      //ACR-931606ed4f2b42a193965ef9f1373354
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

    //ACR-374e158c864e4ddb8548f0c55e3ca823
    @Override
    public boolean equals(Object o) {
      var that = (TextRangeHashMatchingCriterion) o;
      //ACR-b6bcde32451142c5b318ed1dcd9780fd
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

    //ACR-26b12e0ca59142f1a0702e4ad3706285
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
