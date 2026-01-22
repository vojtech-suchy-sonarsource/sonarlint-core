/*
ACR-6bf2bfebf5b142bdb27acd1585aea1d9
ACR-a8e57338db344e65abd9b5862d931586
ACR-dba259e2ee4d4eda8099f68c9cd89a27
ACR-0017f53395da473493c9239f9539fcbe
ACR-0a229e12362a4bb989ba240ef61f1cbc
ACR-894aa7e725504abab7cf620a16513479
ACR-649a3302341646f5906831361cc83c18
ACR-493d48e2fdec40df922a1585cb8fca84
ACR-b04179b29d6a470e8882a806e8fac3eb
ACR-d19c62c9785448eea26c11d819ff706d
ACR-97f7e0ed09f444208547b15ad0013ef0
ACR-d7e787b0464f45a99b64854e6b8e9aa3
ACR-1160968a448848a29504bc80a4d5c61f
ACR-794c7ac96a47486881f7196b4398fa2d
ACR-dd4df0cfb36c49cdbce9fc334e048214
ACR-7aee4d10c5f24388967617a443955492
ACR-62e2ff75be244195b15f3d9cec675eb7
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
