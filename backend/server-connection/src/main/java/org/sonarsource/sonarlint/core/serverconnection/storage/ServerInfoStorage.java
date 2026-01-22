/*
ACR-838eab0aaf2d40e9b4a283b66051dbd1
ACR-39094f468d56419e9283c0f56c65457b
ACR-625dfffaf399411f81aa20f7a5c956cc
ACR-73f4d1ee216a4eb0a6a00d8684f3290f
ACR-75b25b05d6f54316afc303d1b539d52e
ACR-616987d437094ec6a3e9c98564ad99a5
ACR-254b1f5eeb314afc8286651439cfb440
ACR-42c97068bb56495db9b0a87858bd797c
ACR-71f96d68992e4e119aafc7f3a855c026
ACR-f997ba0e029445088d75e6530df8f7d0
ACR-5082461ed81a4f8f91b9bb11a82f5086
ACR-2d82007d7bae432a87e928371f41a583
ACR-1fce8da9f3dc42e7915674e900a6b81d
ACR-4a3e2cfe45744db78e90faaaf0c951bb
ACR-cbbbe3d2bd574983967dfb45b5c73626
ACR-9afa3e1d617e4e47a833aa76bfd45988
ACR-0e2292631665482b8f83dc7472db5c0f
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
      //ACR-a59b043c183546f581b33ef9624ee638
      globalSettings = new HashMap<>();
      if (serverInfo.hasIsMqrMode()) {
        globalSettings.put(MQR_MODE_SETTING, Boolean.toString(serverInfo.getIsMqrMode()));
      }
      globalSettings.put(ServerSettings.EARLY_ACCESS_MISRA_ENABLED, Boolean.toString(serverInfo.getMisraEarlyAccessRulesEnabled()));
    }
    //ACR-7f1172c0580947cab2638f28ae77206b
    if (globalSettings.containsKey(ServerSettings.EARLY_ACCESS_MISRA_ENABLED)) {
      globalSettings = new HashMap<>(globalSettings);
      globalSettings.put(ServerSettings.MISRA_COMPLIANCE_ENABLED, globalSettings.get(ServerSettings.EARLY_ACCESS_MISRA_ENABLED));
    }
    return new StoredServerInfo(Version.create(serverInfo.getVersion()),
      serverInfo.getSupportedFeaturesList().stream().map(Feature::fromKey).flatMap(Optional::stream).collect(Collectors.toSet()), new ServerSettings(globalSettings),
      serverInfo.getServerId());
  }

}
