/*
ACR-5d6a7f0c0e9a4489aab668bb417885ea
ACR-71b8a218c72142ed9bccfc83e7b099a3
ACR-d7c2607232a0426aa25117599bd7addd
ACR-7cf4835ed3144fbea3b5c0705695b846
ACR-91f0f30924b0438e8bf84bbe1ec77a40
ACR-b6b8de1e0de645c497f0c43514c44b4c
ACR-bc88c61169a740eb8c97db2ee38d21a7
ACR-07b193d2d5a54b4da8a51d58e62ec4c2
ACR-053ffdb4188a44e28bb8b0d6593c597c
ACR-d63fea1b171541ad980c35d0da8ed5b0
ACR-929d1969c0ea46e78fa18e4e6eba4fe9
ACR-798567114505446db13050e5af171fd3
ACR-e31aa6eef1c348009549dfbb771b2a74
ACR-5981b79e57f74776807d76c92dfdbc37
ACR-de5b42d6c6354a9aa65f8ada49004e03
ACR-cce197b15277464689c5975eba2cd184
ACR-587d5435408a4f47a6188e306434e0ae
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import javax.annotation.Nullable;
import org.sonar.api.batch.fs.InputFile;

public class StatusPredicate extends AbstractFilePredicate {

  private final InputFile.Status status;

  StatusPredicate(@Nullable InputFile.Status status) {
    this.status = status;
  }

  @Override
  public boolean apply(InputFile f) {
    return status == null || status == f.status();
  }

}
