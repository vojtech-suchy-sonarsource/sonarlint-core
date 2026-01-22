/*
ACR-4cfc473fb0b9405a8b7d49e5b0fd7713
ACR-4d084d701f03473daaaf99a76e785966
ACR-772511ab9f784650b8449484ddd02f9a
ACR-7971832a2eec42cfa8b93a3cf89cda9f
ACR-439e0db9adf54db3bea61cb9a8b686ee
ACR-d1568692984c4f9185bdfa47653356fa
ACR-3a90d0c8a1a64f4bbe6c1ba0423a12eb
ACR-c402b088dd7e4666a280b4e4e44475ce
ACR-d4d1d35c35504d26bdea4529d82a891f
ACR-b4fea5964f27444d9d6146c8937a4196
ACR-0367c00d55e14d0fa272076deac1cca2
ACR-28e057f783fb4ce9a9f2c4982bc65615
ACR-dc2e3da2fe934a11ac8f429902347558
ACR-18aa6471827b4e25928153940bd36e3c
ACR-ef56ab5ac9ef4943a53e67b5f9d9443e
ACR-47817506ce0f49e09c047d22f79e5e28
ACR-a2d685b80de04d37a24bec85d6061906
 */
package org.sonarsource.sonarlint.core.commons.util.git;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.api.AssertionsForClassTypes;
import org.eclipse.jgit.util.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.jgit.util.FileUtils.RECURSIVE;
import static org.junit.jupiter.api.condition.OS.WINDOWS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class NativeGitLocatorTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();
  private static NativeGitLocator underTest;
  @TempDir
  private Path projectDirPath;

  @BeforeEach
  void prepare() {
    underTest = spy(new NativeGitLocator());
  }

  @AfterEach
  void cleanup() throws IOException {
    FileUtils.delete(projectDirPath.toFile(), RECURSIVE);
  }

  @Test
  void shouldConsiderNativeGitNotAvailableOnNull() {
    doReturn(Optional.empty()).when(underTest).getGitExecutable();

    assertThat(underTest.getNativeGitExecutable()).isEmpty();
  }

  @EnabledOnOs(WINDOWS)
  @ParameterizedTest
  @MethodSource("gitLocations")
  void should_return_first_git_location(TestData testData, Optional<String> expectedLocation) {
    var location = NativeGitLocator.locateGitOnWindows(testData.whereToolResult, testData.lines());

    AssertionsForClassTypes.assertThat(location).isEqualTo(expectedLocation);
  }

  private static Stream<Arguments> gitLocations() {
    return Stream.of(
      Arguments.of(result(0, ""), Optional.empty()),
      Arguments.of(result(1, "invalid location"), Optional.empty()),
      Arguments.of(result(0, "C:\\Program Files\\Git\\bin\\git.exe"), Optional.of("C:\\Program Files\\Git\\bin\\git.exe")),
      Arguments.of(result(0, "C:\\Users\\user.name\\AppData\\Local\\Programs\\Git\\cmd\\git.exe" + System.lineSeparator() +
                             "C:\\Users\\user.name\\AppData\\Local\\Programs\\Git\\mingw64\\bin\\git.exe"), Optional.of("C:\\Users\\user.name\\AppData\\Local\\Programs\\Git\\cmd\\git.exe")));
  }

  private static TestData result(int code, String output) {
    return new TestData(new ProcessWrapperFactory.ProcessExecutionResult(code), output);
  }

  private record TestData(ProcessWrapperFactory.ProcessExecutionResult whereToolResult, String lines) {
  }
}
