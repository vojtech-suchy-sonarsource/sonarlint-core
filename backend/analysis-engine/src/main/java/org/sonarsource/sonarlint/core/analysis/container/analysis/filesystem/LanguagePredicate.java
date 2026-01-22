/*
ACR-f4fc47e6b8c9441f8c3d9a8f870b4a09
ACR-4ecd31c201f44004a0fd13d48aadd5e4
ACR-0d4bdb19da854ceea828857372ddac66
ACR-c39bf16685fe49e6b810d00a43335255
ACR-54acc86c366d41b1bf8d0cd6fd09d153
ACR-6d9a0506b19444aabe400fc6b76b6557
ACR-eb326c16cc0d441c9e3c87c98fbe19e3
ACR-252cb7fcd4b54e7680be92a650254ac7
ACR-0e2d5378fbad42ba8d1c72b61ccb3420
ACR-0c998c5ce8d74d759eb90b4e6c2d8064
ACR-2ef94a7cd9284940adb07c0273fb4227
ACR-de1d6a4b76bb4357ac7ee3be28bf112c
ACR-f9a5dffd70b04516b36e99ed3f0dfa25
ACR-0852b72adf664e4f89bf28d6c72a963b
ACR-f29adfc0e52b4287be454f03d6fdfcee
ACR-ff149e3a66a74b63977e8c299112a6db
ACR-95b336c8e8bd494eb538ba5b17f25265
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import org.sonar.api.batch.fs.InputFile;

/*ACR-f68578aa1785448388e2f3dc7358801a
ACR-132996c75ee342f198e76ce1bbb5ac88
 */
class LanguagePredicate extends AbstractFilePredicate {
  private final String language;

  LanguagePredicate(String language) {
    this.language = language;
  }

  @Override
  public boolean apply(InputFile f) {
    return language.equals(f.language());
  }
}
