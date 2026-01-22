/*
ACR-d8df829467a247208dd77c5b22ea6a15
ACR-654df363c3334c9e9c7c25b9c98db4a7
ACR-4096477d2a564216a859f388a610933c
ACR-bf809f1950b14035823addcacccfd50a
ACR-cd32b5318a424b6bae389b73209700b4
ACR-08ab35856fb74ab49d6b84f89bda05fb
ACR-86d27922f4724bacaaefaaa830ee4454
ACR-73c342fed51648b3884255f8a77df2c2
ACR-6d0c7fb0923b4cd1b2873cd3e50fba63
ACR-1557a28cd02444b7a3de8d3eb5f1e661
ACR-7ff26b2701264651a70edbf25b18fcb5
ACR-34a22e120a7b46d48fe9a54e98fd8ada
ACR-34d1eb08d8ce409cb727f7aa86eda5c2
ACR-9925dbbb9f704203afaa1e5819814b29
ACR-085c8b9554694a57ae4630a39504696a
ACR-4d41163e46fd48af9b6b89ee3f9eb6e8
ACR-db3cb367473f430c9d7950b34e9ad068
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
