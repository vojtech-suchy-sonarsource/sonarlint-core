/*
ACR-9d12ac9171fc44a2a1eba6c7eeafeaca
ACR-15efbee24fdc4ea186d5bf6101e689b8
ACR-8f8b4a708ea0471894ad14c052d3659c
ACR-1bc3e877dfcc4bf49e6ce6150f228c6f
ACR-5d1a533e5de140c59e62111b312c3757
ACR-ab6d814818bf40099ef93dd238750362
ACR-a836e6c1b5384b49a289fe9a3853e611
ACR-e4492e72e45b4b8fa2fabf317b3c3865
ACR-0d861baafedb4233b53e261f3014e55b
ACR-da9ace4ae095454a91bcbc2b198be963
ACR-357428a8348247c49469d532fc6ba963
ACR-9bd6591726c846b0b6e75d8d4529225e
ACR-9545ed822d5e4b588f7d10749d341611
ACR-4ec1cb037a084d52bbf3e46bb9c021a1
ACR-071e67c2dc3f4dc298a7741e7d9a5cfb
ACR-62b5f789a8d341239add87e838fcad7b
ACR-bda2098fe10445ebb1e6724350779c13
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.rules;

import java.util.Map;

public class ListAllStandaloneRulesDefinitionsResponse {

  private final Map<String, RuleDefinitionDto> rulesByKey;

  public ListAllStandaloneRulesDefinitionsResponse(Map<String, RuleDefinitionDto> rulesByKey) {
    this.rulesByKey = rulesByKey;
  }

  public Map<String, RuleDefinitionDto> getRulesByKey() {
    return rulesByKey;
  }
}
