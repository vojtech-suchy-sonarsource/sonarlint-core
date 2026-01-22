/*
ACR-9f6818a49a234c72a502a2918ba8e104
ACR-25b0e9232bb14ce5ab7ec9115b65bfec
ACR-4d72d5a6809a4c4aa6f9fc7aa7281c6d
ACR-385ba5bc7f7245088a6c09bfa4688b5f
ACR-75ccc36625d147e08e18b32d4b1e5a80
ACR-59dc44c6f6e84bcdadff8fec4bd82492
ACR-5f33b6f1e5f24eef93a7db20ac79daf3
ACR-5eabc9403d4649638286918847b73da4
ACR-24529fc921e44ddcb235246bad88f2e9
ACR-a5a6a16c3c224159aca1c01df1f37e7e
ACR-1365423c948e4dd3840fe1444c39e2f9
ACR-3171656da83c452baf85dc8f596b9c76
ACR-c9d85e77958b4b15b612a4b80312df30
ACR-4a8e95e1894849c8913a5b8b5e63b3db
ACR-1fe0645366e345c1bbc89954d43dab83
ACR-7580103c0cd64a42a45ae9ce03216c70
ACR-eccf2002b7214aeabc56b12f995be894
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
