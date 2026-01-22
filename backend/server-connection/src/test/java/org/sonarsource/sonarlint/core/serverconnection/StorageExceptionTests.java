/*
ACR-928e6a7a626d4c50937111bdb7a94809
ACR-bad875051c2345e3b6b1f339fcc6c86d
ACR-c55e3501a331460f8f1fa37464dca481
ACR-7ef53adea9194a298fdc310f9ad21816
ACR-56d17973b6a9499abeb1e197be4cf412
ACR-6f56ca44211345a9b1561acb95da59d4
ACR-71ffebad75184f4284cf2e5883192985
ACR-eed70aa8d2f84686b074574b6a133ac6
ACR-65b76c56e46745f6b75a334a2939fcab
ACR-4cd230bbb76b4e418da618f5d92c7053
ACR-49a4c57e38ae4c63a88ee9b6562aa281
ACR-9a3c3ef76dec404787de5ff5af122f01
ACR-886a6710bd814608b86cefca5bd6308f
ACR-86f2533e5c144716b5b112ff74de09c0
ACR-835eec1055a5471cb588a26c44cc23bd
ACR-29c5bf20827d47ef863d076e626be755
ACR-37c7531189084d1fb051c40626440a6d
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.serverconnection.storage.StorageException;

import static org.assertj.core.api.Assertions.assertThat;

class StorageExceptionTests {
  @Test
  void withCauseAndMessage() {
    var cause = new IOException("cause");
    var ex = new StorageException("msg", cause);
    assertThat(ex.getCause()).isEqualTo(cause);
    assertThat(ex.getMessage()).isEqualTo("msg");
    assertThat(ex.getStackTrace()).isNotEmpty();
  }

}
