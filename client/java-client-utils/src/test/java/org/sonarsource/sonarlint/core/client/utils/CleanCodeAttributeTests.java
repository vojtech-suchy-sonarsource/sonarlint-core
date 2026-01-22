/*
ACR-99ef277fe0af498e88ae53c308b6d973
ACR-30d161a7a78b4f81b4506652735dec6f
ACR-caf126e6302442a98c36a85ccb02ede7
ACR-e7b4e1909ad0467e8fa6323e5920e435
ACR-f660f2e457954810b43d4eb3913c3bf3
ACR-9eda204b87604a7ba8a38b48001c3e57
ACR-03b850c4377940f79c3adce8ceacaced
ACR-20d5663985ad4412b1fee6247ac1576d
ACR-ce57c22d781545e5b7c23c1105c3da1e
ACR-df352fa1827a426e8ade16d514d86701
ACR-bd92e179435344fdb86f65b0f6420150
ACR-f3b3571f0d334e16ad18a63153a47639
ACR-1c20ba61dd0b430ea05026f128159b59
ACR-742b414c21fc42e59de3bc9e141b9953
ACR-c0bbfa4009bf4247bbd20daa52cd1308
ACR-20c1bfd7c23241fc923d9fed899d3c0a
ACR-dae0b255dcf541f98119456ea8c709d9
 */
package org.sonarsource.sonarlint.core.client.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CleanCodeAttributeTests {

  @Test
  void should_convert_all_enum_values() {
    for (var rpcEnum : org.sonarsource.sonarlint.core.rpc.protocol.common.CleanCodeAttribute.values()) {
      var converted = CleanCodeAttribute.fromDto(rpcEnum);
      assertEquals(rpcEnum.name(), converted.name());
    }
  }
}
