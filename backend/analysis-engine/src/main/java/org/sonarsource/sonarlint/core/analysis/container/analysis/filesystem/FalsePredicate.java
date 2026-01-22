/*
ACR-fc85b531f12a41ceb81a13adf204c47d
ACR-400352e02bee4712bd7edb07f1b4cc21
ACR-4f29a2dea49d4b93a0be468ab4fe54d4
ACR-1a6b9c06ff7b42d0a06e185e70ef1e63
ACR-24ce60cf88984874b673b221f1525084
ACR-3796f9eddca547fca8d1b01f4113a3f8
ACR-6055251593e84fe7a28cf82af436c002
ACR-9a977da16da84944bf6f8d8af7df231d
ACR-ec1267a7c3894b21b9accc4a39c41e84
ACR-8463359b8a9c4cdd884dfb7a95506352
ACR-1a3347fbf9974f25b5dc2d74f34ac2c4
ACR-01f7d551f9dc4c3ea68a6cd3f7864ec4
ACR-055c41c790b643bcb85040a101ab1227
ACR-37a2ac875a5b4260a0541783f995001b
ACR-9eba65868fe24bc58b1f04a224dfde90
ACR-9bfef7bfd28144a08e77ca52f92154e8
ACR-08ca1b4492514c76b5fd03b355cea1ba
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.util.Collections;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem.Index;
import org.sonar.api.batch.fs.InputFile;

class FalsePredicate extends AbstractFilePredicate {

  static final FilePredicate FALSE = new FalsePredicate();

  @Override
  public boolean apply(InputFile inputFile) {
    return false;
  }

  @Override
  public Iterable<InputFile> filter(Iterable<InputFile> target) {
    return Collections.emptyList();
  }

  @Override
  public Iterable<InputFile> get(Index index) {
    return Collections.emptyList();
  }
}
