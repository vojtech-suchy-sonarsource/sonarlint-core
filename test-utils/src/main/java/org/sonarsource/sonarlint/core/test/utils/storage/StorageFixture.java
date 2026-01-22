/*
ACR-4e9b3e2bfcd74226b5e64dd352e98308
ACR-45e420d5dbb64be689376e6b78c894d0
ACR-ba6f93c7c7e14405b54167074fe9a11c
ACR-4058a694223b48adb94410110a22a6a7
ACR-84d359f6de054969bca65b4d4cc54133
ACR-10da1eb5e26c43679017b7eb3da5c680
ACR-26daa22f9d354177afd803f6f43bec36
ACR-2e38a20cb7664bfe86b455ce0c78bb75
ACR-66d38b6d79b44b5398457ec225e10362
ACR-9d694a3047974baf869cc41ae56c891a
ACR-c126c351b7564e5794d072eef61fbdef
ACR-7d2ac5754e9c443e8d7a7b51356df02d
ACR-10d4fb2d479c4cc58ce4116fb719b354
ACR-33e787ef493d4004b2160188c909de92
ACR-dfe77ec2a991466688377767b3b0c9ca
ACR-0ad4c8907fbb416da41df07f3e2f6118
ACR-3b03ec224c804853984a144f9752ddec
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
    //ACR-ba30ba2b99f44b89a13ce44a2130e17d
  }
}
