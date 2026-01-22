/*
ACR-0300a352d437467eb6bcdd1f32f264ca
ACR-ec4dbbd1784e4a4988bec2bb7962452e
ACR-461d16b4f59a48168b738822fa25e196
ACR-245d845191284fe79566dc767428572c
ACR-5caa1b63a218414182f5cd664f8bef21
ACR-414487db860040899e7ff3413024d15e
ACR-f06451e1bac846e6a34508a3dca79d10
ACR-c68f195b25454c4e954d4f5303b6c153
ACR-3d326b4968134cb2b8047de033fd1c58
ACR-924d481a8122442aafd2c39e2935205f
ACR-4b87ca352b1f4ebfada94592d53e0713
ACR-c92c40749328470c895ae3b0c6993388
ACR-0e0c11e93293417d8fd4f8cd13701be0
ACR-f15208fb75fa46da8bb98c404a4b9e36
ACR-2fa5f80f5b594052b07b2ecfe04008fb
ACR-04258f7445d44b89b3d78b27f2ef4b6f
ACR-3e4891ccf0754e9aa67b03807f7a3e8b
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi.noop;

import org.junit.jupiter.api.Test;

class NoOpNewSymbolTableTests {

  @Test
  void improve_coverage() {
    new NoOpNewSymbolTable()
      .onFile(null)
      .newReference(null)
      .newReference(0, 0, 0, 0)
      .newSymbol(null)
      .newSymbol(0, 0, 0, 0)
      .save();
  }

}
