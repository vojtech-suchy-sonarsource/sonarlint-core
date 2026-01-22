/*
ACR-65be40308da7498e8530b63f68338796
ACR-9057efd708154b1c9bd5370ab1d98ee6
ACR-e01074226f614fb188be631a8e844651
ACR-18de41334bcc4a798684d769eaa74c5d
ACR-19539361622c46e1b405c727d9c62ca3
ACR-df01f56ca6ad49db9f443f2a05fae32f
ACR-1f3f00836010448fb8748928722e432c
ACR-29f5ae7680d1433db8140dc9d6f39202
ACR-fe15b45524224e8e998c52dddea043b8
ACR-38f6ea24b2e143258e85bee11762d260
ACR-ff1417c7fc634e87a8f722431246b3c4
ACR-cd9e84f39a014669a16a3674e310d570
ACR-37c3a2489973483883fccccecb9bce57
ACR-3fc4e7f0998a42bd88b5af43011bd007
ACR-95d7fe3b202a469190fc239e7e6fd3eb
ACR-f2e2a3c998434642b3a7fb4a12514447
ACR-f379ad1952dd4ba299bbd28f3d108934
 */
package org.sonarsource.sonarlint.core.serverapi.rules;

import com.google.common.base.Enums;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.sonarsource.sonarlint.core.commons.CleanCodeAttribute;
import org.sonarsource.sonarlint.core.commons.ImpactSeverity;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.SoftwareQuality;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;
import org.sonarsource.sonarlint.core.serverapi.UrlUtils;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Rules;
import org.sonarsource.sonarlint.core.serverapi.push.parsing.common.ImpactPayload;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

public class RulesApi {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  public static final Map<SonarLanguage, String> TAINT_REPOS_BY_LANGUAGE = Map.of(
    SonarLanguage.GO, "gosecurity",
    SonarLanguage.JAVA, "javasecurity",
    SonarLanguage.JS, "jssecurity",
    SonarLanguage.KOTLIN, "kotlinsecurity",
    SonarLanguage.PHP, "phpsecurity",
    SonarLanguage.PYTHON, "pythonsecurity",
    SonarLanguage.CS, "roslyn.sonaranalyzer.security.cs",
    SonarLanguage.TS, "tssecurity",
    SonarLanguage.VBNET, "vbnetsecurity");

  public static final Set<String> TAINT_REPOS = Set.copyOf(TAINT_REPOS_BY_LANGUAGE.values());

  public static final String RULE_SHOW_URL = "/api/rules/show.protobuf?key=";

  private final ServerApiHelper serverApiHelper;

  public RulesApi(ServerApiHelper serverApiHelper) {
    this.serverApiHelper = serverApiHelper;
  }

  public Optional<ServerRule> getRule(String ruleKey, SonarLintCancelMonitor cancelMonitor) {
    var builder = new StringBuilder(RULE_SHOW_URL + ruleKey);
    serverApiHelper.getOrganizationKey().ifPresent(org -> builder.append("&organization=").append(UrlUtils.urlEncode(org)));
    try (var response = serverApiHelper.get(builder.toString(), cancelMonitor)) {
      var rule = Rules.ShowResponse.parseFrom(response.bodyAsStream()).getRule();
      var cleanCodeAttribute = Enums.getIfPresent(CleanCodeAttribute.class, rule.getCleanCodeAttribute().name()).orNull();
      var impacts = rule.getImpacts().getImpactsList().stream().collect(toMap(
        impact -> SoftwareQuality.valueOf(impact.getSoftwareQuality().name()),
        impact -> ImpactSeverity.mapSeverity(impact.getSeverity().name())));
      return Optional.of(new ServerRule(rule.getName(), IssueSeverity.valueOf(rule.getSeverity()), RuleType.valueOf(rule.getType().name()), rule.getLang(), rule.getHtmlDesc(),
        convertDescriptionSections(rule),
        rule.getHtmlNote(), Set.copyOf(rule.getEducationPrinciples().getEducationPrinciplesList()), cleanCodeAttribute, impacts));
    } catch (Exception e) {
      LOG.error("Error when fetching rule '" + ruleKey + "'", e);
    }
    return Optional.empty();
  }

