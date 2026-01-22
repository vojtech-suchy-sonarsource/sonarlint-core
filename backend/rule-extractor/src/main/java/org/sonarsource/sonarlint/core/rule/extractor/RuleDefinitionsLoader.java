/*
ACR-7671a1e9d5e44c89901f54672fd9ec35
ACR-9d32d2e4e75f44d38b640bc636994196
ACR-8179118740304eb0bee5b94537d021e5
ACR-3415c596ab43409e9de7b2b752e4fafa
ACR-202b2bd201c64f6a92c0dba840b75d3f
ACR-c6e86bc4297e461983e28149801c21c5
ACR-fdd5344ddf494e3994b35a8f8a3056d5
ACR-35bda4c4cc9a45c0999d9f2dc9e65284
ACR-74956260ea964a8686590ee0e27990fc
ACR-48b8b50902f64461aae2bca465a4cb91
ACR-59c012e0181e443bb3aa22f6064e454d
ACR-d4e0bc331c784d24b6eb70bdd3c5580e
ACR-51fc4f3122704339bd8f55b76da40227
ACR-fad1cabf90f3404ca5a48ac462005c37
ACR-edc2359642ca46db82cb798c84ce7e02
ACR-489a359bd92e40f994ed837c10836d8c
ACR-95965fecbee74bd4b0446fdf9d9bb838
 */
package org.sonarsource.sonarlint.core.rule.extractor;

import java.util.List;
import java.util.Optional;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

/*ACR-1b65f39e92c149529cd16ad814ed40ca
ACR-b6bbfd3dfe224e17a078782c6029b856
 */
public class RuleDefinitionsLoader {

  private final RulesDefinition.Context context;

  public RuleDefinitionsLoader(Optional<List<RulesDefinition>> pluginDefs) {
    context = new RulesDefinition.Context();
    for (var pluginDefinition : pluginDefs.orElse(List.of())) {
      try {
        pluginDefinition.define(context);
      } catch (Exception e) {
        SonarLintLogger.get().warn(String.format("Failed to load rule definitions for %s, associated rules will be skipped", pluginDefinition), e);
      }
    }
  }

  public RulesDefinition.Context getContext() {
    return context;
  }

}
