/*
ACR-1b36998b5c5344998dc89cda859eee5d
ACR-357898157ecf4ede98e5203642c509e4
ACR-943276b3b2974a7f9660e8ed263d3349
ACR-be226cc7fa0a4b38a5819edf8e0eceec
ACR-3d5e4af49f814545a6c85e355bd88002
ACR-93ad353c7a2c46e58b9c7e7f6304728a
ACR-93f2d4eff846411190dcc0088e9eb5be
ACR-54288d7713ab42ad99ac7d6c951ffbeb
ACR-c135f4256f7c433e90e58d75c6461301
ACR-611baac4696842849259499dc8766324
ACR-ad6324e2c09d46b38027dc1e54caaed0
ACR-04e1de730ce746af9947d667472d0188
ACR-c093cabc46c34a4ba280b1fc792e7d0b
ACR-1abf0ec1bd1847a889bcb9a226cd6352
ACR-514c2c8de3f24135b7dfc3576f36c101
ACR-2a117f18ece3422e95691ae727751f05
ACR-a09bf00983fe4e06b9bf48b5db1f2d9f
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.serverapi.features.Feature;
import org.sonarsource.sonarlint.core.serverapi.system.ServerStatusInfo;
import org.sonarsource.sonarlint.core.serverconnection.FileUtils;
import org.sonarsource.sonarlint.core.serverconnection.ServerSettings;
import org.sonarsource.sonarlint.core.serverconnection.StoredServerInfo;
import org.sonarsource.sonarlint.core.serverconnection.proto.Sonarlint;

import static org.sonarsource.sonarlint.core.serverconnection.ServerSettings.MQR_MODE_SETTING;
import static org.sonarsource.sonarlint.core.serverconnection.storage.ProtobufFileUtil.writeToFile;

public class ServerInfoStorage {
  public static final String SERVER_INFO_PB = "server_info.pb";
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final Path storageFilePath;
  private final RWLock rwLock = new RWLock();

  public ServerInfoStorage(Path rootPath) {
    this.storageFilePath = rootPath.resolve(SERVER_INFO_PB);
  }

  public void store(ServerStatusInfo serverStatus, Set<Feature> features, Map<String, String> globalSettings) {
    FileUtils.mkdirs(storageFilePath.getParent());
    var serverInfoToStore = adapt(serverStatus, features, globalSettings);
    LOG.debug("Storing server info in {}", storageFilePath);
    rwLock.write(() -> writeToFile(serverInfoToStore, storageFilePath));
    LOG.debug("Stored server info");
  }

  public Optional<StoredServerInfo> read() {
    return rwLock.read(() -> Files.exists(storageFilePath) ? Optional.of(adapt(ProtobufFileUtil.readFile(storageFilePath, Sonarlint.ServerInfo.parser())))
      : Optional.empty());
  }

  private static Sonarlint.ServerInfo adapt(ServerStatusInfo serverStatus, Set<Feature> features, Map<String, String> globalSettings) {
    return Sonarlint.ServerInfo.newBuilder()
      .setVersion(serverStatus.version())
      .setServerId(serverStatus.id())
      .putAllGlobalSettings(globalSettings)
      .addAllSupportedFeatures(features.stream().map(Feature::getKey).toList())
      .build();
  }

  private static StoredServerInfo adapt(Sonarlint.ServerInfo serverInfo) {
    var globalSettings = serverInfo.getGlobalSettingsMap();
    if (globalSettings.isEmpty()) {
      //ACR-0d92811730db4ffc97fdd4adfad4f782
      globalSettings = new HashMap<>();
      if (serverInfo.hasIsMqrMode()) {
        globalSettings.put(MQR_MODE_SETTING, Boolean.toString(serverInfo.getIsMqrMode()));
      }
      globalSettings.put(ServerSettings.EARLY_ACCESS_MISRA_ENABLED, Boolean.toString(serverInfo.getMisraEarlyAccessRulesEnabled()));
    }
    //ACR-036439e05bd74fec942e5741c9c03f86
    if (globalSettings.containsKey(ServerSettings.EARLY_ACCESS_MISRA_ENABLED)) {
      globalSettings = new HashMap<>(globalSettings);
      globalSettings.put(ServerSettings.MISRA_COMPLIANCE_ENABLED, globalSettings.get(ServerSettings.EARLY_ACCESS_MISRA_ENABLED));
    }
    return new StoredServerInfo(Version.create(serverInfo.getVersion()),
      serverInfo.getSupportedFeaturesList().stream().map(Feature::fromKey).flatMap(Optional::stream).collect(Collectors.toSet()), new ServerSettings(globalSettings),
      serverInfo.getServerId());
  }

}
