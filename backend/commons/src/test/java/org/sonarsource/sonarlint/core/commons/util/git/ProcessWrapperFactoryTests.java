/*
ACR-f2d9d1a2e1524f3083208e06f57dd92e
ACR-0333a0bfef5e4cf6b26da691f9b34b54
ACR-59d86824ad1940c7910c896d52fd9bb6
ACR-be4dd34c58f944c193d22adb35348154
ACR-fb97950f10f44a36b29e3adc5f9a0591
ACR-663c0461fa3f47bda2b5e16ac5710b36
ACR-1bac6b489cbe4b86a4ae449841c61b78
ACR-09929f7c6e9d4f5db4611e57e4322469
ACR-400bab39249c4f7387eb895f1b2413e4
ACR-5dfd30fd0bc745b89e28cd5281598dc6
ACR-6ef1a33f64e84a6db2dd4f757b459a8d
ACR-10e4e8772a9e49db96e416e05222507b
ACR-d752fb012aac44cfabf1af65e070a1f4
ACR-133d4d9099ee462f864189e9fe4f56ce
ACR-13c2cb1f14044b4aabef948b5ccf1cc6
ACR-3e3a3bb2355d4c6eaa493d044892725e
ACR-3fcc31d7ab2a45e497974a8e1ea20832
 */
package org.sonarsource.sonarlint.core.commons.util.git;

import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

class ProcessWrapperFactoryTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  @Test
  void it_should_execute_git(@TempDir Path baseDir) {
    assumeTrue(new NativeGitLocator().getNativeGitExecutable().isPresent());
    var lines = new StringBuilder();
    var result = new ProcessWrapperFactory().create(baseDir, lines::append, "git", "--version").execute();

    assertThat(result.exitCode()).isZero();
    assertThat(lines).contains("git version ");
  }

  @Test
  void it_should_return_output_for_invalid_command(@TempDir Path baseDir) {
    assumeTrue(new NativeGitLocator().getNativeGitExecutable().isPresent());
    var processWrapper = new ProcessWrapperFactory().create(baseDir, l -> {
    }, "git", "-version");
    var result = processWrapper.execute();
    assertThat(result.exitCode()).isEqualTo(129);
    assertThat(logTester.logs()).contains("unknown option: -version");
  }

  @Test
  void it_should_gracefully_return_output_for_interrupted_exception(@TempDir Path baseDir) throws InterruptedException, IOException {
    assumeTrue(new NativeGitLocator().getNativeGitExecutable().isPresent());
    var lines = new StringBuilder();
    var processWrapper = new ProcessWrapperFactory().create(baseDir, lines::append, "git", "--version");
    var spy = spy(processWrapper);
    doThrow(InterruptedException.class).when(spy).runProcessAndGetOutput(any());
    var result = spy.execute();

    assertThat(result.exitCode()).isEqualTo(-1);
    assertThat(lines).contains("");
  }

  @Test
  void it_should_gracefully_return_output_for_exception(@TempDir Path baseDir) throws InterruptedException, IOException {
    assumeTrue(new NativeGitLocator().getNativeGitExecutable().isPresent());
    var lines = new StringBuilder();
    var processWrapper = new ProcessWrapperFactory().create(baseDir, lines::append, "git", "--version");
    var spy = spy(processWrapper);
    doThrow(RuntimeException.class).when(spy).runProcessAndGetOutput(any());
    var result = spy.execute();

    assertThat(result.exitCode()).isEqualTo(-1);
    assertThat(lines).contains("");
  }

  @Test
  void it_should_gracefully_return_output_when_not_able_to_create_process(@TempDir Path baseDir) throws IOException {
    var lines = new StringBuilder();
    var processWrapper = new ProcessWrapperFactory().create(baseDir, lines::append, "git", "--version");
    var spy = spy(processWrapper);
    doThrow(IOException.class).when(spy).createProcess();
    var result = spy.execute();

    assertThat(result.exitCode()).isEqualTo(-2);
    assertThat(lines).contains("");
  }
}
