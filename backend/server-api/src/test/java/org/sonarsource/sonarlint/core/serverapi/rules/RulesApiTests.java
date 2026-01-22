/*
ACR-ab51c2aa7da844009139405af7ba260b
ACR-d1805ebaa28049a0a47d8e34db8a2fb9
ACR-40a8dc9e91db428a80c4bd568de51a77
ACR-48846a3c2eae452eb09bf159a6955791
ACR-52c9ba4a348c495b9c23335aa75b3dfc
ACR-65bd9259db4d48ef9860348f0ca7bf59
ACR-41aac44e34c544018c8f19c3c2509e42
ACR-3e67c01b823947e6b9572c73d0f4aca1
ACR-f36a3022c96c4b7db0f57258b9a8c55e
ACR-bc42fd9dfc794d9d809b4876d2d13098
ACR-cd9a54d7ec5349598cb0418d6cc8d1e8
ACR-dea7b43c5eff4464b04f85c25bfc79e9
ACR-00f47723f0a2400ba70aec8aab76ab0d
ACR-09bece29e03a4b50aae7f7b3efddde7f
ACR-4d14e4d23b5c4956a2f284904757be4b
ACR-12801cfea76b4c6583e3dfa6b3a1f042
ACR-10e00741c25c4118bd4507623d4f7922
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
