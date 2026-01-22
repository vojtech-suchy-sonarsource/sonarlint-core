/*
ACR-e44733f7402444c7af51854e86881896
ACR-3bc91dbbcdf0482faaca8609e3830705
ACR-22baad3d5c14415e8f18865e0813fdad
ACR-0390b55cfd0f4666b355b0fe51ced5bf
ACR-288b3030e7dc4f2a9b4206718aab923e
ACR-04eebcbe84364f7faaf0af2220964a43
ACR-2cd8686fa7764c55a75dc0329e6faf2f
ACR-6e17782dd3ef46febdf85f5b1651a70f
ACR-e0c9f9fc630d46e5a7cab97ded1a1e86
ACR-e80bc8f6e5a742b2878040e09e18973f
ACR-81f00750824e44a2bdef4ecc17575b46
ACR-fe6730205aea43e4b8bd599fd628e75d
ACR-0ca42b7dcb0b4a328d889aec8d2a70ea
ACR-378c81a2d8d544a7b442d96cb130f214
ACR-b06424fd32614c7e8cc9eb4743e79bd6
ACR-d072d1ae83e04179b879f1522b06d016
ACR-659ec7ce0fd84ea9a350d28ab7984293
 */
package org.sonarsource.sonarlint.core.plugin.commons.sonarapi;

import java.util.Optional;
import org.sonar.api.config.Configuration;
import org.sonar.api.config.Settings;

/*ACR-0c231dc9b1af413aa8ac8944804f2f05
ACR-1f9fa09ad5e24c158435610a9c735412
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
