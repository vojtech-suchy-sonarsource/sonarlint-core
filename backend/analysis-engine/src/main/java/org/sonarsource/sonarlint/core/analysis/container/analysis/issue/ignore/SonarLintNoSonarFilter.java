/*
ACR-2c628ed8faa24777a395f8c451c1c691
ACR-d168b84cf5ad46a081a63f57350b608e
ACR-f20eb3f8308541978e9dcdd362e12e66
ACR-14cb1250777a47e6bffc06ac53848b5d
ACR-7ac3ff312a5a4ef4aac49953d1f470a3
ACR-86a366d1705a4fbb9e20f3d21960f80a
ACR-4710f715899c40ebb079f7b762e1bd8d
ACR-10bfd55de5234d5aa0e4c359edea81e2
ACR-2510350e14944d56b6c18974761db054
ACR-36bed4c2e1fc4f8392a39a9a423f8fee
ACR-739742e2525a4c0c8e180fa540e22ada
ACR-06fd08dd60464c0ca0097063d12b77ab
ACR-87f63bb5e80c4647b30993624ffbff58
ACR-95bf9dba81b44da2893f29dfb7c4abd2
ACR-b3b13b21cf2f405187a02fd1f4c57a47
ACR-b5b0f1fb07df43cf9f14d8d97175da33
ACR-4db44dca3a5049e78ee75162f15838e0
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
