/*
ACR-8eaa502bf91c4183b12229628c3a2523
ACR-03a4fc486dd54eecaef09e7605bde47c
ACR-8f391734a5be411e82e0ec2deed8b447
ACR-e7279d9d673d410e95cdeb881d83f4c6
ACR-3f61cfbf8d594ef9a1a1e0014eebe5ea
ACR-306939ceb8774dc89a4981c938c673f0
ACR-11a68c7939b84724bbf8ee744cefbd46
ACR-d6332a7146054ee8b4bb8a431b7f06c9
ACR-7df8a539f4894d7bac892c5407ef8316
ACR-dcbec2fcd4a244569ae78d73586f89d7
ACR-d893b3d8a68640858f9712e69856c32b
ACR-1c36ccdfff79425688158367564364aa
ACR-78253ff5b3df437cbac11306640bba03
ACR-a33b24ba674541a3af375dbb7672e1e8
ACR-439af15cf6ae4fc99ea9f73b6b101cf5
ACR-26eaf8e744d94500b4c4890ea1ca950d
ACR-a569219d4a0543948a6f7222d3077106
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.util.stream.StreamSupport;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem.Index;
import org.sonar.api.batch.fs.InputFile;

/*ACR-9ad366a2975542078ae93a65909d0122
ACR-20f18709256d45c490032035b157b4c4
ACR-f3c73a998c1645ef907c6d80f6501f21
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
