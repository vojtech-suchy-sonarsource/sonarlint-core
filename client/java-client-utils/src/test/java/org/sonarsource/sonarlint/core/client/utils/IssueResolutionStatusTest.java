/*
ACR-20689a65e1ff40959e7412d15fc6398e
ACR-a4f35fe8ce7e4ad0b1530a6738b3cdcb
ACR-f7af0b8ee91545d7830560e57ac0d918
ACR-63797f4337c64a8a9f7ec6724b7fd3b0
ACR-dc223a90c158481aba0bd2449268ac49
ACR-b60e0eb9681b45aeb3cfa15cc5686fba
ACR-725afd9ceb9e44a4b16b26e40c87a702
ACR-e1505a254ec442a088051a772f6bb661
ACR-424a9b3f0d664f6fa839365bbd3be44d
ACR-6bfc2d9a633f4a3788552ef8a3609b0b
ACR-accf8dedb8c445b5b88289489c1b9399
ACR-caa49038cccf45de8253c1730f669e8c
ACR-aa7c4fac283d4ddb86d5c19b923db712
ACR-0aa1c4b77b3b4addae6919e98305d543
ACR-f3635396b1e2492ca4d75afd6a00d779
ACR-cdb48dd5263f4005bbe9a975b95afdc7
ACR-b08c0a4aec1c48a7b596f1dc4518baa2
 */
package org.sonarsource.sonarlint.core.client.utils;

import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.ResolutionStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IssueResolutionStatusTest {
  @Test
  void should_convert_all_enum_values() {
    for (var rpcEnum : ResolutionStatus.values()) {
      var converted = IssueResolutionStatus.fromDto(rpcEnum);
      assertEquals(rpcEnum.name(), converted.name());
    }
  }
}
