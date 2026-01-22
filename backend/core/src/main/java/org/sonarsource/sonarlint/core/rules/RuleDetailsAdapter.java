/*
ACR-d3023fc3382745e4a5bc0311fcec22c9
ACR-c5ee83108891402780e0b23805cc0913
ACR-8031b4227f4c4c478b583d7a1098ab23
ACR-a44b0978cde643c58b48f52e28e13282
ACR-ea7bebb91d304634814b9c58822a1b88
ACR-4ccb140bf57a4a85892c2d3715caea18
ACR-d7949bcad0fd4498aef29d1dee1af16c
ACR-aca0458d40fc42a6a9979eb765a9e2df
ACR-a6b7c916e09944adb3dec9304a011d80
ACR-dddd987d812049fa9661e453e2b04c51
ACR-27d7a7da5c184565af7fa458c5731513
ACR-cf96abf9d7364fa09709743ecf403f3a
ACR-b932bb059db448f6aeeb31c43eae8492
ACR-6c3ca4540beb49b0a1db23028e6e5f2c
ACR-8a5e1b65302447148e0d273838ed26a3
ACR-a7670a1a00c34f61a87ca6009b2fc967
ACR-f5ec16278cb6484f9d1a4499c7bdf672
 */
package org.sonarsource.sonarlint.core.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.sonarsource.sonarlint.core.analysis.api.ClientInputFileEdit;
import org.sonarsource.sonarlint.core.analysis.api.Flow;
import org.sonarsource.sonarlint.core.analysis.api.QuickFix;
import org.sonarsource.sonarlint.core.analysis.api.TextEdit;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.EffectiveRuleDetailsDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.EffectiveRuleParamDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.ImpactDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.RuleContextualSectionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.RuleContextualSectionWithDefaultContextKeyDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.RuleDescriptionTabDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.RuleMonolithicDescriptionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.RuleNonContextualSectionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.RuleSplitDescriptionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.rules.VulnerabilityProbability;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.FileEditDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.IssueFlowDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.IssueLocationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.QuickFixDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.TextEditDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.CleanCodeAttribute;
import org.sonarsource.sonarlint.core.rpc.protocol.common.CleanCodeAttributeCategory;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ImpactSeverity;
import org.sonarsource.sonarlint.core.rpc.protocol.common.IssueSeverity;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;
import org.sonarsource.sonarlint.core.rpc.protocol.common.MQRModeDetails;
import org.sonarsource.sonarlint.core.rpc.protocol.common.SoftwareQuality;
import org.sonarsource.sonarlint.core.rpc.protocol.common.StandardModeDetails;

import static org.sonarsource.sonarlint.core.tracking.TextRangeUtils.toTextRangeDto;

public class RuleDetailsAdapter {
  public static final String INTRODUCTION_SECTION_KEY = "introduction";
  public static final String ROOT_CAUSE_SECTION_KEY = "root_cause";
  public static final String ASSESS_THE_PROBLEM_SECTION_KEY = "assess_the_problem";
  public static final String HOW_TO_FIX_SECTION_KEY = "how_to_fix";
  public static final String RESOURCES_SECTION_KEY = "resources";
  private static final String DEFAULT_CONTEXT_KEY = "others";
  private static final String DEFAULT_CONTEXT_DISPLAY_NAME = "Others";
  private static final List<String> SECTION_KEYS_ORDERED = List.of(ROOT_CAUSE_SECTION_KEY, ASSESS_THE_PROBLEM_SECTION_KEY,
    HOW_TO_FIX_SECTION_KEY, RESOURCES_SECTION_KEY);

  private RuleDetailsAdapter() {
    //ACR-3063e6267575407f9505ff49dd26bbb7
  }

