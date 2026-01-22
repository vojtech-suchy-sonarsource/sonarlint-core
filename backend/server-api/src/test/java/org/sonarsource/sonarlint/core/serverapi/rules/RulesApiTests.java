/*
ACR-b7a12daa02294093af5c43f4f923b3dc
ACR-54656a7061354e459179b7e56287eb9a
ACR-cc343c6cb46c4126aaf59137eb39a8f6
ACR-416a2e9acc7f4329bf996acd032aff29
ACR-4023c993a639474480f46d5529e2f7b9
ACR-1c3a36e7a50a41efa75e3b3c0d0a5826
ACR-c97b6708692a4c1ba6f9d4f79fd95324
ACR-3b7127c919064e21b8243b1041afbd69
ACR-ccf3b5276ce0441b8b9c7689215bee24
ACR-f665e2520b9e4f6fafe8902e77418c99
ACR-fa114af643ce40f4bacc8a3fcae00068
ACR-4bad10ec58c9411bb7bb4c079456bdcb
ACR-5d1c5c6ea27c4833ba6e0bddcdabbeae
ACR-f7a298bc683c48ff99d05b7e1333d059
ACR-3795e7b51580483cb0b5b1270b8298c9
ACR-0c1d04ffdbd242eba8387f2ff23ef573
ACR-a90580d246da436a86496b81e45c8aa3
 */
package org.sonarsource.sonarlint.core.serverapi.rules;

