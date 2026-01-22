/*
ACR-71ab3ef7b6cd48739c6d576058600601
ACR-644ab7ffaa9041e4a64be3b94f8634a4
ACR-da5f80369c3b4ea6924ae7149359d67d
ACR-0cf5b27f1a7048fda94093a5d2ebe312
ACR-0b1abb7717fb46b1b99c4976db41aa51
ACR-85a9bf3001274badbf18943a8dcfb885
ACR-b00c15331b8647a9a7e87e43e97114c0
ACR-cadc57a760254d4d88f0203df58cb95d
ACR-81e59a4e6a254f2f85da6bfe5ba39297
ACR-8186709b0f1b4d9b8f0fcad9efc5da88
ACR-8a6797f75b3e4f8ea09c56d8110a22d3
ACR-a875c57642794f769b24810cff5e4a06
ACR-6f3615240a2b485c9d4580aeeac763cf
ACR-9a1a57b6075c4e8ebcafe505a33d8ee3
ACR-ee225eef04c04fb98fe4e445b8769f88
ACR-e1a29ffdef4c4e609d6879367e5e4fd2
ACR-af8462a7b096414ca713a99d4020c4b2
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
  //ACR-0bdb06588a09465b917986c60b05429b
  private boolean checkedForNativeGitExecutable = false;
  private NativeGit nativeGitExecutable = null;

  /*ACR-f4d9e68ec51e4ee4801a009f286ab8fe
ACR-1fbaf383673a461fb9ffd6fbb077fd87
ACR-e4792bbeb25b4d8fbd142ebbfeab82db
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
    //ACR-b108e55ee1674d45889edc6119cb62cf
    //ACR-7d892e7b828b437f9e671b5fe5bf5486

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
