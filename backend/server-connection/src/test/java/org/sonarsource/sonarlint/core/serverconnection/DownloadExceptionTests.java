/*
ACR-c41529623b064246844f2e3bf23266f0
ACR-7d0ca0cb2a4b4f75aed3438f3a180210
ACR-bb24bd73c5fd478fac0fc804a2749b6e
ACR-31f6707996bc4951bb15623c40fafd62
ACR-c20335708385459092dac55dd0f844d2
ACR-0a22a45d265c4b3983a6d95a388e70d2
ACR-08e7774e86cd45688bbdfc2aaa6b01f1
ACR-6905395202244d188b9ae8f33b9c5636
ACR-756d0f76fa204c28b30defd8199a2243
ACR-7e0c6d60d8e44b5aabdaf9587e3b1c5d
ACR-70334173ce094e278e732df2794caf73
ACR-b6361c1d43a14ae995196556344d7cb5
ACR-fc2aea83a6354a31a5757991ef704a4a
ACR-214adec80bf6425b81438f9a262376cb
ACR-2e4c2302ef6349d7976abb0342b7b5e0
ACR-4952b0baf3b043f892c5399908b1e849
ACR-1fc54e734b80463e86f4e4a5f1082a6c
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.io.IOException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DownloadExceptionTests {
  @Test
  void testNoArgs() {
    var exception = new DownloadException();
    assertThat(exception.getMessage()).isNull();
    assertThat(exception.getCause()).isNull();
  }

  @Test
  void testCauseAndMsg() {
    var cause = new IOException("cause msg");
    var exception = new DownloadException("msg", cause);
    assertThat(exception.getMessage()).isEqualTo("msg");
    assertThat(exception.getCause()).isEqualTo(cause);
  }
}
