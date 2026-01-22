/*
ACR-5301fd78d8764ec4b3544656604bc311
ACR-040e6bb1701443e7bd079930a8cb2a58
ACR-d59407c621ab47fc8a084ae0a42fa2f7
ACR-cb16e670e70341a1b11a81bcf0d566aa
ACR-cc6865fd31ca406980515e1de25a708e
ACR-ac339efc952c454dbeccf8dd4b10f068
ACR-9703f85242104261b6c668d0d255f72c
ACR-001825dfa61641f18ee84ccead34baf8
ACR-9b95c3bcce4e4390ad77052b15606a60
ACR-4dfdaae9c28f482a8028fc9e4d3df587
ACR-36f9c0adbb3449ca8823ff9350ad0604
ACR-41c482f858ce4b30a7e8a08cf88b1123
ACR-7f77b0eea9d4449d9cc0eee2251bdc7a
ACR-8527875686114468a234a0db6296c935
ACR-335f49cf68564df2b8e29e6df496f555
ACR-df3750e5b8b6427d8daba0b304b2a114
ACR-9a8e2ab87ad9448aab449eb0f2807076
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
