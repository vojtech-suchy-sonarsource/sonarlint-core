/*
ACR-a056ac269ad94dfca6cf8ae3fb36ccd1
ACR-e5c2e2f0e1e2492e8257c1268526ba71
ACR-99bbefba0e2242339556829258426c96
ACR-8920b7e484c34abba4f602bd3f79da23
ACR-590e923a5b67486fb5ccf1c1cbdd206f
ACR-1e4df35ba3424a9398a9e8e2388546dd
ACR-508f0e8e0966422bb0e2a3c6e9e627ee
ACR-31507990a2d848b8b273220bdf1e7a35
ACR-a222c15e17c34fd9bcb84fbfdd094103
ACR-f7255d3c474a4c0687b4de51437f817b
ACR-ce7d1af9f9b0489da078bf29849b337d
ACR-386b86b26dd448459ab6a41f880ad8b6
ACR-e5283adcdac442658e4ee76be75b0499
ACR-5f723e74d7294e25bf35bfcce8df066b
ACR-5635bc6168fb4654a3648ca4a1eceec9
ACR-560767774a86479a87254364147e3366
ACR-e46877714d9541f682564af81016287c
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi.noop;

import org.junit.jupiter.api.Test;

 class NoOpNewMeasureTests {
  @Test
   void test() {
    new NoOpNewMeasure<>()
      .on(null)
      .forMetric(null)
      .withValue(null)
      .save();
  }
}
