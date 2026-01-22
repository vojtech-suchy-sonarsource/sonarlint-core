/*
ACR-b0a542e6cf6c4251826c11c07525b4da
ACR-4dc300d6813a4033ae75889434ae2f30
ACR-f2f4b17a8db845368088dcae6ec39a8a
ACR-67ed450e8ff644138bc7f7c386a72010
ACR-ef4cfb5e4ebd4ce2a64878a897d634ba
ACR-36ada1a2141e4f9e8ef7704671d226b1
ACR-36aaaa2c9a61440f847f633e706340b0
ACR-32aa9743b0d54f1f9efaea8a9f0bdf23
ACR-322fc597d598482da206a041978e4c7e
ACR-9b83f090049d4a70883b99103ca647e9
ACR-11a98b49dfe5465a98453159e4354f51
ACR-ab4ba2c32d48440f94e2458ec1f9dd70
ACR-dd622b8e296d424291a4a4c58ae0fdc5
ACR-9643ca68fa194fb1824d2917348e1a1a
ACR-bb2c8e39266e483ab9d80d265be8a927
ACR-9ca05eb51fe64ce080268fb14980f8be
ACR-99e3de61460a49ef80a0dd2a88f61f2e
 */
package org.sonarsource.sonarlint.core.commons.util.git;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import org.apache.commons.lang3.SystemUtils;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

public class NativeGitLocator {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  //ACR-8eb5043536504785b1ee2eed95f16595
  private boolean checkedForNativeGitExecutable = false;
  private NativeGit nativeGitExecutable = null;

  /*ACR-56dd0c36afc3454c9380311f2804f77b
ACR-089a1786eec448c0892cca78bd3174d3
ACR-c6cd6d491c2d4897a5234737f40edc59
   */
  public Optional<NativeGit> getNativeGitExecutable() {
    if (checkedForNativeGitExecutable) {
      return Optional.ofNullable(nativeGitExecutable);
    }

    var nativeGit = getGitExecutable()
      .map(NativeGit::new)
      .filter(NativeGit::isSupportedVersion);
    checkedForNativeGitExecutable = true;
    nativeGitExecutable = nativeGit.orElse(null);
    return nativeGit;
  }

  Optional<String> getGitExecutable() {
    return SystemUtils.IS_OS_WINDOWS ? locateGitOnWindows() : Optional.of("git");
  }

  private static Optional<String> locateGitOnWindows() {
    var lines = new ArrayList<String>();
    var result = callWhereTool(lines::add);
    return locateGitOnWindows(result, String.join("\n", lines));
  }

  static Optional<String> locateGitOnWindows(ProcessWrapperFactory.ProcessExecutionResult result, String lines) {
    //ACR-8d48dcb82a384e14ab54761134e6b02c
    //ACR-8deb7f134a5648e7b1a9185899ef4bd2

    if (result.exitCode() == 0 && lines.contains("git.exe")) {
      var out = Arrays.stream(lines.split(System.lineSeparator())).map(String::trim).findFirst();
      LOG.debug("Found git.exe at {}", out);
      return out;
    }
    LOG.debug("git.exe not found in PATH. PATH value was: " + System.getProperty("PATH"));
    return Optional.empty();
  }

  private static ProcessWrapperFactory.ProcessExecutionResult callWhereTool(Consumer<String> lineConsumer) {
    LOG.debug("Looking for git command in the PATH using where.exe (Windows)");
    return new ProcessWrapperFactory()
      .create(null, lineConsumer, "C:\\Windows\\System32\\where.exe", "$PATH:git.exe")
      .execute();
  }
}
