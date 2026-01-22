/*
ACR-93eb27b66f734ebe88952d0ba8793bc2
ACR-6c5cdaf174bd421f933351324153f07e
ACR-bea748d95b1f4852b72b29148424e922
ACR-d2c5c394e7bd413fba1861d36c0c06a9
ACR-675cfe8b420844629ea63173681e1268
ACR-28c879ee923c4af28046afcceb26a2a9
ACR-b603b7d78e85447c9af3799477070730
ACR-2b8946f5157e4c429b6f48e09e649486
ACR-b86eb3020c9b4a8ca1d32a5cb3da2067
ACR-6100c6edee5c400baf4fee585d92b3bc
ACR-76102575c37148f7954ff516331a4c96
ACR-db46a8c4e8b441e6aba830c5a24c2bcb
ACR-57583982c10e45ba805c02786ed73668
ACR-1bce8adc25c24a008ba0800a7a6d4110
ACR-96cd2576b345462aa3f59e4e3d84393e
ACR-59a1d768bee644bbac7c11035f735189
ACR-0a19125bf723419594b0fd11fcacf902
 */
package org.sonarsource.sonarlint.core.rpc.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.sonarsource.sonarlint.core.rpc.protocol.client.log.LogLevel;
import org.sonarsource.sonarlint.core.rpc.protocol.client.log.LogParams;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SloopLauncherTests {
  private Process mockProcess;
  private SloopLauncher underTest;
  private Sloop sloop;
  private Function<List<String>, ProcessBuilder> mockPbFactory;
  private SonarLintRpcClientDelegate rpcClient;
  private String osName = "Linux";
  private Path fakeJreHomePath;
  private Path fakeJreJavaLinuxPath;
  private Path fakeJreJavaWindowsPath;

  @BeforeEach
  void prepare(@TempDir Path fakeJreHomePath) throws IOException {
    this.fakeJreHomePath = fakeJreHomePath;
    var fakeJreBinFolder = this.fakeJreHomePath.resolve("bin");
    Files.createDirectories(fakeJreBinFolder);
    fakeJreJavaLinuxPath = fakeJreBinFolder.resolve("java");
    Files.createFile(fakeJreJavaLinuxPath);
    fakeJreJavaWindowsPath = fakeJreBinFolder.resolve("java.exe");
    Files.createFile(fakeJreJavaWindowsPath);
    mockPbFactory = mock();
    var mockProcessBuilder = mock(ProcessBuilder.class);
    when(mockPbFactory.apply(any())).thenReturn(mockProcessBuilder);
    mockProcess = mock(Process.class);
    when(mockProcess.onExit()).thenReturn(new CompletableFuture<>());
    doReturn(mockProcess).when(mockProcessBuilder).start();

    when(mockProcess.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
    when(mockProcess.getErrorStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
    when(mockProcess.getOutputStream()).thenReturn(new ByteArrayOutputStream());

    rpcClient = mock(SonarLintRpcClientDelegate.class);
    underTest = new SloopLauncher(rpcClient, mockPbFactory, () -> osName);
  }

  @Test
  void test_command_with_embedded_jre(@TempDir Path distPath) throws IOException {
    var bundledJreBinPath = distPath.resolve("jre").resolve("bin");
    Files.createDirectories(bundledJreBinPath);
    var bundledJrejavaPath = bundledJreBinPath.resolve("java");
    Files.createFile(bundledJrejavaPath);

    sloop = underTest.start(distPath);

    verify(mockPbFactory).apply(List.of(bundledJrejavaPath.toString(), "-Djava.awt.headless=true",
      "-classpath", distPath.resolve("lib") + File.separator + '*', "org.sonarsource.sonarlint.core.backend.cli.SonarLintServerCli"));
    assertThat(sloop.getRpcServer()).isNotNull();
  }

  @Test
  void test_command_with_custom_jre_on_linux(@TempDir Path distPath) {
    sloop = underTest.start(distPath, fakeJreHomePath);

    verify(mockPbFactory)
      .apply(List.of(fakeJreJavaLinuxPath.toString(), "-Djava.awt.headless=true",
        "-classpath", distPath.resolve("lib") + File.separator + '*', "org.sonarsource.sonarlint.core.backend.cli.SonarLintServerCli"));
    assertThat(sloop.getRpcServer()).isNotNull();
  }

  @Test
  void test_command_with_custom_jre_on_windows(@TempDir Path distPath) {
    osName = "Windows";
    sloop = underTest.start(distPath, fakeJreHomePath);

    verify(mockPbFactory)
      .apply(List.of(fakeJreJavaWindowsPath.toString(), "-Djava.awt.headless=true",
        "-classpath", distPath.resolve("lib") + File.separator + '*', "org.sonarsource.sonarlint.core.backend.cli.SonarLintServerCli"));
    assertThat(sloop.getRpcServer()).isNotNull();
  }

  @Test
  void test_redirect_stderr_to_client(@TempDir Path distPath) {
    when(mockProcess.getErrorStream()).thenReturn(new ByteArrayInputStream("Some errors\nSome other error".getBytes()));

    sloop = underTest.start(distPath, fakeJreHomePath);

    ArgumentCaptor<LogParams> captor = ArgumentCaptor.captor();
    verify(rpcClient, timeout(1000).times(3)).log(captor.capture());

    assertThat(captor.getAllValues())
      .filteredOn(m -> m.getLevel() == LogLevel.ERROR)
      .extracting(LogParams::getMessage)
      .containsExactly("StdErr: Some errors", "StdErr: Some other error");
  }

  @Test
  void test_log_stacktrace(@TempDir Path distPath) {
    doThrow(new IllegalStateException("Some error")).when(mockProcess).getInputStream();

    assertThrows(IllegalStateException.class, () -> sloop = underTest.start(distPath, fakeJreHomePath));

    ArgumentCaptor<LogParams> captor = ArgumentCaptor.captor();
    verify(rpcClient, times(2)).log(captor.capture());

    var log = captor.getValue();

    assertThat(log.getMessage()).isEqualTo("Unable to start the SonarLint backend");
    assertThat(log.getStackTrace()).startsWith("java.lang.IllegalStateException: Some error");
  }

  @Test
  void test_throw_error_if_java_path_does_not_exist(@TempDir Path distPath) {
    var wrongPath = Paths.get("wrongPath");
    assertThrows(IllegalStateException.class, () -> sloop = underTest.start(distPath, wrongPath));
  }

  @Test
  void test_command_with_custom_jre_on_linux_and_jvm_option(@TempDir Path distPath) {
    sloop = underTest.start(distPath, fakeJreHomePath, "-XX:+UseG1GC -XX:MaxHeapFreeRatio=50");

    verify(mockPbFactory)
      .apply(List.of(fakeJreJavaLinuxPath.toString(), "-XX:+UseG1GC", "-XX:MaxHeapFreeRatio=50", "-Djava.awt.headless=true",
        "-classpath", distPath.resolve("lib") + File.separator + '*', "org.sonarsource.sonarlint.core.backend.cli.SonarLintServerCli"));
    assertThat(sloop.getRpcServer()).isNotNull();
  }
}
