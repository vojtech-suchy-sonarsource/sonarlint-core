/*
ACR-a42dfc4968b1451f8aa89874fb505719
ACR-adeb1a736ce54b209464cc9884257bd9
ACR-4220e7e70a6a42b1a806dec79da9b50f
ACR-f4b25587aa9c40fdaf6e02f0116d9dea
ACR-da4799e8972b426fae4aa54098544156
ACR-ab555999ce0c427db666ec60902446fb
ACR-a5987800faa249a9a78ccbfee0bd5daf
ACR-e6af51fa26ce419088e6752f140c88fe
ACR-d249476d0c3447639c92062b011c7d34
ACR-6c8a75873c124290856e008c09c4898e
ACR-aafea781afde4ca8a839cf33d2a0b258
ACR-7704ce8e7e6f40aba347ca285e208853
ACR-cf32cbfa15a04faca78da35db786bb12
ACR-3f366021eb1043aea73312950b612cd8
ACR-14cbad0895364098a4d1be9962568344
ACR-8c25102a926e4a1cbf0e681fa504a341
ACR-f079a44c43674cd9a01aa374b8b670bd
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi.noop;

import org.junit.jupiter.api.Test;

class NoOpNewHighlightingTests {

  @Test
  void improve_coverage() {
    new NoOpNewHighlighting().onFile(null)
      .highlight(null, null)
      .highlight(0, 0, 0, 0, null)
      .save();
  }

}
