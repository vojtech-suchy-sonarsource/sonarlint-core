/*
ACR-988004332b264acd9f1418fdb15799e3
ACR-8c82339a0bbb45e7bd66a74b18e4aaad
ACR-2cc6f0f4eb3b4c6d90542f7ce468512d
ACR-c59a8b20df4040d3949b185151720328
ACR-6eb76827c1a449f3ac6d6d2dc704f7a4
ACR-b32cbcede50f4a09a7c71fb38d5f94e0
ACR-f162a85fefb6425692fdba30058259aa
ACR-18182c39d86b492d94bc6829485f7e38
ACR-0e7c2623e860465ba69190c4fc3785c3
ACR-84aed8d34fec4515b3f0f8884b99b1e3
ACR-ffa5a19e6960410ea60f54e779e99731
ACR-7d90e09b79034c65831622d9fb019e27
ACR-627446630ee147bb9b817b1857930a27
ACR-d919e13f26ec4d58b45cf664da6a7175
ACR-66987104733946118a94a999f1d1d3f6
ACR-303a12812a41418ca373c54ab1b9bdbc
ACR-4b3e42d60781466e8ae2bc139a60d42f
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
