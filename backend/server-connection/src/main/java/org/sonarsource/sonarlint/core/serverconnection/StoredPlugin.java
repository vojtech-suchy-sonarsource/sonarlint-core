/*
ACR-330d95ea022f4629a26fabf3fe85457c
ACR-eb441443b2a641d8bdb541218be357ca
ACR-a187e62455354d1083361408651ece61
ACR-a210f4b8b6b04b7dbc2ffc50749d4046
ACR-614da2003e7d4f419badc162e0285b16
ACR-7db5d30cb97441ad9de41f393f1d0937
ACR-8917bba7646b441091fcc61ff06ebe3f
ACR-44d8146535b2485ea210a03f23287d86
ACR-bae50ab6c28b42b1bf134a76dcc70a60
ACR-80a63f86c1294f49a2215addc2aadabf
ACR-6021eef5d2c34daca1fa288b5f6ec0e8
ACR-38c2e9695763462d995492a2c133db06
ACR-492e1b9b780e4411b24a8554b5f6ec0b
ACR-06d678b2238f44b880ecdc8f8fe78e22
ACR-fba8152795514bbf8279bcbc31bed2c1
ACR-bcc99515b1b843a3a0b984cc1148003e
ACR-34979b220e3d47c08cb397e7bcd1e722
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.nio.file.Path;
import org.sonarsource.sonarlint.core.serverapi.plugins.ServerPlugin;

public class StoredPlugin {
  private final String key;
  private final String hash;
  private final Path jarPath;

  public StoredPlugin(String key, String hash, Path jarPath) {
    this.key = key;
    this.hash = hash;
    this.jarPath = jarPath;
  }

  public String getKey() {
    return key;
  }

  public String getHash() {
    return hash;
  }

  public Path getJarPath() {
    return jarPath;
  }

  public boolean hasSameHash(ServerPlugin serverPlugin) {
    return getHash().equals(serverPlugin.getHash());
  }
}
