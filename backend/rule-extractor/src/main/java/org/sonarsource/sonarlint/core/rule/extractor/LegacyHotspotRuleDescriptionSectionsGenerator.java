/*
ACR-ece40a1f53b84dd2b498e6835f46c952
ACR-23f801f43e954a1497cde8da37d2a986
ACR-1b7ba8bbc5184dbd923b14987f6d13c6
ACR-ed703ec6b0c44f69b299cc731f95e462
ACR-06c1b701a64749f78db455813ba8f7aa
ACR-0fdceecacd484815aff0e083f3bfe275
ACR-750f5c65ce014cf3b016eb959752c631
ACR-a42d5d2737f74cffb6d205f2bced799d
ACR-f550217ff32247bfb5a83636769c2256
ACR-30a8778522d540e0905a266410ccd517
ACR-c2666440c9824b47a3591e43560a642e
ACR-23e6f0934a15427b94f4c75433b5e12a
ACR-d71a6136d2e343ce8dd1bfdd0a5dd0ae
ACR-88d777302fc248718b4168915995f3f5
ACR-cce9cefaff584234bdcb6f3bb6f1589c
ACR-949c1a1bc22447a0bccf7882355994c4
ACR-0d1a8f340fd644b6a1426968b9152674
 */
package org.sonarsource.sonarlint.core.rule.extractor;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rule.extractor.SonarLintRuleDescriptionSection.Context;

import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.sonar.api.server.rule.RuleDescriptionSection.RuleDescriptionSectionKeys.ASSESS_THE_PROBLEM_SECTION_KEY;
import static org.sonar.api.server.rule.RuleDescriptionSection.RuleDescriptionSectionKeys.HOW_TO_FIX_SECTION_KEY;
import static org.sonar.api.server.rule.RuleDescriptionSection.RuleDescriptionSectionKeys.ROOT_CAUSE_SECTION_KEY;

/*ACR-c6d5a3ae3cd84f36b496acfe72eb870c
ACR-0370ecabbace4b8d8b67954777d777ea
 */
public class LegacyHotspotRuleDescriptionSectionsGenerator {

  private LegacyHotspotRuleDescriptionSectionsGenerator() {
    //ACR-479d2e41a23d46a486e6d6d840f0907f
  }

  static List<SonarLintRuleDescriptionSection> extractDescriptionSectionsFromHtml(@Nullable String descriptionInHtml) {
    if (descriptionInHtml == null || descriptionInHtml.isEmpty()) {
      return List.of();
    }
    String[] split = extractSection("", descriptionInHtml);
    String remainingText = split[0];
    String ruleDescriptionSection = split[1];

    split = extractSection("<h2>Exceptions</h2>", remainingText);
    remainingText = split[0];
    String exceptions = split[1];

    split = extractSection("<h2>Ask Yourself Whether</h2>", remainingText);
    remainingText = split[0];
    String askSection = split[1];

    split = extractSection("<h2>Sensitive Code Example</h2>", remainingText);
    remainingText = split[0];
    String sensitiveSection = split[1];

    split = extractSection("<h2>Noncompliant Code Example</h2>", remainingText);
    remainingText = split[0];
    String noncompliantSection = split[1];

    split = extractSection("<h2>Recommended Secure Coding Practices</h2>", remainingText);
    remainingText = split[0];
    String recommendedSection = split[1];

    split = extractSection("<h2>Compliant Solution</h2>", remainingText);
    remainingText = split[0];
    String compliantSection = split[1];

    split = extractSection("<h2>See</h2>", remainingText);
    remainingText = split[0];
    String seeSection = split[1];

    Optional<SonarLintRuleDescriptionSection> rootSection = createSection(ROOT_CAUSE_SECTION_KEY, ruleDescriptionSection, exceptions, remainingText);
    Optional<SonarLintRuleDescriptionSection> assessSection = createSection(ASSESS_THE_PROBLEM_SECTION_KEY, askSection, sensitiveSection, noncompliantSection);
    Optional<SonarLintRuleDescriptionSection> fixSection = createSection(HOW_TO_FIX_SECTION_KEY, recommendedSection, compliantSection, seeSection);

    return Stream.of(rootSection, assessSection, fixSection)
      .filter(Predicate.not(Optional::isEmpty))
      .flatMap(Optional::stream)
      .toList();
  }

  private static String[] extractSection(String beginning, String description) {
    var endSection = "<h2>";
    var beginningIndex = description.indexOf(beginning);
    if (beginningIndex != -1) {
      var endIndex = description.indexOf(endSection, beginningIndex + beginning.length());
      if (endIndex == -1) {
        endIndex = description.length();
      }
      return new String[] {
        description.substring(0, beginningIndex) + description.substring(endIndex),
        description.substring(beginningIndex, endIndex)
      };
    } else {
      return new String[] {description, ""};
    }
  }

  private static Optional<SonarLintRuleDescriptionSection> createSection(String sectionKey, String... contentPieces) {
    var content = trimToNull(String.join("", contentPieces));
    if (content == null) {
      return Optional.empty();
    }
    return Optional.of(new SonarLintRuleDescriptionSection(sectionKey, content, emptyContextForConvertedHotspotSection()));
  }

  private static Optional<Context> emptyContextForConvertedHotspotSection() {
    return Optional.empty();
  }
}
