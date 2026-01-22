/*
ACR-08f72174ffe048f789868eb63ed41fec
ACR-51d459d6857d4da5916c44e3c9dc7665
ACR-05a9e0298e23498eac41ec19614b53f6
ACR-fdfa75a508b046be952199e0830bf0b8
ACR-57e62f91db7a470399cdcb00b197e778
ACR-ad7e6d95a26046da9e7ac463220e5ebe
ACR-96105c6826df4f0aa030d5d72798a519
ACR-a387137f89cb4b589d54991500f2313d
ACR-0a6852c2e8374b6e87029fa5376ca117
ACR-596d01208a6c483caa2c496cb83d790e
ACR-85e2292e34f740ec82afba9992fd3547
ACR-dbab227a097b41c2ad5f7a8163f8ee20
ACR-845f2037bc1c4149944bc6dd93095d23
ACR-f8f09841bdb64a84a629c6ae2d767c33
ACR-68ce90c23cfd49daa1d8e7902d477c10
ACR-6edea131894c4cd4927b06735ff6e6bc
ACR-f1951c8ee51a4e14ac1307cbf9f5aae9
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
