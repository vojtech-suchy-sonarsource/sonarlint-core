/*
ACR-0c634f5a3ae745cabfdc2162eb0c05d9
ACR-468078cb8dca43d888f8ca51028de9cb
ACR-0293c55e0304437ea931d30d23596c4c
ACR-5a93a76cddf34951b74f183cd480915b
ACR-fdd46f9629eb49d6b1366f6ebdc29b24
ACR-c2319688768e4c478983985cd3a0b68e
ACR-cb92344bfd6c4157879fbcd48e8badec
ACR-34273b76b56e4b50b8fd5e32f528e965
ACR-1fe892e46f074e3483166864187d91cb
ACR-f8aaf7b2928145fcb03194b4ea5a9bfe
ACR-9e0f84c5e96749979bb101493201ade7
ACR-7f86d32dfb944f419e808d52c0acf3cb
ACR-cfe92619b2a44dc583aae9ed16c93081
ACR-93f045c63d104cc48779e02648243e59
ACR-6ae693ad7f29424e81e1b5e9ca30e620
ACR-0aa7b256034146f8863675885f8327f8
ACR-3b698408cfae4fc3bbab7f8d7e3010a4
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
