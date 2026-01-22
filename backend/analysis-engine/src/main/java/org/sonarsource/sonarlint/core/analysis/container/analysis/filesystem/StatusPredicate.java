/*
ACR-e208e268c304466fa39d50187061e442
ACR-f906e6993d2746d6a46392d8e1bc2037
ACR-263bc6ffdf3f4d61802ef0373b07df1c
ACR-d36f717d63194b978d38dfe616287412
ACR-fdaa99d9fbe441dfa7898a1406754d59
ACR-23d19a9fe0e64adabbdf2ba4de878d3c
ACR-f02fdfb6c96342c7a8bbe2f8205b3001
ACR-8283e4920a344457a7b8df3ec744dd9f
ACR-177bd7a166ab4903a2c00b4f867d393b
ACR-5fd2fdcfca8543cf8e2b13ddb2a72836
ACR-75d91a4c3a78436881e720dbaf19f42a
ACR-1be497ac926d43a78acc84d34fec8796
ACR-6ee2fdda15b54061ae09fd1cbc6a1aa1
ACR-9ca40f3bfc8d45f890600f54455871ef
ACR-e8c00b9e138e45fd82231c3ee89972cd
ACR-d325be4761fd492fb9967f30efdd9359
ACR-99e615cbaf894a9896ba536d85b2d234
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