  private static List<ServerRule.DescriptionSection> convertDescriptionSections(Rules.Rule rule) {
    if (rule.hasDescriptionSections()) {
      return rule.getDescriptionSections().getDescriptionSectionsList().stream()
        .map(s -> {
          ServerRule.DescriptionSection.Context context = null;
          if (s.hasContext()) {
            var contextFromServer = s.getContext();
            context = new ServerRule.DescriptionSection.Context(contextFromServer.getKey(), contextFromServer.getDisplayName());
          }
          return new ServerRule.DescriptionSection(s.getKey(), s.getContent(), Optional.ofNullable(context));
        }).toList();
    }
    return Collections.emptyList();
  }

  public Collection<ServerActiveRule> getAllActiveRules(String qualityProfileKey, SonarLintCancelMonitor cancelMonitor) {
    //ACR-86699933115f495e9a94b9c829362017
    Map<String, ServerActiveRule> activeRulesByKey = new HashMap<>();
    Map<String, String> ruleTemplatesByRuleKey = new HashMap<>();
    serverApiHelper.getPaginated(getSearchByQualityProfileUrl(qualityProfileKey),
      Rules.SearchResponse::parseFrom,
      r -> r.hasPaging() ? r.getPaging().getTotal() : r.getTotal(),
      r -> {
        ruleTemplatesByRuleKey.putAll(r.getRulesList().stream().collect(toMap(Rules.Rule::getKey, Rules.Rule::getTemplateKey)));
        return List.copyOf(r.getActives().getActivesMap().entrySet());
      },
      activeEntry -> {
        var ruleKey = activeEntry.getKey();
        //ACR-34a3b9cdab7442758f3d7b195f369945
        Rules.Active ar = activeEntry.getValue().getActiveListList().get(0);
        activeRulesByKey.put(ruleKey, new ServerActiveRule(
          ruleKey,
          IssueSeverity.valueOf(ar.getSeverity()),
          ar.getParamsList().stream().collect(toMap(Rules.Active.Param::getKey, Rules.Active.Param::getValue)),
          ruleTemplatesByRuleKey.get(ruleKey),
          ar.getImpacts().getImpactsList().stream()
            .map(impact -> new ImpactPayload(impact.getSoftwareQuality().toString(), ImpactSeverity.mapSeverity(impact.getSeverity().name()).name()))
            .toList()));

      },
      false,
      cancelMonitor);
    return activeRulesByKey.values();
  }

  private String getSearchByQualityProfileUrl(String qualityProfileKey) {
    var builder = new StringBuilder();
    builder.append("/api/rules/search.protobuf?qprofile=");
    builder.append(UrlUtils.urlEncode(qualityProfileKey));
    serverApiHelper.getOrganizationKey().ifPresent(org -> builder.append("&organization=").append(UrlUtils.urlEncode(org)));
    builder.append("&activation=true&f=templateKey,actives&types=CODE_SMELL,BUG,VULNERABILITY,SECURITY_HOTSPOT&s=key");
    return builder.toString();
  }

  public Set<String> getAllTaintRules(List<SonarLanguage> enabledLanguages, SonarLintCancelMonitor cancelMonitor) {
    Set<String> taintRules = new HashSet<>();
    serverApiHelper.getPaginated(getSearchByRepoUrl(enabledLanguages.stream().map(TAINT_REPOS_BY_LANGUAGE::get).filter(Objects::nonNull).toList()),
      Rules.SearchResponse::parseFrom,
      Rules.SearchResponse::getTotal,
      Rules.SearchResponse::getRulesList,
      rule -> taintRules.add(rule.getKey()),
      false,
      cancelMonitor);
    return taintRules;
  }

  private String getSearchByRepoUrl(List<String> repositories) {
    var builder = new StringBuilder();
    builder.append("/api/rules/search.protobuf?repositories=");
    builder.append(repositories.stream().map(UrlUtils::urlEncode).collect(joining(",")));
    serverApiHelper.getOrganizationKey().ifPresent(org -> builder.append("&organization=").append(UrlUtils.urlEncode(org)));
    //ACR-59f1180d80534d0790a612022bcd6c76
    builder.append("&f=repo&s=key");
    return builder.toString();
  }

}
