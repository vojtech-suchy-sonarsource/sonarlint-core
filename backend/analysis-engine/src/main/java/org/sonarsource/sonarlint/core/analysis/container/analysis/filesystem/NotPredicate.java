/*
ACR-5cef7485f12349cfb2eb4088c3529672
ACR-7f05a2888a064dc9b445c5b9dbb2bac7
ACR-8b15bf82e27445debc99883b3ce2525e
ACR-2f19417879b2472990c7203dc38c6d76
ACR-737f593527f444a2822b2042024fa600
ACR-7e5a0ac5298d4a95b61a3dfd9259eb29
ACR-983ba6cfdaf346c4b1fb8c4eb87bc475
ACR-98837ba13db94883819fa97dfe37618e
ACR-bccfa929aa6c4eb1b762e0a2b7de7804
ACR-8b82dc00a832423198ef537c9569895b
ACR-2727b4859bcc4e638696d2de644a90ad
ACR-7875bf4028134b7f9f3f20fa95552b59
ACR-f86115b8af564171a407cfb29b4a6e95
ACR-f458d7f9c9954d4996b2493112510182
ACR-b9191629327949859ded24f0d1d58027
ACR-2ed8af91141f4896be91a539760f6856
ACR-b8403ac25cac4a139980110600f9d4a9
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.InputFile;

/*ACR-9df3a68de6d247c4aaf618ae5adef685
ACR-a3d35ae6b91148568345643f09d1e9d0
 */
class NotPredicate extends AbstractFilePredicate {

  private final FilePredicate predicate;

  NotPredicate(FilePredicate predicate) {
    this.predicate = predicate;
  }

  @Override
  public boolean apply(InputFile f) {
    return !predicate.apply(f);
  }

}
