/*
ACR-532abc21e2fe41c4906359a34aa3e4b7
ACR-5d1de61e461e4a8eaf5ee67e095c5074
ACR-fda45c9bf0e1496997c8154d85cffe97
ACR-5926b16b3d444f3a9905618cb476fe77
ACR-55dadb4b1ee749d1a51bbf2950c1c182
ACR-994d11e2fbab471cbc706233821f4957
ACR-3a414684f5ea43a0a08a323cba14a80a
ACR-0beabc7357084ee385c67a90676df20a
ACR-b9cad0aa92f443e7a47862a7287355f0
ACR-4fa32a37b4f340e895bde54b900cda4d
ACR-6f5ea220af7148e59bb2bbe5864b06e8
ACR-b45663558a504d7e9802d1a33b98e5ff
ACR-a41345222fa941d0987d9a2e9754b8cb
ACR-c1b9331ee0b54b7a9fc9d12ed3b9ac37
ACR-9f85c0a59d8e425f8df25ae36414f348
ACR-2f78c905bc4d4a93b25f8a868fe03042
ACR-6a0965629eb84f019b4487eb71a362c9
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.util.stream.StreamSupport;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem.Index;
import org.sonar.api.batch.fs.InputFile;

/*ACR-02a165c447074391a95efecf937ca53c
ACR-7a1a67987675413b915871a8ff8f3645
ACR-bfa15e9837c149b2bc437c377de78a99
 */
public abstract class AbstractFilePredicate implements OptimizedFilePredicate {

  protected static final int DEFAULT_PRIORITY = 10;

  @Override
  public Iterable<InputFile> filter(Iterable<InputFile> target) {
    return StreamSupport.stream(target.spliterator(), false).filter(AbstractFilePredicate.this::apply).toList();
  }

  @Override
  public Iterable<InputFile> get(Index index) {
    return filter(index.inputFiles());
  }

  @Override
  public int priority() {
    return DEFAULT_PRIORITY;
  }

  @Override
  public final int compareTo(OptimizedFilePredicate o) {
    return o.priority() - priority();
  }

}