  public static EffectiveRuleDetailsDto transform(RuleDetails ruleDetails, @Nullable String contextKey) {
    var cleanCodeAttribute = ruleDetails.getCleanCodeAttribute().map(RuleDetailsAdapter::adapt).orElse(null);
    Either<StandardModeDetails, MQRModeDetails> severityDetails = cleanCodeAttribute != null && !ruleDetails.getImpacts().isEmpty() ?
      Either.forRight(new MQRModeDetails(cleanCodeAttribute, toDto(ruleDetails.getImpacts()))) :
      Either.forLeft(new StandardModeDetails(adapt(Objects.requireNonNull(ruleDetails.getDefaultSeverity())),
        adapt(Objects.requireNonNull(ruleDetails.getType()))));
    return new EffectiveRuleDetailsDto(
      ruleDetails.getKey(),
      ruleDetails.getName(),
      severityDetails,
      transformDescriptions(ruleDetails, contextKey),
      transform(ruleDetails.getParams()),
      adapt(ruleDetails.getLanguage()),
      adapt(ruleDetails.getVulnerabilityProbability()));
  }

  public static Either<RuleMonolithicDescriptionDto, RuleSplitDescriptionDto> transformDescriptions(RuleDetails ruleDetails, @Nullable String contextKey) {
    if (ruleDetails.hasMonolithicDescription()) {
      return Either.forLeft(transformMonolithicDescription(ruleDetails));
    }
    return Either.forRight(transformSplitDescription(ruleDetails, contextKey));
  }

  private static RuleMonolithicDescriptionDto transformMonolithicDescription(RuleDetails ruleDetails) {
    var htmlSnippets = new ArrayList<String>();
    if (!ruleDetails.getDescriptionSectionsByKey().isEmpty()) {
      //ACR-72a6608a24664355a3f2f5e2a1d6b20f
      htmlSnippets.addAll(ruleDetails.getDescriptionSectionsByKey().get("default").stream().map(RuleDetails.DescriptionSection::getHtmlContent).toList());
    } else {
      htmlSnippets.add(ruleDetails.getHtmlDescription());
    }
    htmlSnippets.add(ruleDetails.getExtendedDescription());
    htmlSnippets.add(getCleanCodePrinciplesContent(ruleDetails.getCleanCodePrincipleKeys()));
    return new RuleMonolithicDescriptionDto(concat(htmlSnippets));
  }

  private static String getCleanCodePrinciplesContent(Set<String> cleanCodePrincipleKeys) {
    var principles = cleanCodePrincipleKeys.stream().sorted(Comparator.naturalOrder()).map(CleanCodePrinciples::getContent).toList();
    return (principles.stream().anyMatch(StringUtils::isNotBlank) ? "<h3>Clean Code Principles</h3>\n" : "") + concat(principles);
  }

  private static RuleSplitDescriptionDto transformSplitDescription(RuleDetails ruleDetails, @Nullable String contextKey) {
    var sectionsByKey = ruleDetails.getDescriptionSectionsByKey();

    var tabbedSections = new ArrayList<>(transformSectionsButIntroductionToTabs(ruleDetails, contextKey));
    addMoreInfoTabIfNeeded(ruleDetails, tabbedSections);
    return new RuleSplitDescriptionDto(extractIntroductionFromSections(sectionsByKey), tabbedSections);
  }

  @Nullable
  private static String extractIntroductionFromSections(Map<String, List<RuleDetails.DescriptionSection>> sectionsByKey) {
    var introductionSections = sectionsByKey.get(INTRODUCTION_SECTION_KEY);
    String introductionHtmlContent = null;
    if (introductionSections != null && !introductionSections.isEmpty()) {
      //ACR-e72543aa5dfd4c7c9aa8a1f283805573
      introductionHtmlContent = introductionSections.get(0).getHtmlContent();
    }
    return introductionHtmlContent;
  }

