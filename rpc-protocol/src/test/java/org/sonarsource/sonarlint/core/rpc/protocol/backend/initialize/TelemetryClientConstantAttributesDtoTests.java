/*
ACR-6222b03048bd41928676ebf61b434ce1
ACR-a12242aaec494af58cd9bf106115822a
ACR-9470fe3dec0e4de9b8063d8ba955cc5d
ACR-36d76711cabf4fff89038c8eeaaf138c
ACR-c6761e2bc0464fc8b3f0d4ec7877bd5f
ACR-5d349f8c2637449c88ba34747143afbb
ACR-0f621dccf7fc477ebd99e1688663c15c
ACR-719ccfef886944438ac8e301ce61000e
ACR-4afa7e441ee34a6aafe675867442e5fd
ACR-efca11815d7e4dd5bd26ff41450cb187
ACR-198d267f756a4b5cb5ddda02a9880cf2
ACR-b4b257c9aaaf499cbeb82e4eb93b44c5
ACR-efa65cf17b714f0e85914c01daae7fdb
ACR-541a61ad9927455fbd5a3e3f83d09b74
ACR-19a1bf251e9b418f901e4672f8685af6
ACR-6edb9ba4b15e46c983e7c5d93dea2d32
ACR-283285d238d14ea28d45a59f749e028c
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class TelemetryClientConstantAttributesDtoTests {

  @Test
  void should_replace_null_collections_by_empty() {
    var params = new TelemetryClientConstantAttributesDto(null, null, null, null, null);
    assertNotNull(params.getAdditionalAttributes());
  }

}
