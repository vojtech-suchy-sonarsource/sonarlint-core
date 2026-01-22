/*
ACR-89a4f66752944b2f94119fd4755390bb
ACR-9b76eee546024f1999745b6cb27c4a3a
ACR-c5335ea0d9da4f11ba8dbbc9ff0eb3e3
ACR-c6d5bf800126412390b2921413d46ef2
ACR-15cf04dbc8954f77aa547ed75023f215
ACR-b3eddb38d5784a8e81174873396fc243
ACR-151a2cf893914fc6a277ecea16b7a3f3
ACR-34fb14232b6f40b29413a21f93f55e1e
ACR-07b59fb3e2f14675b7033cfe16084f1a
ACR-6310188e680048e38e427bc71433dd8c
ACR-b4d86b9f61c841d596bce14eca2d1b1a
ACR-269b78c5967f468dbe7a43d5157ebe46
ACR-80c03b45015540928b6ca33e30e7a801
ACR-a41840fb36214f5ea40ea70c490743e4
ACR-0490187583aa4f1882555c1107c282ff
ACR-80f61e2179454f43a8d1391e426641e8
ACR-3c2e7f3fdcb5434ba367eb50adb8476b
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
