/*
ACR-b8b49a05122a467c8390345eb15f736d
ACR-04423791bdd74bd7bbdd6078e412cbf7
ACR-f1a994162a6645868e19d1b8d7b2d32d
ACR-bccecc791b5c40e68e6d0081e32f04cf
ACR-b3094dbb48da4c3786121997663c8ed0
ACR-00c0323cecb542d0bf1d05afaa646a25
ACR-6e118209b0ce4f3ca71bdc137bc47ea9
ACR-594d23365d9540fcb74f31048942ad84
ACR-a86992064d5c455ab8803f4bbbee34ef
ACR-a2d3c3e6d4f54704bc3858a89af9e803
ACR-51448a6784fe45a097b996c761d3d899
ACR-56f83e4d7449459fa671194fb01e40ac
ACR-dc5d8ee3f0e34a9681aba2af64ace601
ACR-02055fa3281f4845a4febfef5dec4ad1
ACR-752d917aa7ed4da1bee73bb475548db9
ACR-02e21a4f3b204a288446147a26ac9ce1
ACR-a3d053e2aca84ccc8179102797dfbbb1
 */
package org.sonarsource.sonarlint.core.serverapi.rules;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.serverapi.push.parsing.common.ImpactPayload;

public class ServerActiveRule {
  private final String ruleKey;
  private final IssueSeverity severity;
  private final Map<String, String> params;
  private final String templateKey;
  private final List<ImpactPayload> overriddenImpacts;

  public ServerActiveRule(String ruleKey, IssueSeverity severity, Map<String, String> params, @Nullable String templateKey, List<ImpactPayload> overriddenImpacts) {
    this.ruleKey = ruleKey;
    this.severity = severity;
    this.params = params;
    this.templateKey = templateKey;
    this.overriddenImpacts = overriddenImpacts;
  }

  public List<ImpactPayload> getOverriddenImpacts() {
    return overriddenImpacts;
  }

  public IssueSeverity getSeverity() {
    return severity;
  }

  public Map<String, String> getParams() {
    return params;
  }

  public String getRuleKey() {
    return ruleKey;
  }

  public String getTemplateKey() {
    return templateKey;
  }
}
