/*
ACR-b7d06f51a3b7464094116792c7d98f7c
ACR-5ef1a574a134409e8578b0b308b86b03
ACR-ebc21c039dd34e6a96c1e7111c7c73b5
ACR-54a556f838dd4b559ed80d956cdbf4b6
ACR-ba58f5d498d4473ba6644eb7d9cd6965
ACR-362c76601d7f4c629b3a0ec38fc9b481
ACR-a78357dfc30e45298bb892a26e9cd568
ACR-0f5a022d6b7d460c94d1f816e54c31f0
ACR-cf53d0d1961a4fdebb08d86a9e4e584c
ACR-f9824df408ba49ca8f104d1ed03fa6b7
ACR-ca1bb2f702d04f9a9b13984d869c8d4d
ACR-df2810ca153443e3b88d78b3e47c95bd
ACR-0b985fa7221b42148a2875f18d086e9e
ACR-0db947a2ffd84365bced5d4917eff0d7
ACR-eb94877f4dd44384978e58af21fc29b7
ACR-5f5a553a1ff04488913a055e3092485e
ACR-5fc68af6b2224bed9c490b89c41ae679
 */
package org.sonarsource.sonarlint.core.plugin.skipped;

import org.sonarsource.sonarlint.core.plugin.commons.api.SkipReason;

public class SkippedPlugin {
  private final String key;
  private final SkipReason reason;

  public SkippedPlugin(String key, SkipReason skipReason) {
    this.key = key;
    this.reason = skipReason;
  }

  public String getKey() {
    return key;
  }

  public SkipReason getReason() {
    return reason;
  }
}
