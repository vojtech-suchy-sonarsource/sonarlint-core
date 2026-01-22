/*
ACR-4724f5577e31404280ba12beeb0d85a5
ACR-93f94050709d470e9a96c094830958a3
ACR-92f4d152c16242dc963bbc9d245c0da3
ACR-579af89dc515473b8f411daff49985b4
ACR-4df5c63be6c34ef080cdde1e6b383286
ACR-620aa49dd88242b0a849f00f9514fc81
ACR-9dc1571b4f1e4760a353efdb92a84320
ACR-6493ffde9f1346e896d4ee6ee4124196
ACR-a6170d0097034313a9a61428443effeb
ACR-1c665a31b1504bfe94d4d43be2a86e8d
ACR-e44f46c9da804139ac5de3d5ef8be11f
ACR-bcee50de024f4c67975afb1f9e7e2d38
ACR-1f4034c23d59488a9cf6a2e1b48a8cd4
ACR-084716c53afa4672847d6d85077f0072
ACR-e844da4ffcce45b2acba15475a3aa93d
ACR-a222c93c8cc345ca9cd4217226d4d454
ACR-53001beab2a14a09979cd1cdf15eec04
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore;

import java.util.Set;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.issue.NoSonarFilter;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.SonarLintInputFile;

public class SonarLintNoSonarFilter extends NoSonarFilter {

  @Override
  public NoSonarFilter noSonarInFile(InputFile inputFile, Set<Integer> noSonarLines) {
    ((SonarLintInputFile) inputFile).noSonarAt(noSonarLines);
    return this;
  }

}
