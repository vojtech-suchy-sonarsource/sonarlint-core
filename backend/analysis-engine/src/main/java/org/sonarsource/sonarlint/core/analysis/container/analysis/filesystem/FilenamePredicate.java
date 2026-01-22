/*
ACR-073150b16edb46a68ff8ee70284255d5
ACR-5d0078f4ec1e4ea6aeb8e2dca02430b8
ACR-63457d3ff27e4e1ab8c9acbdcb3c8925
ACR-69b817475d814d4bb28c31c1c6903070
ACR-8cbdd02da2454e43bb9db8025be93a57
ACR-31631ee188a14ca0bbd00a9e1c8df989
ACR-2ebaade0f47f4f3e9cedc6ddd223b41b
ACR-cd149da7aeb347bca3d2b3c14a96e665
ACR-99c149ea129b4e79bd68251ad57a6856
ACR-95a1c435db474f17b9918207af94195b
ACR-8948d7ef24a04f0bb126b03cd11ff2a1
ACR-386cd622bfaf489f8cade95d8cb9ddad
ACR-db5c25f445c74538a547ef785eba9765
ACR-7eb5d41eef3e478386f924cd8c404cc0
ACR-1b8b7e23e3f14767b101fdae1a81cd76
ACR-2a2353f4d29644e387967bda52f5c9ff
ACR-93f30f430d3c45a992db4aa3e137d107
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;

public class FilenamePredicate extends AbstractFilePredicate {
  private final String filename;

  public FilenamePredicate(String filename) {
    this.filename = filename;
  }

  @Override
  public boolean apply(InputFile inputFile) {
    return filename.equals(inputFile.filename());
  }

  @Override
  public Iterable<InputFile> get(FileSystem.Index index) {
    return index.getFilesByName(filename);
  }

}
