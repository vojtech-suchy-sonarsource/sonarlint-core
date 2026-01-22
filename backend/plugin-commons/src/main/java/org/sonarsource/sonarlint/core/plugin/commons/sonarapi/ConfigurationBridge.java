/*
ACR-8cb0353b36a54b2286c27757aa2363b1
ACR-b7bea239d93f45c68bb64fa4bb451906
ACR-910369f79df64c5cb179301df8b6a0f3
ACR-18d777dfbdcb46bf8fac35896000bc41
ACR-b7bd0c53a8064da9bcaa32ce83c03221
ACR-d1e4c004a7e04dc7976fdddff25503e6
ACR-93ef61cd1c5a4805bdc3a98bdbbd9b9a
ACR-752c66cfd365482d921663460f0a5846
ACR-285e390162d54576992b23d9f666a84d
ACR-28e3b7eb858642b984baa74c0edb1351
ACR-af5e1c51812c412ea7212dc48f4d35d3
ACR-28e694987fb24e84a3dfa623cda1db33
ACR-7384f14cc85b4aa683de04269bf86d6a
ACR-a1c7d0ceffe7438882bd58a2937663ad
ACR-5bfc78883f654ac080c4f556a19f427c
ACR-a4b58cb602cd46429fbfbb86035ec751
ACR-0561ab08fad0480daafba9171543df82
 */
package org.sonarsource.sonarlint.core.plugin.commons.sonarapi;

import java.util.Optional;
import org.sonar.api.config.Configuration;
import org.sonar.api.config.Settings;

/*ACR-b948c497337c4313a062ef673b320643
ACR-337248cd3952408fadf1beee52ec84a6
 */
public class ConfigurationBridge implements Configuration {

  private final Settings settings;

  public ConfigurationBridge(Settings settings) {
    this.settings = settings;
  }

  @Override
  public Optional<String> get(String key) {
    return Optional.ofNullable(settings.getString(key));
  }

  @Override
  public boolean hasKey(String key) {
    return settings.hasKey(key);
  }

  @Override
  public String[] getStringArray(String key) {
    return settings.getStringArray(key);
  }

}
