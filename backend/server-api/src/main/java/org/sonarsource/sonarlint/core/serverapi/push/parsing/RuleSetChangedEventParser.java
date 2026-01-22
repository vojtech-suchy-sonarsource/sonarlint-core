/*
ACR-7ba33d8a6aca438f983a26f1e84d81f1
ACR-2641ac7ef39a4fa9b00c3acfe6a4051d
ACR-db4347df4ad64d099c0f81d0643cec07
ACR-6186b016e1824fbfaa86eac11453acd7
ACR-6f9accf14a1f4e789740d43d6c1ca766
ACR-790e909fa072494286394d5b6d21d665
ACR-0f7a8fa2852043778ee6d43283128553
ACR-779a14d18cc2433e82554ef4095cd461
ACR-3751a7adc01a43fc96762fa4fcb63c86
ACR-d88db5a096f441abbc842f448efb66cf
ACR-b2193e12704f425a96851217b62814d8
ACR-7c531198c20946c18bc5f41b234be8d4
ACR-9fdb80bcb02549ccbf57e954fb6d9403
ACR-020f26c38a5a4a39a15de96a39a80ea8
ACR-e38ccfd5d371440089d23aaf12668cf7
ACR-f6fd99e39cc1476ebfa9eec14a64494d
ACR-e69c1cd7918c4d0bacd8d10e1481c695
 */
package org.sonarsource.sonarlint.core.serverapi.push.parsing;

import com.google.gson.Gson;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.serverapi.push.RuleSetChangedEvent;
import org.sonarsource.sonarlint.core.serverapi.push.parsing.common.ImpactPayload;

import static org.sonarsource.sonarlint.core.serverapi.util.ServerApiUtils.areBlank;
import static org.sonarsource.sonarlint.core.serverapi.util.ServerApiUtils.isBlank;

public class RuleSetChangedEventParser implements EventParser<RuleSetChangedEvent> {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final Gson gson = new Gson();

  @Override
  public Optional<RuleSetChangedEvent> parse(String jsonData) {
    var payload = gson.fromJson(jsonData, RuleSetChangedEventPayload.class);
    if (payload.isInvalid()) {
      LOG.error("Invalid payload for 'RuleSetChanged' event: {}", jsonData);
      return Optional.empty();
    }
    return Optional.of(new RuleSetChangedEvent(
      payload.projects,
      payload.activatedRules.stream().map(changedRule -> new RuleSetChangedEvent.ActiveRule(
          changedRule.key,
          changedRule.language,
          IssueSeverity.valueOf(changedRule.severity),
          changedRule.params.stream().filter(p -> p.value != null).collect(Collectors.toMap(p -> p.key, p -> p.value)),
          changedRule.templateKey,
          changedRule.impacts == null ? Collections.emptyList() : changedRule.impacts.stream()
            .map(impact -> new ImpactPayload(impact.getSoftwareQuality(), impact.getSeverity())).toList()
        ))
        .toList(),
      payload.deactivatedRules));
  }

  private static class RuleSetChangedEventPayload {
    private List<String> projects;
    private List<ActiveRulePayload> activatedRules;
    private List<String> deactivatedRules;

    private boolean isInvalid() {
      return isBlank(projects) || areBlank(activatedRules, deactivatedRules);
    }

    private static class ActiveRulePayload {
      private String key;
      private String language;
      private String severity;
      private List<RuleParameterPayload> params;
      private String templateKey;
      private List<ImpactPayload> impacts;
    }

    private static class RuleParameterPayload {
      private String key;
      private String value;
    }
  }
}
