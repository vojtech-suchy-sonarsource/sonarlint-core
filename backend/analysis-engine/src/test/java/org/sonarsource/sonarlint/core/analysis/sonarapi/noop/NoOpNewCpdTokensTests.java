/*
ACR-5375e0abb1724a188f0bb254f64603e7
ACR-34d8399d727c4553b234acb609236128
ACR-ca4034c5017e43adb5d30b90a299e3c0
ACR-c73926a919a746d98415b9decf3e0d21
ACR-4f568c02304c4d4fa509be0f1ce7af5c
ACR-3b8b7178d8fd48b19392d06e2d67dbd0
ACR-d2edb1f8575941d59a0269b35c247c3b
ACR-4714cafe28084f14b060cd8dc06679ae
ACR-2488c3eeea384aacbab76e8e9d7a8b47
ACR-81f75af5586641fb83a453d8cbf5ca79
ACR-b933a68f970045e386fc7cff4aec70c9
ACR-46624aff106a47b6bab6e79345bc6b64
ACR-783e34cf62b84ffe867190a2f1d6e195
ACR-71f48e9ccca440a08eb0222c1e6d3c7a
ACR-74f1758212e34c788bac50aaa4e67600
ACR-c08ea7a3f26645c491a29bff2ca8fa22
ACR-caf8b776c55d4b0fa55fe6621ae263e1
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