  private static void addMoreInfoTabIfNeeded(RuleDetails ruleDetails, ArrayList<RuleDescriptionTabDto> tabbedSections) {
    if (!ruleDetails.getDescriptionSectionsByKey().containsKey(RESOURCES_SECTION_KEY)) {
      var htmlSnippets = new ArrayList<String>();
      htmlSnippets.add(ruleDetails.getExtendedDescription());
      htmlSnippets.add(getCleanCodePrinciplesContent(ruleDetails.getCleanCodePrincipleKeys()));
      var content = concat(htmlSnippets);
      if (StringUtils.isNotBlank(content)) {
        tabbedSections
          .add(new RuleDescriptionTabDto(getTabTitle(ruleDetails, RESOURCES_SECTION_KEY), Either.forLeft(new RuleNonContextualSectionDto(content))));
      }
    }
  }

  private static Collection<RuleDescriptionTabDto> transformSectionsButIntroductionToTabs(RuleDetails ruleDetails, @Nullable String contextKey) {
    var tabbedSections = new ArrayList<RuleDescriptionTabDto>();
    var sectionsByKey = ruleDetails.getDescriptionSectionsByKey();
    SECTION_KEYS_ORDERED.forEach(sectionKey -> {
      if (sectionsByKey.containsKey(sectionKey)) {
        var tabContents = sectionsByKey.get(sectionKey);
        Either<RuleNonContextualSectionDto, RuleContextualSectionWithDefaultContextKeyDto> content;
        var foundMatchingContext = tabContents.stream().anyMatch(c -> c.getContext().isPresent() && c.getContext().get().getKey().equals(contextKey));
        if (tabContents.size() == 1 && tabContents.get(0).getContext().isEmpty()) {
          content = buildNonContextualSectionDto(ruleDetails, tabContents.get(0));
        } else {
          //ACR-718f96e33f5c43ee84d54e2bf8f849e9
          var contextualSectionContents = tabContents.stream().map(s -> {
            var context = s.getContext().get();
            return new RuleContextualSectionDto(getTabContent(s, ruleDetails.getExtendedDescription(), ruleDetails.getCleanCodePrincipleKeys()), context.getKey(),
              context.getDisplayName());
          })
            .sorted(Comparator.comparing(RuleContextualSectionDto::getDisplayName))
            .collect(Collectors.toCollection(ArrayList::new));
          contextualSectionContents.add(
            new RuleContextualSectionDto(OthersSectionHtmlContent.getHtmlContent(),
              DEFAULT_CONTEXT_KEY, DEFAULT_CONTEXT_DISPLAY_NAME));
          content = Either.forRight(new RuleContextualSectionWithDefaultContextKeyDto(foundMatchingContext ? contextKey : DEFAULT_CONTEXT_KEY, contextualSectionContents));
        }
        tabbedSections.add(new RuleDescriptionTabDto(getTabTitle(ruleDetails, sectionKey), content));
      }
    });
    return tabbedSections;
  }

  private static String getTabTitle(RuleDetails ruleDetails, String sectionKey) {
    switch (sectionKey) {
      case ROOT_CAUSE_SECTION_KEY:
        return RuleType.SECURITY_HOTSPOT.equals(ruleDetails.getType()) ? "What's the risk?" : "Why is this an issue?";
      case ASSESS_THE_PROBLEM_SECTION_KEY:
        return "Assess the risk";
      case HOW_TO_FIX_SECTION_KEY:
        return "How can I fix it?";
      default:
        return "More Info";
    }
  }

  private static String concat(Collection<String> htmlSnippets) {
    return htmlSnippets.stream()
      .filter(StringUtils::isNotBlank)
      .collect(Collectors.joining());
  }

  private static String getTabContent(RuleDetails.DescriptionSection section, @Nullable String extendedDescription, Set<String> educationPrincipleKeys) {
    var htmlSnippets = new ArrayList<String>();
    htmlSnippets.add(section.getHtmlContent());
    if (RESOURCES_SECTION_KEY.equals(section.getKey())) {
      htmlSnippets.add(extendedDescription);
      htmlSnippets.add(getCleanCodePrinciplesContent(educationPrincipleKeys));
    }
    return concat(htmlSnippets);
  }

