/*
ACR-037d0cece1af4df19df3ea7b13663940
ACR-2b826559ba96480b87b498c39152a7cf
ACR-1e6f7caca51c4ead9ab1a3f8ede56cc6
ACR-1f3b8b45a90a4091a2b0b72c00baee08
ACR-25c94379f26f41d484b2a13ed2b0c105
ACR-db5718719279451a8b2681767b5c3fdc
ACR-3fd7fac9aeb64eb886c598ebb0bf607f
ACR-b03764f9a63a44109c5c2357dd1f8d4f
ACR-007fc5d4a4654f7ca9b89ee02f908171
ACR-43bf8159e0474a9980d55015ad9ae41e
ACR-2d2f1af1b5a74473808ec040bdc1d544
ACR-6941d2d1c22d47f086bd89ed6991d5c1
ACR-056f7dd4dfb74c739b95cc7e7514568d
ACR-e4ff9b2103c24150ac45e07e8ced1dd2
ACR-19582ee35c57445fa6ec14b42a460527
ACR-b56b7a1810f744b7890880412ca89a78
ACR-be2551594a2c4b19aef46cda41f7f2e4
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
