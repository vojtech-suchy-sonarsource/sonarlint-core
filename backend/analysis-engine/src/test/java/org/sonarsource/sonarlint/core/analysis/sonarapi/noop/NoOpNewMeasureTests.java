/*
ACR-b96e26b3f3fc40e78612374c411c21b0
ACR-fb159226f22a4c7eaf8fa1a0b751e9d3
ACR-636c3984396f4707bab5a241a2627c6a
ACR-220e3651ad224437897689c36d968211
ACR-aa7ba7df2a824bd58b7e646255cf5123
ACR-eb912af947ce4e0c9939fed628768ab3
ACR-8c53551885d445758b3378bb2c29e20f
ACR-36280bf133ca452aab9860f93e1da325
ACR-c640226af4e442809c4d0a6a54dac6fe
ACR-282243cfb2b244009855361679e8fb88
ACR-6b842b2c96014976a037f21f086e90ea
ACR-06a386cc551f42fa8da44818488ed3a9
ACR-75de0eb79b8f49f3b4270cb9be974dfe
ACR-11402a0cbca04c1083ef50e66e9204c2
ACR-843ef14231cc48279f5fa8e924a80063
ACR-f44ffae444f344239b0021b43bad7fa9
ACR-9691abf004e14d49b4c82786aa022443
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