import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.CleanCodeAttribute;
import org.sonarsource.sonarlint.core.commons.ImpactSeverity;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.SoftwareQuality;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.MockWebServerExtensionWithProtobuf;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Common;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class RulesApiTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  @RegisterExtension
  static MockWebServerExtensionWithProtobuf mockServer = new MockWebServerExtensionWithProtobuf();

  @Test
  void logErrorParsingRuleDescription() {
    mockServer.addStringResponse("/api/rules/show.protobuf?key=java:S1234", "trash");

    var rulesApi = new RulesApi(mockServer.serverApiHelper());

    assertThat(rulesApi.getRule("java:S1234", new SonarLintCancelMonitor())).isEmpty();

    assertThat(logTester.logs()).contains("Error when fetching rule 'java:S1234'");
  }

  @Test
  void should_get_rule() {
    mockServer.addProtobufResponse("/api/rules/show.protobuf?key=java:S1234",
      Rules.ShowResponse.newBuilder().setRule(
          Rules.Rule.newBuilder()
            .setName("name")
            .setSeverity("MINOR")
            .setType(Common.RuleType.VULNERABILITY)
            .setLang(SonarLanguage.PYTHON.getSonarLanguageKey())
            .setHtmlNote("htmlNote")
            .setDescriptionSections(Rules.Rule.DescriptionSections.newBuilder()
              .addDescriptionSections(Rules.Rule.DescriptionSection.newBuilder()
                .setKey("default")
                .setContent("desc")
                .build())
              .build())
            .setCleanCodeAttribute(Common.CleanCodeAttribute.COMPLETE)
            .setImpacts(Rules.Rule.Impacts.newBuilder().addImpacts(Common.Impact.newBuilder().setSeverity(Common.ImpactSeverity.HIGH).setSoftwareQuality(Common.SoftwareQuality.MAINTAINABILITY).build()).build())
            .build())
        .build());

    var rulesApi = new RulesApi(mockServer.serverApiHelper());

    var rule = rulesApi.getRule("java:S1234", new SonarLintCancelMonitor()).get();

    assertThat(rule).extracting("name", "severity", "type", "language", "htmlNote", "cleanCodeAttribute", "impacts")
      .contains("name", IssueSeverity.MINOR, RuleType.VULNERABILITY, SonarLanguage.PYTHON, "htmlNote", CleanCodeAttribute.COMPLETE, Map.of(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.HIGH));
    assertThat(rule.getDescriptionSections().get(0).getHtmlContent()).isEqualTo("desc");
  }

  @Test
  void should_get_rule_with_description_sections() {
    mockServer.addProtobufResponse("/api/rules/show.protobuf?key=java:S1234",
      Rules.ShowResponse.newBuilder().setRule(
          Rules.Rule.newBuilder()
            .setName("name")
            .setSeverity("MINOR")
            .setType(Common.RuleType.VULNERABILITY)
            .setLang(SonarLanguage.PYTHON.getSonarLanguageKey())
            .setDescriptionSections(Rules.Rule.DescriptionSections.newBuilder()
              .addDescriptionSections(Rules.Rule.DescriptionSection.newBuilder().setKey("sectionKey").setContent("htmlContent").build())
              .addDescriptionSections(
                Rules.Rule.DescriptionSection.newBuilder().setKey("sectionKey2").setContent("htmlContent2").setContext(Rules.Rule.DescriptionSection.Context.newBuilder()
                  .setKey("contextKey").setDisplayName("displayName").build()).build())
              .build())
            .setHtmlNote("htmlNote")
            .setCleanCodeAttribute(Common.CleanCodeAttribute.CONVENTIONAL)
            .setImpacts(Rules.Rule.Impacts.newBuilder().addImpacts(Common.Impact.newBuilder().setSeverity(Common.ImpactSeverity.LOW).setSoftwareQuality(Common.SoftwareQuality.RELIABILITY).build()).build())
            .build())
        .build());

    var rulesApi = new RulesApi(mockServer.serverApiHelper());

    var rule = rulesApi.getRule("java:S1234", new SonarLintCancelMonitor()).get();

    assertThat(rule).extracting("name", "severity", "type", "language","htmlNote", "cleanCodeAttribute", "impacts")
      .contains("name", IssueSeverity.MINOR, RuleType.VULNERABILITY, SonarLanguage.PYTHON, "htmlNote", CleanCodeAttribute.CONVENTIONAL, Map.of(SoftwareQuality.RELIABILITY, ImpactSeverity.LOW));

    var sections = rule.getDescriptionSections();
    assertThat(sections).hasSize(2);
    assertThat(sections.get(0)).extracting("key", "htmlContent", "context")
      .containsExactly("sectionKey", "htmlContent", Optional.empty());
    assertThat(sections.get(1)).extracting("key", "htmlContent")
      .containsExactly("sectionKey2", "htmlContent2");
    assertThat(sections.get(1).getContext()).hasValueSatisfying(context -> {
      assertThat(context.getKey()).isEqualTo("contextKey");
      assertThat(context.getDisplayName()).isEqualTo("displayName");
    });

  }

  @Test
  void should_get_rule_from_organization() {
    mockServer.addProtobufResponse("/api/rules/show.protobuf?key=java:S1234&organization=orgKey",
      Rules.ShowResponse.newBuilder().setRule(
          Rules.Rule.newBuilder()
            .setName("name")
            .setSeverity("MAJOR")
            .setType(Common.RuleType.VULNERABILITY)
            .setLang(SonarLanguage.PYTHON.getSonarLanguageKey())
            .setHtmlNote("htmlNote")
            .setDescriptionSections(Rules.Rule.DescriptionSections.newBuilder()
              .addDescriptionSections(Rules.Rule.DescriptionSection.newBuilder()
                .setKey("default")
                .setContent("desc")
                  .build())
              .build())
            .build())
        .build());

    var rulesApi = new RulesApi(mockServer.serverApiHelper("orgKey"));

    var rule = rulesApi.getRule("java:S1234", new SonarLintCancelMonitor()).get();

    assertThat(rule).extracting("name", "severity", "type", "language", "htmlNote")
      .contains("name", IssueSeverity.MAJOR, RuleType.VULNERABILITY, SonarLanguage.PYTHON, "htmlNote");
    assertThat(rule.getDescriptionSections().get(0).getHtmlContent()).isEqualTo("desc");
  }

  @Test
  void should_get_active_rules_of_a_given_quality_profile() {
    mockServer.addProtobufResponse(
      "/api/rules/search.protobuf?qprofile=QPKEY%2B&organization=orgKey&activation=true&f=templateKey,actives&types=CODE_SMELL,BUG,VULNERABILITY,SECURITY_HOTSPOT&s=key&ps=500&p=1",
      Rules.SearchResponse.newBuilder()
        .setPaging(Common.Paging.newBuilder().setTotal(2).build())
        .addRules(Rules.Rule.newBuilder().setKey("repo:key_with_template").setTemplateKey("template").build())
        .addRules(Rules.Rule.newBuilder().setKey("repo:key").build())
        .setActives(
          Rules.Actives.newBuilder()
            .putActives("repo:key_with_template", Rules.ActiveList.newBuilder().addActiveList(
                Rules.Active.newBuilder()
                  .setSeverity("MAJOR")
                  .addParams(Rules.Active.Param.newBuilder().setKey("paramKey").setValue("paramValue").build())
                  .build())
              .build())
            .putActives("repo:key", Rules.ActiveList.newBuilder().addActiveList(
                Rules.Active.newBuilder()
                  .setSeverity("MINOR")
                  .build())
              .build())
            .build())
        .build());

    var rulesApi = new RulesApi(mockServer.serverApiHelper("orgKey"));

    var activeRules = rulesApi.getAllActiveRules("QPKEY+", new SonarLintCancelMonitor());

    assertThat(activeRules)
      .extracting("ruleKey", "severity", "templateKey", "params")
      .containsOnly(tuple("repo:key", IssueSeverity.MINOR, "", Map.of()),
        tuple("repo:key_with_template", IssueSeverity.MAJOR, "template", Map.of("paramKey", "paramValue")));
  }

  @Test
  void should_fallback_on_deprecated_pagination_for_sonarqube_older_than_9_8() {
    mockServer.addProtobufResponse(
      "/api/rules/search.protobuf?qprofile=QPKEY%2B&organization=orgKey&activation=true&f=templateKey,actives&types=CODE_SMELL,BUG,VULNERABILITY,SECURITY_HOTSPOT&s=key&ps=500&p=1",
      Rules.SearchResponse.newBuilder()
        .setTotal(501)
        .addRules(Rules.Rule.newBuilder().setKey("repo:key1").build())
        .setActives(
          Rules.Actives.newBuilder()
            .putActives("repo:key1", Rules.ActiveList.newBuilder().addActiveList(
                Rules.Active.newBuilder()
                  .setSeverity("MINOR")
                  .build())
              .build())
            .build())
        .build());
    mockServer.addProtobufResponse(
      "/api/rules/search.protobuf?qprofile=QPKEY%2B&organization=orgKey&activation=true&f=templateKey,actives&types=CODE_SMELL,BUG,VULNERABILITY,SECURITY_HOTSPOT&s=key&ps=500&p=2",
      Rules.SearchResponse.newBuilder()
        .setTotal(501)
        .addRules(Rules.Rule.newBuilder().setKey("repo:key2").build())
        .setActives(
          Rules.Actives.newBuilder()
            .putActives("repo:key2", Rules.ActiveList.newBuilder().addActiveList(
                Rules.Active.newBuilder()
                  .setSeverity("MAJOR")
                  .build())
              .build())
            .build())
        .build());

    var rulesApi = new RulesApi(mockServer.serverApiHelper("orgKey"));

    var activeRules = rulesApi.getAllActiveRules("QPKEY+", new SonarLintCancelMonitor());

    assertThat(activeRules).extracting(ServerActiveRule::getRuleKey).containsExactlyInAnyOrder("repo:key1", "repo:key2");
  }

}
