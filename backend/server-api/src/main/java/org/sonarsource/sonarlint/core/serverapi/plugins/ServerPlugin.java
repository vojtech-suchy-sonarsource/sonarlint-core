/*
ACR-5eebee8d6486409196fd9bed1d018573
ACR-e037f41b1aaf4c1ba14ed4ad1f1504fc
ACR-ed06e70e434345178bf5ed846830c897
ACR-5991d5b9ca9d416bb4c1972a97839841
ACR-3fa57e635ba24ac2b140019be3c4167d
ACR-67c3ff954778425999be76ed3ccc34ca
ACR-b7faec52122549d1b62717328b201f0c
ACR-bcac952af58d4ed0bfb0211a590efa58
ACR-c6f5d031298e4a2d92d0b200c45b20c1
ACR-fdc16f89e5914bd393c6992fb11b3db5
ACR-df9a9c310a5f42658956cdd9ba7ec211
ACR-778d72b78b2e4de9b582db9ea546b709
ACR-5bedb35c60d54d8babe9b77fd8401983
ACR-21b34e9fccf144858dd79d24af4a3ff6
ACR-30bf82a655744b438915a5ec3bf3dea8
ACR-186661bd4d784301be16fcc6d37f69b0
ACR-99afd427c0194cc5ab1d3a2bed69d4d9
 */
package org.sonarsource.sonarlint.core.serverapi.plugins;

public class ServerPlugin {
  private final String key;
  private final String hash;
  private final String filename;
  private final boolean sonarLintSupported;

  public ServerPlugin(String key, String hash, String filename, boolean sonarLintSupported) {
    this.key = key;
    this.hash = hash;
    this.filename = filename;
    this.sonarLintSupported = sonarLintSupported;
  }

  public String getKey() {
    return key;
  }

  public String getHash() {
    return hash;
  }

  public String getFilename() {
    return filename;
  }

  public boolean isSonarLintSupported() {
    return sonarLintSupported;
  }
}
