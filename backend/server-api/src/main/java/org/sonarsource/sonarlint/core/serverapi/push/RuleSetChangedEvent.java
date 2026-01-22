/*
ACR-124c23d3d4204bd497204c74d78363fc
ACR-7c84bc3c571d44659ad5c1554f72658a
ACR-06eb9f0eae464e378c7051ac9980b760
ACR-2cfabefb2ffd43d0984357556837896c
ACR-746295d2ee9545449ed4c0358d0a9926
ACR-0953e5d2ffa64948999aaa59a1ed9353
ACR-82eec3fdccfa442d8a9ee44923e02a33
ACR-e5c947dd47984a8a88888b73e4121ec4
ACR-bc10ffa0fddf4ce49d410c2e5511b366
ACR-bae1b44226514be2b1bc3869b8ee6ed3
ACR-5518f498c5a54b25996df1785555ea5b
ACR-75ae24c2204a4951ae49a9ab1e887da7
ACR-e38089aca3544c5d8517a7314d33f1c9
ACR-8f8e978813794631b8eace876aea2488
ACR-864606afe6354890837d1f6116736be7
ACR-43268a17994f4a5eacfdb51767c75c0e
ACR-972fdfcd8a3047a5b3a8271b7a94575d
 */
package org.sonarsource.sonarlint.core.serverapi.push;

import java.util.List;
import java.util.Map;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.serverapi.push.parsing.common.ImpactPayload;

public class RuleSetChangedEvent implements SonarServerEvent {
  private final List<String> projectKeys;
  private final List<ActiveRule> activatedRules;
  private final List<String> deactivatedRules;

  public RuleSetChangedEvent(List<String> projectKeys, List<ActiveRule> activatedRules, List<String> deactivatedRules) {
    this.projectKeys = projectKeys;
    this.activatedRules = activatedRules;
    this.deactivatedRules = deactivatedRules;
  }

  public List<String> getProjectKeys() {
    return projectKeys;
  }

  public List<ActiveRule> getActivatedRules() {
    return activatedRules;
  }

  public List<String> getDeactivatedRules() {
    return deactivatedRules;
  }

  public static class ActiveRule {
    private final String key;
    private final String languageKey;
    private final IssueSeverity severity;
    private final Map<String, String> parameters;
    private final String templateKey;
    private final List<ImpactPayload> overridenImpacts;

    public ActiveRule(String key, String languageKey, IssueSeverity severity, Map<String, String> parameters,
      @Nullable String templateKey, List<ImpactPayload> overridenImpacts) {
      this.key = key;
      this.languageKey = languageKey;
      this.severity = severity;
      this.parameters = parameters;
      this.templateKey = templateKey;
      this.overridenImpacts = overridenImpacts;
    }

    public String getKey() {
      return key;
    }

    public String getLanguageKey() {
      return languageKey;
    }

    public IssueSeverity getSeverity() {
      return severity;
    }

    public Map<String, String> getParameters() {
      return parameters;
    }

    @CheckForNull
    public String getTemplateKey() {
      return templateKey;
    }

    public List<ImpactPayload> getOverriddenImpacts() {
      return overridenImpacts;
    }
  }
}
