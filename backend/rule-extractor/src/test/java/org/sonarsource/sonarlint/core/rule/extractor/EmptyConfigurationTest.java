/*
ACR-372ea194b4f44af8b6c152255b62f9ec
ACR-5504611818f84dfc8233a31410dbc5f4
ACR-a6516ee79cff418a9dfcfa4c0bb2f1d5
ACR-021be7724dd841928c2f7600498f788d
ACR-f3ed84cf8674404f80f612c2a7720e0e
ACR-4680557c607849c8bcb582025265924b
ACR-a311c81135a74277aa4b5d32a36e6df4
ACR-b57d27c8cfc347a49c11117cc6abb545
ACR-2d27cf1b518b4145a56ab0e547696212
ACR-9335071383984f2b87c6ff290cf5e884
ACR-afcde0dcc8fb4479a765b8f45e56042d
ACR-67bf05c280f54385a57adece88d42559
ACR-46fe8c1292f6462e900d8fe56c51671d
ACR-fe043e1c672a48e6a98cae989969e6b0
ACR-aba52b8352d14f8487c0bd2def25e923
ACR-2c56c7420f134bb98aa5f82ad2d589b3
ACR-69d5911e44a6496c99e0ae1bd74956ff
 */
package org.sonarsource.sonarlint.core.rule.extractor;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmptyConfigurationTest {
  @Test
  void should_be_empty() {
    var emptyConfiguration = new EmptyConfiguration();

    assertThat(emptyConfiguration.hasKey("")).isFalse();
    assertThat(emptyConfiguration.get("")).isEmpty();
    assertThat(emptyConfiguration.getStringArray("")).isEmpty();
  }
}
