/*
ACR-de51f40a8ec74a879c6d2049e256febd
ACR-1794ec9656ec40da99d86443a9004a6e
ACR-8c67090b61fe4578b7315216bdfd03c4
ACR-edcaa2a42cee4f96be200caef94b0573
ACR-9fb52cf48920496091c525d4d319c017
ACR-08e243ddc97f445b83baf32d2a57f45b
ACR-5b81000541964a6ebc7caa742ccdeb5f
ACR-dce60b15272f4334897178a8b18d2c89
ACR-36e83c3f66d24b9fae5bcec1d4779596
ACR-0c449bb46aec489db372531600629db5
ACR-660e9fe84ce34a6c90bab2c761d5f437
ACR-c7a4ce3f7a764d81a20806f9f4149e43
ACR-91fbd99a8c7b44c1987c4199d0813d54
ACR-38c107b1c1734d548647aaf20fd24e43
ACR-464db432f4084bd5ad300d3771c16cc6
ACR-ec1983dc3edd4a36a37919b140627391
ACR-d1c189328a924bfcb127d257818fa7bd
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
