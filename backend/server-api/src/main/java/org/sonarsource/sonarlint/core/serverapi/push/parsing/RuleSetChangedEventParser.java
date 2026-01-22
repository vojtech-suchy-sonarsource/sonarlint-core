/*
ACR-33ca3c091b6d41c6aa2e02f693524e25
ACR-667042b2d6e342e2ba1b8de288fd86f3
ACR-f8f4de454c0346778d42302487ec38e7
ACR-62b815e86c8245b39f5832c7c4b6fe24
ACR-9229d21b6be440d5b87813543e5f30bd
ACR-aa64e1f57be043d2b6d683e65b53974f
ACR-f5257bb49c7243a3b4132bd886857ea6
ACR-eb762d02842b46a281ddb6fcc4861de2
ACR-7b2607306358494caf043792ab97899d
ACR-a9e84680380f4477b307b8570854f475
ACR-717da525200b4d0ba88c93347554b39a
ACR-f91481abace44297af99ef9a681d7b73
ACR-372249a210e84821beea73761c8582f0
ACR-b385470301444124969485a8aa45905e
ACR-1fb0f823b9174c6db078c2763b777a59
ACR-7a65c48a9bd1447ca681aa103b26f6d6
ACR-0a0ed818ea39446a86961dccd1f41c9d
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
