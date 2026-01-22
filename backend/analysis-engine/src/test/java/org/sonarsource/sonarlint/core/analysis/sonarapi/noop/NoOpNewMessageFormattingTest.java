/*
ACR-70d95ef68d8e4560acf6b5bba262cb4c
ACR-019abd13c8f4496c83e4fdb6bea5ce53
ACR-8e96008a4d104efab78a79af1cb7127e
ACR-483a3c2e78d14ec3a496e3f3d7d40cad
ACR-dd779f0611a24f91b9068af92db3f6b5
ACR-edec7c4bffd841e9b974e066fa45d518
ACR-ab8f46af2f6841b0bacf02b6bbbaabe5
ACR-84c7a79419394771b614576fa60c513c
ACR-4d4606e3dda74961a388d8b70d9f9294
ACR-a6a480c91d62443cb7eb8a6077f16e15
ACR-52814a580ef54fedb94842738b260375
ACR-0d655b12b3ac4474af62b1b9753e6fab
ACR-9b4771522c9e4c759a60026b47207b78
ACR-37d49514a96b486ea00728904da4dd21
ACR-d6e6d79972854e76b8543f2b223c86ec
ACR-afb455d798514e9a8529193526263959
ACR-6f59b83b8e6b4b3982a0c4df060ee74a
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi.noop;

import org.junit.jupiter.api.Test;
import org.sonar.api.batch.sensor.issue.MessageFormatting;

import static org.assertj.core.api.Assertions.assertThat;

class NoOpNewMessageFormattingTest {
  @Test
  void should_do_nothing_and_return_same_instance() {
    var originalMessageFormatting = new NoOpNewMessageFormatting();
    var messageFormatting = originalMessageFormatting
      .start(1)
      .end(4)
      .type(MessageFormatting.Type.CODE);

    assertThat(messageFormatting).isEqualTo(originalMessageFormatting);
  }

}
