/*
ACR-6804b63921e943c7a3470b411bd53a49
ACR-2cb9637220f24e42837f61ffa7d924f1
ACR-3ec46aea5a74466fb1cd85262ba05e89
ACR-afa20a9ea7df4f4bba8ed5e20f43c990
ACR-ad35c7f9e8804f3bbc5cb1cfa62e0669
ACR-63e6d926532f40ba8e3176390fd817d5
ACR-f53dd00d7726415d9b8c3d74c2f9eea6
ACR-c0faa4e055384fed8d8be376d0c7a560
ACR-780ee0e61457429fa2e3b708c7c2a1f5
ACR-faa6a6f220e54c2da72c6c9d9291b154
ACR-17c2a77d6c034b5fb7f587388d71b217
ACR-4024d811030a444db3701199dd4b834a
ACR-363d7d1a5e5c4e0c85f35bdb00fa05fb
ACR-adc747e3fa99415f810d777a5585dcb4
ACR-2e2287d6a8d84f3b83f3d0ea190b4d61
ACR-8af6f09e3726445795f3abf15fce11a2
ACR-31011986fde44c6f9d2116f8d3192fc8
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

/*ACR-7e40803eeebf476f80b7b71cd9d6bb55
ACR-5d64f0b3f62e4be397adb50347192e19
 */
public class LegacyHotspotRuleDescriptionSectionsGenerator {

  private LegacyHotspotRuleDescriptionSectionsGenerator() {
    //ACR-ac451c68e4c94aa5972de152055d7609
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
