/*
ACR-cc7f2651b5dd42cca026aef63940cfd1
ACR-710fb5bff50947c3a24928f8aa8880aa
ACR-9024a99389474a4aa69ee12b5fcf1506
ACR-3e03ce35fd4846028c31427b2a9c3b05
ACR-a788f7513eda474ab0cef6db2bce5e82
ACR-519cd11fd3ec4ef38314d95f0986e795
ACR-0470f4f6dff248e49947a2889f870f9a
ACR-f54748cc4fdc4e359791338959acba10
ACR-a5027c05f7c44657b28a2502faf60c2b
ACR-2d7a81bebc1b4bdba1b1e42c4dcc5ff8
ACR-5efc7c4f9ef54c2cb9523d4a58e2eebd
ACR-5195a0a1ac2a43ce89bce4aa6157f48e
ACR-2dde091979284ba4a2e03e9dcc9c7265
ACR-bdb678cf3a324d3183463f5e10485ddc
ACR-9d6ed6e279d24e4ab79d0b223f377ebf
ACR-6e3909966f834934830d97dadcb9d648
ACR-de1af2a73d3e498b94528a0dc4a2e3b1
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
