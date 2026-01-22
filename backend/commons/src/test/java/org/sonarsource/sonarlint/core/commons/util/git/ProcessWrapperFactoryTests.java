/*
ACR-2b495b93c8774da1a583d2456a1be026
ACR-af4e53aae3ce40a8a477a1f5fae577f8
ACR-8179dae3779b4e58bde6cbf71bd991ad
ACR-82b49483d0f444da91808bf84dc66f40
ACR-858bba9aa96b4dbfae131a5f3eeb2a1a
ACR-d9f010532e224317b7641c9e69356ca2
ACR-b21a34c9c3c343f782d41deccf99cae2
ACR-b64d7f86e0d74b9eac4eb86c5c5cef94
ACR-4af620425cb14bd9b846cb149d8f61b0
ACR-6fe0ea8c9597410abe9c3ead32256ca2
ACR-162deecbf8e2448db8de3d5979441812
ACR-6a102cd8b6bb4ec1a90a25665219a7d5
ACR-ed74cf2a008c4097a3027eccf0c3c30b
ACR-55ed8bd89c3e4c29983dc6fd7409091b
ACR-ae5106a535e046e3bc3fe1d9644bb851
ACR-12d9db7f1d6548dab4cfd5350235041b
ACR-4c4067bbe7cf439ca8d6fdbfd1c8c31b
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
