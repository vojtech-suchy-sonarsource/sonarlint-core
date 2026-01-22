/*
ACR-a41c19b122d4486fb4347201367490c7
ACR-4b2d4953df9d4b349a716d03d1283356
ACR-ee36133570d347a386316bd6a4804590
ACR-8e9489d6e1b142029366c84dd5545430
ACR-bbddb16e8dbb4e35b05bc06c7937ac17
ACR-ee9aa94ed58e4f4abd17d79d0c870791
ACR-55e10593e42b4d56a78ade9b70b3b99b
ACR-08f91cdd3f3a4b5a9ff6e08b1df337c3
ACR-fd499b3e3f0948e794201b6726d797af
ACR-ef2ccf51443047a79bda873ebe416fd6
ACR-f8b574522cbb4ccc9ffe52fd0282b8cd
ACR-8aafaf32843e42e0915cc06fb94986e6
ACR-b976fe9b25d840698008b355da7b4c05
ACR-81e85b333df449748f6eeca3fccfba28
ACR-e5c183ebae0d4eedbf5d949a0364b2c5
ACR-d2654970ae0a42beaa67d8cf103621fb
ACR-92a0005ceccf4e9bb843b9587e3437ee
 */
package org.sonarsource.sonarlint.core.rule.extractor;

import java.io.File;
import javax.annotation.Nullable;
import org.sonar.api.utils.TempFolder;

public class NoopTempFolder implements TempFolder {

  @Override
  public File newDir() {
    throw new UnsupportedOperationException("newDir");
  }

  @Override
  public File newDir(String name) {
    throw new UnsupportedOperationException("newDir");
  }

  @Override
  public File newFile() {
    throw new UnsupportedOperationException("newFile");
  }

  @Override
  public File newFile(@Nullable String prefix, @Nullable String suffix) {
    throw new UnsupportedOperationException("newFile");
  }

}
