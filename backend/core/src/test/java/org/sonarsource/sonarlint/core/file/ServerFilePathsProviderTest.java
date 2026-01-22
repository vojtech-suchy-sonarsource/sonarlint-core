/*
ACR-ba59354b750048f0914242b75668f67d
ACR-010457dd172a42c8a88ebdac58e4bc0e
ACR-9039261150534142a6c9158bfa356044
ACR-3d71a55cb9c54c1da91e55c7e0008bd3
ACR-e1cabd8ebda34e5daed7634b96b6fc11
ACR-6d9ab07e000c452aa2e5c671b4f0278a
ACR-beae3a3ecc854d40b8a39debf7762a61
ACR-fc15070962c042d8ad57a7b81ebfaa87
ACR-6d651104077c4cb6b398b779937da30b
ACR-605b2ec6890c45d38e44f8759419a2cc
ACR-b6a57545a2524a56829fa48eecbcce32
ACR-f0b1e0b661c24a33ad6facf38fc78182
ACR-37f66e0d5545433fa99c05f149502235
ACR-18304629b8c24141968237fba927fb8e
ACR-5e8a2966c6b649658671b3a1190a3f89
ACR-f0c5522a18e349b1bdf5752581ac1a4b
ACR-78209d50ce72418aa9176c85821f6ed4
 */
package org.sonarsource.sonarlint.core.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.SonarQubeClientManager;
import org.sonarsource.sonarlint.core.UserPaths;
import org.sonarsource.sonarlint.core.commons.Binding;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import org.sonarsource.sonarlint.core.serverapi.component.ComponentApi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class ServerFilePathsProviderTest {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private static final String CONNECTION_A = "connection_A";
  private static final String CONNECTION_B = "connection_B";
  public static final String PROJECT_KEY = "projectKey";

  private Path cacheDirectory;
  private final SonarQubeClientManager sonarQubeClientManager = mock(SonarQubeClientManager.class);
  private final ServerApi serverApi_A = mock(ServerApi.class);
  private final ServerApi serverApi_B = mock(ServerApi.class);
  private final SonarLintCancelMonitor cancelMonitor = mock(SonarLintCancelMonitor.class);
  private final ComponentApi componentApi_A = mock(ComponentApi.class);
  private final ComponentApi componentApi_B = mock(ComponentApi.class);
  private ServerFilePathsProvider underTest;

  @BeforeEach
  void before(@TempDir Path storageDir) throws IOException {
    cacheDirectory = storageDir.resolve("cache");
    Files.createDirectories(cacheDirectory);
    when(serverApi_A.component()).thenReturn(componentApi_A);
    when(serverApi_B.component()).thenReturn(componentApi_B);
    when(sonarQubeClientManager.withActiveClientAndReturn(eq(CONNECTION_A), any())).thenAnswer(
      invocation -> Optional.ofNullable(((Function<ServerApi, Object>) invocation.getArguments()[1]).apply(serverApi_A)));
    when(sonarQubeClientManager.withActiveClientAndReturn(eq(CONNECTION_B), any())).thenAnswer(
      invocation -> Optional.ofNullable(((Function<ServerApi, Object>) invocation.getArguments()[1]).apply(serverApi_B)));
    mockServerFilePaths(componentApi_A, "pathA", "pathB");
    mockServerFilePaths(componentApi_B, "pathC", "pathD");
    var userPaths = mock(UserPaths.class);
    when(userPaths.getStorageRoot()).thenReturn(storageDir);
    underTest = new ServerFilePathsProvider(sonarQubeClientManager, userPaths);

    cacheDirectory = storageDir.resolve("cache");
  }

  @Test
  void clear_cache_directory_after_initialization(@TempDir Path storageDir) throws IOException {
    cacheDirectory = storageDir.resolve("cache");
    Files.createDirectories(cacheDirectory);
    assertThat(cacheDirectory.toFile()).exists();
    var userPaths = mock(UserPaths.class);
    when(userPaths.getStorageRoot()).thenReturn(storageDir);

    new ServerFilePathsProvider(null, userPaths);

    assertThat(cacheDirectory.toFile()).doesNotExist();
  }

  @Test
  void write_to_cache_file_after_fetch() throws IOException {
    underTest.getServerPaths(new Binding(CONNECTION_A, PROJECT_KEY), cancelMonitor);

    assertThat(cacheDirectory.toFile().listFiles()).hasSize(1);
    File file = Objects.requireNonNull(cacheDirectory.toFile().listFiles())[0];
    List<String> paths = FileUtils.readLines(file, Charset.defaultCharset());
    assertThat(paths).hasSize(2);
    assertThat(paths.get(0)).isEqualTo("pathA");
    assertThat(paths.get(1)).isEqualTo("pathB");
  }

  @Test
  void fetch_from_in_memory_for_the_second_attempt() throws IOException {
    underTest.getServerPaths(new Binding(CONNECTION_A, PROJECT_KEY), cancelMonitor);

    verify(componentApi_A, times(1)).getAllFileKeys(PROJECT_KEY, cancelMonitor);
    verifyNoMoreInteractions(componentApi_A);
    FileUtils.deleteDirectory(cacheDirectory.toFile());

    underTest.getServerPaths(new Binding(CONNECTION_A, PROJECT_KEY), cancelMonitor);

    assertThat(cacheDirectory.toFile()).doesNotExist();
  }

  @Test
  void fetch_from_file_when_cache_timeout() throws IOException {
    underTest.getServerPaths(new Binding(CONNECTION_A, PROJECT_KEY), cancelMonitor);

    File file = Objects.requireNonNull(cacheDirectory.toFile().listFiles())[0];
    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
    bufferedWriter.write("NewPath");
    bufferedWriter.newLine();
    bufferedWriter.close();

    underTest.clearInMemoryCache();

    List<Path> paths = underTest.getServerPaths(new Binding(CONNECTION_A, PROJECT_KEY), cancelMonitor).get();
    assertThat(paths).hasSize(3);
    assertThat(paths.get(0)).hasToString("pathA");
    assertThat(paths.get(1)).hasToString("pathB");
    assertThat(paths.get(2)).hasToString("NewPath");
  }

  @Test
  void write_to_two_cache_files_for_different_request() {
    underTest.getServerPaths(new Binding(CONNECTION_A, PROJECT_KEY), cancelMonitor);
    underTest.getServerPaths(new Binding(CONNECTION_B, PROJECT_KEY), cancelMonitor);

    assertThat(cacheDirectory.toFile().listFiles()).hasSize(2);
  }

  @Test
  void shouldLogAndIgnoreOtherErrors() {
    when(serverApi_A.component().getAllFileKeys(PROJECT_KEY, cancelMonitor)).thenAnswer(invocation -> {
      throw new IllegalStateException();
    });

    underTest.getServerPaths(new Binding(CONNECTION_A, PROJECT_KEY), cancelMonitor);

    assertThat(logTester.logs())
      .contains("Error while getting server file paths for project 'projectKey'");
  }

  private void mockServerFilePaths(ComponentApi componentApi, String... paths) {
    doReturn(Arrays.stream(paths).map(path -> PROJECT_KEY + ":" + path).toList())
      .when(componentApi)
      .getAllFileKeys(PROJECT_KEY, cancelMonitor);
  }
}
