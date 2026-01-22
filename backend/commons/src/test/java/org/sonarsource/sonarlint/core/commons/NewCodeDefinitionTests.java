/*
ACR-d5e3aa896406423b9023288fd8ad6a31
ACR-9a84d85d1a8c49198fb6758b8e2d4652
ACR-48d26b58cf174600a3cd6fa0bd86bbe5
ACR-0b93c2d3297149e48b0930cb977dd6d4
ACR-d50b2a87695c456388dd1f83f80d9738
ACR-b4036b36ee8646928d78d16ed2242e88
ACR-a35420b6d3774196b190dad421f3b9d8
ACR-d4f9ee000b1e4e50b05638bf5ce4651a
ACR-0d141cdaf92a4e679434efdf363ea001
ACR-e952dfcd250a4936b75b34a0cc111b47
ACR-a291c57f000a4d818819dfa3cd5dac82
ACR-a78b3a75816f4ac7983ba5c0fdc9db62
ACR-e80d80b847e94940bc9baf53127fb129
ACR-4eb1aa7cb3f84d738dbd6110af2f079e
ACR-214ef40595fe462cabf7ffd74027f0d1
ACR-61291d6ed16c4a9b8f47ba3b3d4dbf92
ACR-76df94d6a3db41f384dd28124d69271f
 */
package org.sonarsource.sonarlint.core.commons;

import java.time.Instant;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NewCodeDefinitionTests {
  @Test
  void isOnNewCodeTest() {
    var analysisDate = Instant.parse("2023-09-12T10:15:30.00Z");
    var issueCreationDateBeforeAnalysis = Instant.parse("2023-09-10T10:15:30.00Z");
    var issueCreationDateAfterAnalysis = Instant.parse("2023-09-14T10:15:30.00Z");
    var newCodeDefinitionWithoutDate = NewCodeDefinition.withNumberOfDaysWithDate(30, 0);
    var newCodeDefinitionWithDate = NewCodeDefinition.withNumberOfDaysWithDate(30, analysisDate.toEpochMilli());

    assertThat(newCodeDefinitionWithoutDate.isOnNewCode(analysisDate.toEpochMilli())).isTrue();
    assertThat(newCodeDefinitionWithDate.isOnNewCode(issueCreationDateAfterAnalysis.toEpochMilli())).isTrue();
    assertThat(newCodeDefinitionWithDate.isOnNewCode(issueCreationDateBeforeAnalysis.toEpochMilli())).isFalse();
  }

  @Test
  void toStringTest() {
    var analysisEpochDate = Instant.parse("2023-09-12T10:15:30.00Z").toEpochMilli();
    var numberOfDays = NewCodeDefinition.withNumberOfDaysWithDate(30, analysisEpochDate);
    var previousVersionNull = NewCodeDefinition.withPreviousVersion(analysisEpochDate, null);
    var previousVersion = NewCodeDefinition.withPreviousVersion(analysisEpochDate, "version");
    var specificAnalysis = NewCodeDefinition.withSpecificAnalysis(analysisEpochDate);
    var referenceBranch = NewCodeDefinition.withReferenceBranch("referenceBranch");

    var analysisDate = NewCodeDefinition.formatEpochToDate(analysisEpochDate);
    assertThat(numberOfDays).hasToString("From last 30 days");
    assertThat(previousVersionNull).hasToString("Since " + analysisDate);
    assertThat(previousVersion).hasToString("Since version version");
    assertThat(specificAnalysis).hasToString("Since analysis from " + analysisDate);
    assertThat(referenceBranch).hasToString("Current new code definition (reference branch) is not supported");
  }

}
