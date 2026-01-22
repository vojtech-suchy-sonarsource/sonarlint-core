/*
ACR-ab8308944b294a73940517d29b16edd9
ACR-260d130913b24419882bf7f4df93addf
ACR-899704bb083b46ae8ba592b13f34b26f
ACR-760e4d98443048d2848c84a6851cffa7
ACR-216c797176694b5ab45314e9ef3d5082
ACR-9c1aee247d0748d1bfa481a7de3adbd3
ACR-60b9fd7eb497464287a0ce1b27291662
ACR-e2b555a6310c4dbb8c8e444a85c87589
ACR-0bdcf545134e43038518e45003c98ba7
ACR-1c57bf19f72844cb8ec9dc71a5140031
ACR-77c7c1bde56b4cb09ddfe2800fb32b37
ACR-8e0fb662970e444792fa81a8b8d00fc2
ACR-d1e7dab2a55640edacae82f02c2fa39e
ACR-9c51c5c7cf1e4c13ad90051699e90f3d
ACR-7901773e481247aaa57509fe937b2fa8
ACR-55a1a6ac622b40b69ddda7ac988e8021
ACR-9e20e28c4b274f3dbfbd8e856747da21
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
