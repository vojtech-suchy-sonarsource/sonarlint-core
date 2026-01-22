/*
ACR-62643856cb79434e88c133c47f2e1687
ACR-04e70a8c1b0546eeb4cb9b58a6e0feec
ACR-6300db06f258437892f869efeb52eeb6
ACR-7ad7ffec34bc420c9110f8e228229484
ACR-88c95bdea68649068e5049588d515e61
ACR-4d99d6112c524fb1a224d7889b4fb58a
ACR-8d5f1595c5af4959a9844c12be896572
ACR-8bb9790c1c0147d3a24a48500d6bf1ec
ACR-6cd168136d594bd7ad6624194fcb306d
ACR-b4455ee1454c41f183dc15b70b590ca7
ACR-1da0dbcd02bd4e46ab5952e69e837494
ACR-7583913d267a4cb481a8d22f21daa857
ACR-e07dd8d861c84d5ab9fd7a947c36d348
ACR-a67cccf74c784d1c89bd0c1a6d6e6e14
ACR-fb7c948be6bd4024aee489a7c6ae662e
ACR-588e2829b7eb4ec69bdd2aa66121bb32
ACR-03648b1b605e4514b45c3e62b9d2c5ea
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
