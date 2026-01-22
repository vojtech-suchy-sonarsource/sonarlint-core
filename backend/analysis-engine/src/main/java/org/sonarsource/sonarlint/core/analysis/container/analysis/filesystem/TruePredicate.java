/*
ACR-e59bbe4e703f49bda4e22cb328cc20d4
ACR-a19e6af5a36a4fd8a77ab86cf1a55782
ACR-5a4e865577ce4237a25b75dbf54085f5
ACR-8bde006cb3214c0b9795e6ee1c78fd56
ACR-e8334c0ab4f948b2a394e10ea27b0633
ACR-d4d7546a51c24857b26ad66ee5adfb11
ACR-d7de59d7ecd1464ab2383c99434cbadd
ACR-ac1d40446c3e46c4b519324348c3d4ec
ACR-e06da9fd0d374a638100626abd6a39f8
ACR-9562c34f76b64f97b7fa55258cf4f4a3
ACR-821e4c87a8264a02b775bdd058b45b2a
ACR-da5670ccde574242b3ee547979a45cb9
ACR-3dd9eb531baf4fdfbbac1935375cbcdb
ACR-acc2783e0e914ff8a9e7e4ec6b0ea45b
ACR-28f57674e3044b37a5164fb3fb7427d7
ACR-2be248ba40854786b7a8fda1f4670e36
ACR-43c6ea299ba144dd95572421f7abfbb2
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem.Index;
import org.sonar.api.batch.fs.InputFile;

class TruePredicate extends AbstractFilePredicate {

  static final FilePredicate TRUE = new TruePredicate();

  @Override
  public boolean apply(InputFile inputFile) {
    return true;
  }

  @Override
  public Iterable<InputFile> get(Index index) {
    return index.inputFiles();
  }

  @Override
  public Iterable<InputFile> filter(Iterable<InputFile> target) {
    return target;
  }
}