  private static Collection<EffectiveRuleParamDto> transform(Collection<RuleDetails.EffectiveRuleParam> params) {
    var builder = new ArrayList<EffectiveRuleParamDto>();
    for (var param : params) {
      builder.add(new EffectiveRuleParamDto(
        param.getName(),
        param.getDescription(),
        param.getValue(),
        param.getDefaultValue()));
    }
    return builder;
  }

  @NotNull
  private static Either<RuleNonContextualSectionDto, RuleContextualSectionWithDefaultContextKeyDto> buildNonContextualSectionDto(RuleDetails ruleDetails,
    RuleDetails.DescriptionSection matchingContext) {
    return Either.forLeft(new RuleNonContextualSectionDto(getTabContent(matchingContext, ruleDetails.getExtendedDescription(), ruleDetails.getCleanCodePrincipleKeys())));
  }

  public static List<ImpactDto> toDto(Map<org.sonarsource.sonarlint.core.commons.SoftwareQuality, org.sonarsource.sonarlint.core.commons.ImpactSeverity> defaultImpacts) {
    return defaultImpacts.entrySet().stream()
      .map(e -> new ImpactDto(adapt(e.getKey()), adapt(e.getValue())))
      .toList();
  }

  public static CleanCodeAttribute adapt(org.sonarsource.sonarlint.core.commons.CleanCodeAttribute cca) {
    return CleanCodeAttribute.valueOf(cca.name());
  }

  public static CleanCodeAttributeCategory adapt(org.sonarsource.sonarlint.core.commons.CleanCodeAttributeCategory ccac) {
    return CleanCodeAttributeCategory.valueOf(ccac.name());
  }

  public static IssueSeverity adapt(org.sonarsource.sonarlint.core.commons.IssueSeverity s) {
    return IssueSeverity.valueOf(s.name());
  }

  public static org.sonarsource.sonarlint.core.rpc.protocol.common.RuleType adapt(RuleType t) {
    return org.sonarsource.sonarlint.core.rpc.protocol.common.RuleType.valueOf(t.name());
  }

  public static Language adapt(SonarLanguage l) {
    return Language.valueOf(l.name());
  }

  public static SoftwareQuality adapt(org.sonarsource.sonarlint.core.commons.SoftwareQuality sq) {
    return SoftwareQuality.valueOf(sq.name());
  }

  @CheckForNull
  public static VulnerabilityProbability adapt(@Nullable org.sonarsource.sonarlint.core.commons.VulnerabilityProbability v) {
    return v != null ? VulnerabilityProbability.valueOf(v.name()) : null;
  }

  public static ImpactSeverity adapt(org.sonarsource.sonarlint.core.commons.ImpactSeverity is) {
    return ImpactSeverity.valueOf(is.name());
  }

  public static IssueFlowDto adapt(Flow f) {
    return new IssueFlowDto(f.locations().stream().map(RuleDetailsAdapter::adapt).toList());
  }

  public static IssueLocationDto adapt(org.sonarsource.sonarlint.core.analysis.api.IssueLocation l) {
    var inputFile = l.getInputFile();
    var fileUri = inputFile != null ? inputFile.uri() : null;
    return new IssueLocationDto(toTextRangeDto(l.getTextRange()), l.getMessage(), fileUri);
  }

  public static QuickFixDto adapt(QuickFix qf) {
    List<FileEditDto> fileEditDto = qf.inputFileEdits().stream().map(RuleDetailsAdapter::adapt).toList();
    return new QuickFixDto(fileEditDto, qf.message());
  }

  private static FileEditDto adapt(ClientInputFileEdit edit) {
    return new FileEditDto(edit.target().uri(), edit.textEdits().stream().map(RuleDetailsAdapter::adapt).toList());
  }

  private static TextEditDto adapt(TextEdit textEdit) {
    return new TextEditDto(Objects.requireNonNull(toTextRangeDto(textEdit.range())), textEdit.newText());
  }
}
