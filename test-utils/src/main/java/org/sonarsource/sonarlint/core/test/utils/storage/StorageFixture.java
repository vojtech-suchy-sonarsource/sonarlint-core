/*
ACR-bad2c5295bf14a6c8c594edc06d9b5a6
ACR-4cfd6e3698684cc7afbb34fd667d4e14
ACR-bc851048c40c454e8a6802b0c6903fea
ACR-13444585e8ce436798e55b86fe8392c2
ACR-663f6029a38849c8816c82c0ebdc1449
ACR-1343c73af90a48b0afcbbece8d1be798
ACR-1b7b9ed6bc374ddd803116cc746279a6
ACR-e0f4774e19d84c7699f4dcf754172fd5
ACR-5da56b8307074ad2ba0e8b96a15d25eb
ACR-608bbc5db3864b8589196fd1884c37d8
ACR-19687c5837bf4edd8d2d59a398aac50f
ACR-9d2ecdd26c9442f39e968c97a08bde10
ACR-cfe8ce60f34840198418087d5a9d1ef3
ACR-89a70773dbaf47b8aa16de7a5b86932c
ACR-f5cfd9aab0ea4072b2dfe2836a539d42
ACR-7318b038791a4b8485f80a1f2e9fb4f1
ACR-245334e139534a4faf693abab154ffa4
 */
package org.sonarsource.sonarlint.core.test.utils.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.apache.commons.io.FileUtils;
import org.sonarsource.sonarlint.core.serverapi.features.Feature;
import org.sonarsource.sonarlint.core.serverconnection.proto.Sonarlint;
import org.sonarsource.sonarlint.core.serverconnection.storage.ProtobufFileUtil;

import static org.sonarsource.sonarlint.core.serverconnection.storage.ProjectStoragePaths.encodeForFs;

public class StorageFixture {
  public static StorageBuilder newStorage(String connectionId) {
    return new StorageBuilder(connectionId);
  }

  public static class StorageBuilder {
    private final String connectionId;
    private final List<Feature> supportedFeatures = new ArrayList<>();
    private final List<Plugin> plugins = new ArrayList<>();
    private final List<ProjectStorageFixture.ProjectStorageBuilder> projectBuilders = new ArrayList<>();
    private AiCodeFixFixtures.Builder aiCodeFixBuilder;
    private String serverVersion;
    private Map<String, String> globalSettings;

    private StorageBuilder(String connectionId) {
      this.connectionId = connectionId;
    }

    public StorageBuilder withGlobalSettings(Map<String, String> globalSettings) {
      this.globalSettings = globalSettings;
      return this;
    }

    public StorageBuilder withServerVersion(String serverVersion) {
      this.serverVersion = serverVersion;
      return this;
    }

    public StorageBuilder withServerFeature(Feature feature) {
      this.supportedFeatures.add(feature);
      return this;
    }

    public StorageBuilder withPlugins(org.sonarsource.sonarlint.core.test.utils.plugins.Plugin... plugins) {
      var builder = this;
      for (org.sonarsource.sonarlint.core.test.utils.plugins.Plugin plugin : plugins) {
        builder = builder.withPlugin(plugin);
      }
      return builder;
    }

    public StorageBuilder withPlugin(org.sonarsource.sonarlint.core.test.utils.plugins.Plugin plugin) {
      return withPlugin(plugin.getPluginKey(), plugin.getPath(), plugin.getHash());
    }

    public StorageBuilder withPlugin(String key, Path jarPath, String hash) {
      plugins.add(new Plugin(jarPath, jarPath.getFileName().toString(), hash, key));
      return this;
    }

    public StorageBuilder withProject(String projectKey, Consumer<ProjectStorageFixture.ProjectStorageBuilder> consumer) {
      var builder = new ProjectStorageFixture.ProjectStorageBuilder(connectionId, projectKey);
      consumer.accept(builder);
      projectBuilders.add(builder);
      return this;
    }

    public StorageBuilder withProject(String projectKey) {
      return withProject(projectKey, builder -> {
      });
    }

    public StorageBuilder withAiCodeFixSettings(Consumer<AiCodeFixFixtures.Builder> consumer) {
      var builder = new AiCodeFixFixtures.Builder(this.connectionId);
      consumer.accept(builder);
      aiCodeFixBuilder = builder;
      return this;
    }

    public String getConnectionId() {
      return connectionId;
    }

    public void populate(Path rootPath, TestDatabase database) {
      var storagePath = rootPath.resolve("storage");
      var connectionStorage = storagePath.resolve(encodeForFs(connectionId));
      var pluginsFolderPath = connectionStorage.resolve("plugins");
      var projectsFolderPath = connectionStorage.resolve("projects");
      try {
        FileUtils.forceMkdir(pluginsFolderPath.toFile());
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }

      createServerInfo(connectionStorage);

      createPlugins(pluginsFolderPath);
      createPluginReferences(pluginsFolderPath);

      projectBuilders.forEach(project -> project.populate(projectsFolderPath, database));
      if (aiCodeFixBuilder != null) {
        aiCodeFixBuilder.populate(database);
      }
    }

    private void createServerInfo(Path connectionStorage) {
      if (serverVersion != null || globalSettings != null || !supportedFeatures.isEmpty()) {
        var version = serverVersion == null ? "0.0.0" : serverVersion;
        var settings = globalSettings == null ? Map.<String, String>of() : globalSettings;
        ProtobufFileUtil.writeToFile(Sonarlint.ServerInfo.newBuilder()
          .setVersion(version)
          .addAllSupportedFeatures(supportedFeatures.stream().map(Feature::getKey).toList())
          .putAllGlobalSettings(settings)
          .build(),
          connectionStorage.resolve("server_info.pb"));
      }
    }

    private void createPlugins(Path pluginsFolderPath) {
      plugins.forEach(plugin -> {
        var pluginPath = pluginsFolderPath.resolve(plugin.jarName);
        try {
          Files.copy(plugin.path, pluginPath);
        } catch (IOException e) {
          throw new IllegalStateException("Cannot copy plugin " + plugin.jarName, e);
        }
      });
    }

    private void createPluginReferences(Path pluginsFolderPath) {
      var builder = Sonarlint.PluginReferences.newBuilder();
      plugins.forEach(plugin -> builder.putPluginsByKey(plugin.key, Sonarlint.PluginReferences.PluginReference.newBuilder()
        .setFilename(plugin.jarName)
        .setHash(plugin.hash)
        .setKey(plugin.key)
        .build()));
      ProtobufFileUtil.writeToFile(builder.build(), pluginsFolderPath.resolve("plugin_references.pb"));
    }

    private record Plugin(Path path, String jarName, String hash, String key) {
    }
  }

  private StorageFixture() {
    //ACR-637e7ccc5bc047bca224415adfdbfd04
  }
}
