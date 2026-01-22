/*
ACR-8601af0fde2c4022b22d3904b8dd614f
ACR-c31749140711491484d741c956b40408
ACR-66265acc0e8748db99b8739eaed0e40b
ACR-9b5692a2cb5c452bb4a562753b5177db
ACR-ce16b6b1cb934b8dacfe159f3c240d40
ACR-fb1865a12065410392c87ade71e5a962
ACR-ed1216dee0ac49e2affc0ec01e42cbfe
ACR-01a6d8eec86e4228b537c195936fd346
ACR-8dcc5f9cbf064eb49d079f846f66989c
ACR-7c313103cec84cba8e6fd7adcb034db0
ACR-8e224d7ea588496d8325ea1ab83e881b
ACR-c89c8af5b3164fd3bab11fecf87d9870
ACR-038f27fccb2e4cb58bccf7cefd1b883b
ACR-e1197f26f7244480b9585d588d7a9159
ACR-9c49e58ebc9146c98c550ffae4eb58ed
ACR-d16ebd101b674746bd31a9cb2dfb8397
ACR-2b41f06878c0499e91ca2e783e6b6340
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class InitializeParamsTests {

  @Test
  void should_replace_null_collections_by_empty() {
    var params = new InitializeParams(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, false, null, false, null);
    assertNotNull(params.getEmbeddedPluginPaths());
    assertNotNull(params.getConnectedModeEmbeddedPluginPathsByKey());
    assertNotNull(params.getEnabledLanguagesInStandaloneMode());
    assertNotNull(params.getExtraEnabledLanguagesInConnectedMode());
    assertNotNull(params.getSonarQubeConnections());
    assertNotNull(params.getSonarCloudConnections());
    assertNotNull(params.getStandaloneRuleConfigByKey());
    assertNotNull(params.getDisabledPluginKeysForAnalysis());
  }

}
