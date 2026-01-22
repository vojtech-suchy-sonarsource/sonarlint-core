/*
ACR-825a2a90c24b4bae9a7a840ccd66d8b3
ACR-c67c8f058b874a29a42a5c1ab7c7ed36
ACR-709d937bf8a64d69ad7c5f4ee3b56eef
ACR-35d0cd373e214eda8607f0d1fc6ddc1f
ACR-8b0291f6a0fa4715a993908444ea00fa
ACR-b08376f61f5c43a2a0544be94c97db5e
ACR-5c0a639675de4c478c9990837676657d
ACR-e6f231d6126e4bcd80e837d8df8c3fac
ACR-695da95c8f35477d926bae7686d93cfc
ACR-8740088076aa4121a2d3d290d854b615
ACR-d05125c1b8d54a5eb926f11ae76904ed
ACR-a31a428938e8481fa7dab73689fcf2a5
ACR-aa7ddb742dbd40c79ae6778219ec0ce7
ACR-f37a5a054cdc43f486b0285f1598df40
ACR-3740ef504b7c4fb7a9d4ff3d8b088de4
ACR-6a0c05b9abd0417abc934ec2baf04dfc
ACR-63d0fc90a40d4fe9a931d3a2ebff34b0
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi.noop;

import org.junit.jupiter.api.Test;

 class NoOpNewCpdTokensTests {

  @Test
   void improve_coverage() {
    new NoOpNewCpdTokens()
      .onFile(null)
      .addToken(null, null)
      .addToken(0, 0, 0, 0, null)
      .save();
  }

}
