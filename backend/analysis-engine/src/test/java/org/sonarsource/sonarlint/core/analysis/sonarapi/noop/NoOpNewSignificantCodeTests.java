/*
ACR-217f98aa28e34e34bbd1c00428e27f1f
ACR-cc1ab25807a74a1cbd6110bb4cbe7454
ACR-4f82622ff2284e19906cd2fbbe715748
ACR-b90bb4a5ed7c43928b5fd2699f1721a8
ACR-b165c865cb754de69f8fef5765c0e024
ACR-a3e319705d554af1a7794d5a811ae842
ACR-f2f8601209a24be9b4dc83d13e9f091a
ACR-3e4ffeb22bc740c3ba4d879db7372bfc
ACR-fa757a4ec5d146188cff73d147de4bd8
ACR-c92b834989764bb88cadd7d9e494c13a
ACR-89a42b0e4b9e4fbf9930c11cb2d9980e
ACR-a82bdf60a4e146ee952f01a82f81fad5
ACR-a28ca62644c342aaa08b813bd017f81f
ACR-1af659533bfa42cf917cc8aedf19932c
ACR-e2f4b83984614ac9a33349b04a7d1c19
ACR-e66e0057c7794c67861fe2c419850d33
ACR-742e8783fc0e4a9eb32a8e8360a8204d
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi.noop;

import org.junit.jupiter.api.Test;

 class NoOpNewSignificantCodeTests {
  @Test
   void visit_all_builder_fields() {
    new NoOpNewSignificantCode()
      .onFile(null)
      .addRange(null)
      .save();
  }
}
