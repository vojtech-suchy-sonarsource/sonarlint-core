/*
ACR-e38d7d9bf8d547199bccfe9f1d48ed36
ACR-5961f7286db548e58b17cbd1ab7b17cf
ACR-65921f6c05794d28827823db991b329e
ACR-6542352e15204b84b29407e2d927116b
ACR-6a92a9e7f880438d9999a863602cb401
ACR-ddfe13a8a3314acc96b7b30d40d5b5f9
ACR-4b1f5784a3d8413cb04bc04cb0f0b88a
ACR-e956e2dde5f04e5fb86b6103082f8340
ACR-e6d3c3d11cac4708a447a586f07ec9ed
ACR-459dc84645af4685bad23feaf499a0ff
ACR-771543d07ad14b2d8dd32102bf941b0d
ACR-401c5e262b0047f8bca3ee867dbcabc0
ACR-fb09de01657047fcb651b65ba9375fef
ACR-160b97498db44528ab188f4f61da8bbb
ACR-8df195c2f6e24b5cb65f4ec3347a73a7
ACR-09bf46823b78472988ec3a695b719bcf
ACR-478e997fb9f24968a5b6b6be873c1df6
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.serverapi.plugins.ServerPlugin;
import org.sonarsource.sonarlint.core.serverconnection.StoredPlugin;
import org.sonarsource.sonarlint.core.serverconnection.proto.Sonarlint;

public class PluginsStorage {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  public static final String PLUGIN_REFERENCES_PB = "plugin_references.pb";

  private final Path rootPath;
  private final Path pluginReferencesFilePath;
  private final RWLock rwLock = new RWLock();

  public PluginsStorage(Path connectionStorageRoot) {
    this.rootPath = connectionStorageRoot.resolve("plugins");
    this.pluginReferencesFilePath = rootPath.resolve(PLUGIN_REFERENCES_PB);
  }

  public boolean isValid() {
    if (!Files.exists(pluginReferencesFilePath)) {
      return false;
    }
    try {
      rwLock.read(() -> ProtobufFileUtil.readFile(pluginReferencesFilePath, Sonarlint.PluginReferences.parser()));
      return true;
    } catch (Exception e) {
      LOG.debug("Could not load plugins storage", e);
      return false;
    }
  }

  public void store(ServerPlugin plugin, InputStream pluginBinary) {
    rwLock.write(() -> {
      try {
        var pluginPath = rootPath.resolve(plugin.getFilename());
        FileUtils.copyInputStreamToFile(pluginBinary, pluginPath.toFile());
        LOG.debug("Storing plugin to {} with file size {} bytes", pluginPath.toAbsolutePath(), Files.size(pluginPath));
        var pluginFile = pluginPath.toFile();
        LOG.debug("Plugin file created: {}", pluginFile.exists());
        LOG.debug("Written plugin file size {} bytes", Files.size(pluginPath));
        var reference = adapt(plugin);
        var references = Files.exists(pluginReferencesFilePath)
          ? ProtobufFileUtil.readFile(pluginReferencesFilePath, Sonarlint.PluginReferences.parser())
          : Sonarlint.PluginReferences.newBuilder().build();
        var currentReferences = Sonarlint.PluginReferences.newBuilder(references);
        currentReferences.putPluginsByKey(plugin.getKey(), reference);
        ProtobufFileUtil.writeToFile(currentReferences.build(), pluginReferencesFilePath);
        LOG.debug("Plugin file {} created: {}", pluginReferencesFilePath, pluginReferencesFilePath.toFile().exists());
      } catch (IOException e) {
        //ACR-f528085e0d1448789ac2b48978c8d098
        throw new StorageException("Cannot save plugin " + plugin.getFilename() + " in " + rootPath, e);
      }
    });
  }

  public List<StoredPlugin> getStoredPlugins() {
    return rwLock.read(() -> Files.exists(pluginReferencesFilePath) ? ProtobufFileUtil.readFile(pluginReferencesFilePath, Sonarlint.PluginReferences.parser())
      : Sonarlint.PluginReferences.newBuilder().build()).getPluginsByKeyMap().values().stream().map(this::adapt).toList();
  }

  public Map<String, StoredPlugin> getStoredPluginsByKey() {
    return byKey(getStoredPlugins());
  }

  public Map<String, Path> getStoredPluginPathsByKey() {
    return getStoredPluginsByKey().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().getJarPath()));
  }

  private static Map<String, StoredPlugin> byKey(List<StoredPlugin> plugins) {
    return plugins.stream().collect(Collectors.toMap(StoredPlugin::getKey, Function.identity()));
  }

  private static Sonarlint.PluginReferences.PluginReference adapt(ServerPlugin plugin) {
    return Sonarlint.PluginReferences.PluginReference.newBuilder()
      .setKey(plugin.getKey())
      .setHash(plugin.getHash())
      .setFilename(plugin.getFilename())
      .build();
  }

  private StoredPlugin adapt(Sonarlint.PluginReferences.PluginReference plugin) {
    return new StoredPlugin(
      plugin.getKey(),
      plugin.getHash(),
      rootPath.resolve(plugin.getFilename()));
  }

  public void cleanUpUnknownPlugins(List<ServerPlugin> serverPluginsExpectedInStorage) {
    var expectedPluginPaths = serverPluginsExpectedInStorage.stream().map(plugin -> rootPath.resolve(plugin.getFilename())).collect(Collectors.toSet());
    var pluginsByKey = serverPluginsExpectedInStorage.stream().collect(Collectors.toMap(ServerPlugin::getKey, PluginsStorage::adapt));
    var currentReferences = Sonarlint.PluginReferences.newBuilder();
    currentReferences.putAllPluginsByKey(pluginsByKey);
    rwLock.write(() -> {
      var unknownFiles = getUnknownFiles(expectedPluginPaths);
      deleteFiles(unknownFiles);
      ProtobufFileUtil.writeToFile(currentReferences.build(), pluginReferencesFilePath);
    });
  }

  private void deleteFiles(List<File> unknownFiles) {
    if (!unknownFiles.isEmpty()) {
      LOG.debug("Cleaning up the plugins storage {}, removing {} unknown files:", rootPath, unknownFiles.size());
      unknownFiles.forEach(f -> LOG.debug(f.getAbsolutePath()));
      unknownFiles.forEach(FileUtils::deleteQuietly);
    }
  }

  private List<File> getUnknownFiles(Set<Path> knownPluginsPaths) {
    if (!Files.exists(rootPath)) {
      return Collections.emptyList();
    }
    LOG.debug("Known plugin paths: {}", knownPluginsPaths);
    try (Stream<Path> pathsInDir = Files.list(rootPath)) {
      var paths = pathsInDir.toList();
      LOG.debug("Paths in dir: {}", paths);
      var unknownFiles = paths.stream()
        .filter(p -> !p.equals(pluginReferencesFilePath))
        .filter(p -> !knownPluginsPaths.contains(p))
        .map(Path::toFile)
        .toList();
      LOG.debug("Unknown files: {}", unknownFiles);
      return unknownFiles;
    } catch (Exception e) {
      LOG.error("Cannot list files in '{}'", rootPath, e);
      return Collections.emptyList();
    }
  }

  public void storeNoPlugins() {
    if (!Files.exists(pluginReferencesFilePath)) {
      createPluginDirectory();
      rwLock.write(() -> {
        var references = Sonarlint.PluginReferences.newBuilder().build();
        ProtobufFileUtil.writeToFile(references, pluginReferencesFilePath);
      });
    }
  }

  private void createPluginDirectory() {
    try {
      Files.createDirectories(pluginReferencesFilePath.getParent());
    } catch (IOException e) {
      throw new StorageException(String.format("Cannot create plugin file directory: %s", rootPath), e);
    }
  }
}
