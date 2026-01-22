/*
ACR-529459a040f449e191049cca8054115e
ACR-f31ade3e48b147349c00111029483673
ACR-9f5e4618b24144baa4c277f27c39907a
ACR-95ec19d1c8ec459ab6d62686364f06be
ACR-d2b86a36373341ab9553a6e098d546cd
ACR-e6bfedff751c4a16aa17730cdb68edc1
ACR-89cd63d66a0b4c15b5c3299387cc17c2
ACR-919b109e2ca84f0088e7971fc438f697
ACR-4f44fe50503e435eb5ddfa83a5af2a67
ACR-1c46ad6ee9844b63834c849c7d8bc7bd
ACR-29382c78dbc346bf8c0b304ef0bb98df
ACR-992de0ca586448359aefd1bc9cf9abb2
ACR-87c9c4103a174eb0ae30278c8c2532a0
ACR-6664bceade7542d68f40bed9be9ac890
ACR-4cdf082da9814e0d9b4ee972c6af290a
ACR-dfba2ff877b8499182131a42c0dc1fe4
ACR-2be5dc01a4454446a94fddb5e7ca4feb
 */
package org.sonarsource.sonarlint.core.client.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LanguageTests {

  @Test
  void should_convert_all_enum_values() {
    for (var rpcEnum : org.sonarsource.sonarlint.core.rpc.protocol.common.Language.values()) {
      var converted = Language.fromDto(rpcEnum);
      assertEquals(rpcEnum.name(), converted.name());
    }
  }

}
