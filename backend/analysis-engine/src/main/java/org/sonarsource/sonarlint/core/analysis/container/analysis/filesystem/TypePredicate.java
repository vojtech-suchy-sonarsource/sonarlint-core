/*
ACR-d7acd8df44314c348a1136a6f53dc452
ACR-ad19e7c1752742bc97bd3628733ceaf3
ACR-131e32b27b5e4977b2e3524056bacc1a
ACR-7b437c26497a4d23a177ef1ed5f99d4c
ACR-b490aca23d5348af9a363448c58fed37
ACR-cf9a801fa0774ced8a24638853274cc9
ACR-408f2bb43ccf47379d1e3383a5d2e455
ACR-067c0ba32a094c5ea2cd970bef3342a1
ACR-4d9be3bd37c443b595fbd8f39177b992
ACR-8c6ccd5071c840299aa14ca97ca1899c
ACR-538db4cf07644a0c91c09f2a18c24027
ACR-972a1da869c942b0b8c833f1d6cfbedc
ACR-91f7c941533e4678abf84742dac5b7cd
ACR-b69fe8b4f84b4a59aa6b5fbe0c367c30
ACR-68be45f09fd5461a92a81149b745644d
ACR-744570689e5348abbef9699d667005fc
ACR-f7896c6215c4478c9f525820280ad174
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import org.sonar.api.batch.fs.InputFile;

/*ACR-5289d69a4f244924811173867693863f
ACR-d49a79a9c7a3485c9aa0e674b4d172f5
 */
class TypePredicate extends AbstractFilePredicate {

  private final InputFile.Type type;

  TypePredicate(InputFile.Type type) {
    this.type = type;
  }

  @Override
  public boolean apply(InputFile f) {
    return type == f.type();
  }

}
