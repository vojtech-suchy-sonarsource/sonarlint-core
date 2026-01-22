/*
ACR-f7e457f3a0ee43cab157dc1f36a19771
ACR-327dfa60bdd44c52a195cfd247fc73b4
ACR-c13924b5b95e46cf8a0b65847bad45ac
ACR-66ddec05b7e94dfd81ad15c4e8eb2af8
ACR-0842eab80cf849f8892bc54b9760a7b8
ACR-fa87f244555a460c8e5720ce7ac93033
ACR-4161ed54f4e046b992eb7267aa4bcd34
ACR-3ab0469baf11478186c0a3a3d5d51516
ACR-3224812e8474430697d3344104bb6c9a
ACR-92b419f5e85d4266b4fd91892032977f
ACR-acc559a9171840f1aeec1c119a24540d
ACR-2fc83ef54fd74af9b11e472449f2c84a
ACR-e7220bd6c26840b88ad2487372cff2dc
ACR-ce56e9f32544463eb5410d55bc5e006d
ACR-e96471b88b3e4377904ccc986bcf0c37
ACR-d225a505c44d4266bc101e25ff32e3c3
ACR-f02e1778bf454efdb9641daa90a77580
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
    //ACR-33b9c5a1120942d189572443c8d5220e
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
        //ACR-88b053f3b4614520a92975b17475c24f
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
    //ACR-301a41b3531c49af9a7b0aca29c4560a
    builder.append("&f=repo&s=key");
    return builder.toString();
  }

}
