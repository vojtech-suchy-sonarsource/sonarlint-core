/*
ACR-7c56153cd0074c9395632b911802f7ea
ACR-cf4275dd14b046538654d4268c399da8
ACR-5c3f135a7c0b4aa8a46b6f658efaef77
ACR-7a692b741b544908a337de250ba62494
ACR-74eefd316693451b949142403d19828a
ACR-eede2de146b14eda8ea5d05a0e2fecfd
ACR-4dda77d6110841348491f847210e180b
ACR-7ed63877dd3d4cf9a28a0a5ce973ecca
ACR-7541d2b5cee344ea864b938aeade1f8e
ACR-873a668fa0454afea4c0e291e7a1d1c2
ACR-f0d3cfc63046439bbf79ae37b23dc691
ACR-ab08eab70277409fa09115f61f80c167
ACR-b39afcfc21aa4153ae7681b87bef487b
ACR-941215d1aab546f2901eeb74011b8d6c
ACR-32ee9c4ae2574d0b865ed5760232a582
ACR-cea9025bc6de4c66b71207d2ffcc5803
ACR-b676d6704cb84c4eba182658a5454fb2
 */
package org.sonarsource.sonarlint.core.plugin.commons.loading;

import java.util.Optional;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.plugin.commons.api.SkipReason;

public class PluginRequirementsCheckResult {

  private final PluginInfo plugin;

  @CheckForNull
  private final SkipReason skipReason;

  public PluginRequirementsCheckResult(PluginInfo plugin, @Nullable SkipReason skipReason) {
    this.plugin = plugin;
    this.skipReason = skipReason;
  }

  public PluginInfo getPlugin() {
    return plugin;
  }

  public Optional<SkipReason> getSkipReason() {
    return Optional.ofNullable(skipReason);
  }

  public boolean isSkipped() {
    return skipReason != null;
  }

}
