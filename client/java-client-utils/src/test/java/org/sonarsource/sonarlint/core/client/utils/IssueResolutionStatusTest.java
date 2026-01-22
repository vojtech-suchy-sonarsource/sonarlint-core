/*
ACR-2bbf16fe453c464faf618534951c1011
ACR-e058635c95ce44a493118194e22a0284
ACR-0362510a00bb4588afadfb7157ea7dcc
ACR-e0d670e142ee4622a1b4c20a0586d2c8
ACR-a6117d85d8964ca1bfd069ac5093ff32
ACR-42f24ec949d041e88fa6127f8eb5343e
ACR-0fe3d7541e5942f78b12bafddb45cd89
ACR-e52232be4e7f4d06bf49ea72557aab49
ACR-4962c2f86c364f3baa36c3c2358d80a2
ACR-23df30cb98584f4785e2d0cb1f434031
ACR-c76a820cdd5947e3aee4d04bef5efa54
ACR-55f578a2d2224a0092ee959034db91b9
ACR-97c4d12159f6405f8c4e5d7ad6ec60cd
ACR-4d99fa26a70f43c1914c399109fc789d
ACR-f9d72ea7d5ff4adaaf1dbd727baf03ce
ACR-5b4322ee5a8242d1ba989dfa6ffb03a4
ACR-478137ee37624b0da79d9cf3cc8a34ad
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
