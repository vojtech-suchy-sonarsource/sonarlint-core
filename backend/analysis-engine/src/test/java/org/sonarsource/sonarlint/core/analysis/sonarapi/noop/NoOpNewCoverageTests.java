/*
ACR-be2a96acb07e43349d3a7b762845808e
ACR-dd8f997a26554a5dbff04705242790d0
ACR-6f0d952668f54c209c0e35272dd51709
ACR-318c02dc2d91447e88e422bf57e935c4
ACR-87d3d86314ef48878b1545ecf1e9d7ca
ACR-6e3437e0bd884ede8623a86045a85ac2
ACR-c608a2fb731e46cda0fab546972605cc
ACR-0a9132a11307415db434182d7135a132
ACR-66c496cbcd7942d7bdf43df979cf975c
ACR-755b120e20914fd2a912279a1665a19a
ACR-95a3f15b5b174bf4b802187e0919e5f0
ACR-e71cf7b20a804f3fa0f9e68cc425cef2
ACR-9679eae75ed349d99eb44342a21a6a6c
ACR-80dcdf97555542d99f0e796ebdea5256
ACR-c7dfbf8f239c402caa6d7a0e1ba2f620
ACR-3d74a93a6d8240fb9a6cf1a6d3a5d1d3
ACR-d1b71007b7844987b19c1add43cc9bab
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi.noop;

import org.junit.jupiter.api.Test;

class NoOpNewCoverageTests {

  @Test
  void test() {
    new NoOpNewCoverage()
      .onFile(null)
      .conditions(0, 0, 0)
      .lineHits(0, 0)
      .save();
  }
}
