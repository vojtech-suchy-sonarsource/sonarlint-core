/*
ACR-26051799c61d4c219b58fd0c3a9ef7e1
ACR-9cfb61a6c7c9463b9bbfa29fa134b07b
ACR-aa5e0932064445bbbd46c57016e35b1e
ACR-05305e00defb4da4af10df2ff65a3b0d
ACR-3d0743dc084549a6a468ae258b341ef6
ACR-2515d0b6998e4386a3f4f9adad847ff7
ACR-504a3a93505a4a57b8b1130663c43acf
ACR-2405b786851743228e4d5f5e3b01f701
ACR-597d8896b16b422a9579723181510312
ACR-a745310e43d945eaade8fd9b939ca522
ACR-5f9c01f1034d436289f54a91f049144f
ACR-f36d66a1285c4c2a8a26d0ba2898703c
ACR-a2f06d464e89483ca239ecbda92f3b32
ACR-a1cf43e37f5d4b9d87da0fcf662be8e4
ACR-0dc72563a90140538aa0d065ea7ce023
ACR-644dab69c351495c99cdb358f11ef68b
ACR-181cc0802b6b4819be3855f7b9c3d5bf
 */
package org.sonarsource.sonarlint.core.file;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.sonarsource.sonarlint.core.SonarQubeClientManager;
import org.sonarsource.sonarlint.core.UserPaths;
import org.sonarsource.sonarlint.core.commons.Binding;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;

public class ServerFilePathsProvider {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final SonarQubeClientManager sonarQubeClientManager;
  private final Map<Binding, Path> cachedResponseFilePathByBinding = new HashMap<>();
  private final Path cacheDirectoryPath;
  private final Cache<Binding, List<Path>> temporaryInMemoryFilePathCacheByBinding;

  public ServerFilePathsProvider(SonarQubeClientManager sonarQubeClientManager, UserPaths userPaths) {
    this.sonarQubeClientManager = sonarQubeClientManager;
    this.cacheDirectoryPath = userPaths.getStorageRoot().resolve("cache");
    this.temporaryInMemoryFilePathCacheByBinding = CacheBuilder.newBuilder()
      .expireAfterWrite(Duration.of(1, ChronoUnit.MINUTES))
      .maximumSize(3)
      .build();

    clearCachePath();
  }

  private void clearCachePath() {
    if (!cacheDirectoryPath.toFile().exists()) {
      return;
    }
    try {
      FileUtils.deleteDirectory(cacheDirectoryPath.toFile());
    } catch (IOException e) {
      LOG.debug("Error occurred while deleting a cache file", e);
    }
  }

  Optional<List<Path>> getServerPaths(Binding binding, SonarLintCancelMonitor cancelMonitor) {
    return getPathsFromInMemoryCache(binding)
      .or(() -> getPathsFromFileCache(binding))
      .or(() -> fetchPathsFromServer(binding, cancelMonitor));
  }

  private Optional<List<Path>> getPathsFromInMemoryCache(Binding binding) {
    return Optional.ofNullable(temporaryInMemoryFilePathCacheByBinding.getIfPresent(binding));
  }

  private Optional<List<Path>> getPathsFromFileCache(Binding binding) {
    return Optional.ofNullable(cachedResponseFilePathByBinding.get(binding))
      .filter(path -> path.toFile().exists())
      .map(path -> {
        List<Path> paths = readServerPathsFromFile(path);
        putToInMemoryCache(binding, paths);
        return paths;
      });
  }

  private Optional<List<Path>> fetchPathsFromServer(Binding binding, SonarLintCancelMonitor cancelMonitor) {
    try {
      return sonarQubeClientManager.withActiveClientAndReturn(binding.connectionId(), serverApi -> {
        List<Path> paths = fetchPathsFromServer(serverApi, binding.sonarProjectKey(), cancelMonitor);
        cacheServerPaths(binding, paths);
        return paths;
      });
    } catch (CancellationException e) {
      throw e;
    } catch (Exception e) {
      LOG.debug("Error while getting server file paths for project '{}'", binding.sonarProjectKey(), e);
      return Optional.empty();
    }
  }

  private static List<Path> readServerPathsFromFile(Path responsePath) {
    try {
      return Files.readAllLines(responsePath).stream().map(Paths::get).toList();
    } catch (IOException e) {
      LOG.debug("Error occurred while reading the file server path response file cache {}", responsePath);
      return Collections.emptyList();
    }
  }

  private void putToInMemoryCache(Binding binding, List<Path> paths) {
    temporaryInMemoryFilePathCacheByBinding.put(binding, paths);
  }

  private static List<Path> fetchPathsFromServer(ServerApi serverApi, String projectKey, SonarLintCancelMonitor cancelMonitor) {
    return serverApi.component().getAllFileKeys(projectKey, cancelMonitor).stream()
      .map(fileKey -> StringUtils.substringAfterLast(fileKey, ":"))
      .map(Paths::get)
      .toList();
  }

  private void cacheServerPaths(Binding binding, List<Path> paths) {
    var fileName = UUID.randomUUID().toString();
    var filePath = cacheDirectoryPath.resolve(fileName);
    try {
      Files.createDirectories(cacheDirectoryPath);
      writeToFile(filePath, paths);
      cachedResponseFilePathByBinding.put(binding, filePath);
      putToInMemoryCache(binding, paths);
    } catch (IOException e) {
      LOG.debug("Error occurred while writing the cache file", e);
    }
  }

  private static void writeToFile(Path filePath, List<Path> paths) throws IOException {
    try (var bufferedWriter = new BufferedWriter(new FileWriter(filePath.toFile(), Charset.defaultCharset()))) {
      for (Path path : paths) {
        bufferedWriter.write(path + System.lineSeparator());
      }
    }
  }

  @VisibleForTesting
  void clearInMemoryCache() {
    temporaryInMemoryFilePathCacheByBinding.invalidateAll();
  }
}
