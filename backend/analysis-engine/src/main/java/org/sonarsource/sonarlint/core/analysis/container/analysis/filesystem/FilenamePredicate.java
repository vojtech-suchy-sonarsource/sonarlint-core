/*
ACR-8c37673da20548df817e7fe1807512a1
ACR-5400913f05394dc9b9aef95dbc945262
ACR-b6fc33a0efec4016bb6c6acc2460ea35
ACR-c4a9afcd1a2440c488c55ead67229745
ACR-230b958ba0664e0aace2e4fa8d4c573e
ACR-9470f7ff4f564ba2af2e3f6a45b8f06f
ACR-c1ce48f59d8845649d25ec07d9134b8c
ACR-3bd1d551cb1045eb8b9d130bfb627319
ACR-38e00ff71eea4fb7b4da8a2e995adc4a
ACR-05f5dab58da04e71991f9b058e127869
ACR-5a15a103b655492682b1f849b1c444f9
ACR-57af3f522a534b0ba63384f10d6dd0f8
ACR-8930391fd2834599958619aaf06fac56
ACR-1575a3821f564c8c924fc5005a3889eb
ACR-1adfb8673d434ba5be4f867a111f6b51
ACR-ecad3aee4cab48b1b8916c03548934bc
ACR-578df28f0b53467fb257dc9a15ec0736
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
