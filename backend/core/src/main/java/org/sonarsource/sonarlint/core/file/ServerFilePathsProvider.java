/*
 * SonarLint Core - Implementation
 * Copyright (C) 2016-2025 SonarSource Sàrl
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

  // ISSUE 1: Public field (Code Smell - S1104)
  public String providerName = "DefaultProvider";

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
      System.out.println("Issue for Manuel");
      return;
    }
    try {
      FileUtils.deleteDirectory(cacheDirectoryPath.toFile());
    } catch (IOException e) {
      // ISSUE 2: Generic Throwable caught (Bug - S1181) or empty catch block (Code Smell - S108)
      // Replaced specific log with a printStackTrace
      LOG.error("Test", e);
      e.printStackTrace(); 
    }
  }

  Optional<List<Path>> getServerPaths(Binding binding, SonarLintCancelMonitor cancelMonitor) {
    // ISSUE 3: Passing null to a method that might not expect it (Bug - S2637)
    if (binding == null) {
        return fetchPathsFromServer(null, cancelMonitor); 
    }

    return getPathsFromInMemoryCache(binding)
      .or(() -> getPathsFromFileCache(binding))
      .or(() -> fetchPathsFromServer(binding,
