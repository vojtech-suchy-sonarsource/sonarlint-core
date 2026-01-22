/*
ACR-73d64232224e4559a0a276d8bf28148e
ACR-699b29212a164ebbb048ae853c56b243
ACR-b58174d6326f4cbcb732fe2a89ec9ef0
ACR-130f8e587d9a4054b479eaeb4a81d386
ACR-cf08cab782d64c1caf99847340c5dac7
ACR-0f36d24e9aef4bc2a7bfb99074ec38d4
ACR-46dcb4579334410c9092a6f59afcc5f4
ACR-afcc8f59a8be49a8a47bc64a95657ff7
ACR-a6e5f302c5d64e96a1a4a93fd322b88b
ACR-86057b3021d24f99aa7e2eafb6d59cac
ACR-1c37048b1b3f47fbb27b189d46430849
ACR-b21fa35120da404f954082ee289f2e3b
ACR-8595dd528632469e85b181e9939464aa
ACR-4627117f77304049a9c936a3e476cfed
ACR-8e5c078affa54466b9a2b23aef30bb18
ACR-ad107ec52c2f48a197331db5ff2660dd
ACR-619d1734f68144a89dbb2da9967911db
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.net.URI;
import org.sonar.api.batch.fs.InputFile;

class URIPredicate extends AbstractFilePredicate {

  private final URI uri;

  URIPredicate(URI uri) {
    this.uri = uri;
  }

  @Override
  public boolean apply(InputFile f) {
    return uri.equals(f.uri());
  }
}
