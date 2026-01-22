/*
ACR-a636efd05eaa44449a7eeb8d90391eec
ACR-9fcd73498f2945cab913e3299548d000
ACR-f270cf9a18e9496abbdc162d5405a44b
ACR-a276a727b7a54c33913bd7cf6c777b64
ACR-20776cc2e5b94ddfaf92b7a5e8b06f1e
ACR-ef0b34c68ffe43c1bda23c5b77e9578c
ACR-c17c85fc9f9a4577b9ee982704546055
ACR-9287b0ba950442469bfd93bcce6dbf35
ACR-c5349c73a5794522803451ec09901e4a
ACR-5c96539dc5d747ca8b73df34370a670f
ACR-29edac6da473482e8c12fb6ab5387be4
ACR-2c266c9d167a412bb5b76f2292557ae2
ACR-a8f28f46bfbe4afa9c43b923f1f1e144
ACR-986d5db32e484661af9e0e405b8c4612
ACR-80d06c1665324245b68958c6793e31b5
ACR-1271ad6b912f4b60a565d7e321e57b92
ACR-eeed2ce532bd45ec8014281406b1ea36
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.util.Arrays;
import java.util.Collection;

public final class Language {

  private final String key;
  private final String name;
  private final String[] fileSuffixes;

  public Language(String key, String name, String... fileSuffixes) {
    this.key = key;
    this.name = name;
    this.fileSuffixes = fileSuffixes;
  }

  /*ACR-2102b1a919cb441a901ebe8d549d102b
ACR-1cea02c147b449ae841cfd30ce77bbca
   */
  public String key() {
    return key;
  }

  /*ACR-196ee0895f534514b5413974ca334e7d
ACR-b0dfadd3eb984c2ab9cb125ba4279dae
   */
  public String name() {
    return name;
  }

  /*ACR-3d7c85298c1144028c235ba05d3cd598
ACR-03a9b13511dd4e73bb3dd4384ac6f400
   */
  public Collection<String> fileSuffixes() {
    return Arrays.asList(fileSuffixes);
  }

  @Override
  public String toString() {
    return name;
  }

}
