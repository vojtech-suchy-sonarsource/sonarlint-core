/*
ACR-fe4e272d27da45d6ab595326cde6eab1
ACR-16d78ec6b66a41d2bf778daffcbdea59
ACR-2d8f742e70c74b01b3c6c92f7407663b
ACR-86ac3dfba3734664a57f6454dead52c1
ACR-f42d9b263c2e452b92b0335f25e0d085
ACR-f8fa3c4d4c2d4aae87f1407ee7535ca0
ACR-601136b7fec14d43adc05c48103ca1d1
ACR-67d1c03c0d424ae1ba3d6d4e26115c09
ACR-c75500457e0543fd96dbc96aea3b7bcf
ACR-1c7c73c9911548308d9d360eba16cd2c
ACR-c537d4046c07412a91aa1b0ca3f49dce
ACR-47120ad47b544c03b456e7d2dfa55e3e
ACR-5384aacdbd0e431fbee911481e6c2672
ACR-8d52a636dfcf4dd8ab9e7f7df64b611e
ACR-b3e306e8a9ba4b38add780d062394692
ACR-790b97813c6b471dbb9e266e62e4a45f
ACR-08c11ee1e70c4fe8b8e5c6d191ff68c5
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
