/*
ACR-5c52e403f7504eeaab8e654d57b3dbd4
ACR-1ab7db96ab024565ad1fa3339990acb0
ACR-19e656eea2854642bbb1415a58395842
ACR-cb1d44ca77bd48fa837f6a0b18cf0033
ACR-db50cc8c1b2a4aabb3744a80bdc5f9ce
ACR-76660a384b154a5b917f7f0d556e1548
ACR-35e967292add4e50b76dc898b95c4d88
ACR-dd89a07a743c4ecab64b4217261feafc
ACR-29158e00c481436fa1601cc8d32e86ba
ACR-1f6ee50e92b8476caec0b6b2dafe905f
ACR-b983477bc21d429ba1e074893925fa8d
ACR-abf395a6b4de41b7a87c3787aa826ba3
ACR-fe7d7bba5fb1415fa94856f60e98f7bf
ACR-47f9465ecc094850b7e36ae3032a94a7
ACR-a3d8bd2781984a6db9f49c74db3e85d6
ACR-6afbfa37bfe243519a0130506a54e6cc
ACR-0ef805b8e86b4967ab82d1bd297366ee
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
