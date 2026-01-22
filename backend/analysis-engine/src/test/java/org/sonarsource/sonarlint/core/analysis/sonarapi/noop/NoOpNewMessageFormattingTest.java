/*
ACR-9e25b360eea4425fb833c3a75c94b316
ACR-a105ad5df2c24373a2ac3636923c8dba
ACR-3efe9841b5f34605a45215dd15e383a4
ACR-a7cfe715274744c6b94c8171fbc5bff0
ACR-9a534b533c7142358e3b79c3bedb0154
ACR-a330d803b28b4b44b3c5a874d56668f7
ACR-59038ace0adf4578aaa809544fc03fc1
ACR-7489a4cbe5e74367b202253c9679acff
ACR-0fedc2b37de44df3a92f0cf41d96f0d0
ACR-7c26cf5348d74742ac614dcea100ce41
ACR-5be6ff354860458e9d2ba6df90c2e3c1
ACR-b5a6fefdb94c49d0be1f556c4336a047
ACR-7b4a39ab9d5349f98de8753e68750c36
ACR-7b9594fb83f040c2b7fc576c9c58be81
ACR-5ca96a40a4e540b4ab095d866a08feb9
ACR-13f08e100c384efc8c6379035f40dd00
ACR-0f87f69473b347e980ef756326cc1669
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
