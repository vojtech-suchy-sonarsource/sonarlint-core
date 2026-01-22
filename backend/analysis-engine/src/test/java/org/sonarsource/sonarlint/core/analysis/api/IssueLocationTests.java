/*
ACR-1107419b15894a2ca606059f6e4c116e
ACR-343b5fc9a4894df58d4eb3d60b9cc8e4
ACR-53392f23048a470c901493e5759cc971
ACR-79af354396eb4763831d21ea507bad7f
ACR-dd147546dff040fc99fcdcc1a27d9563
ACR-4bc8477d61e74eadbfde725ffb4c15a5
ACR-fadc6c22bba14238afa150981f3d4820
ACR-78e43983385a458a9b13b9f413d5f912
ACR-0d0f35d48ed440d1bb9a521ed9552c9c
ACR-c9ebe603e2f7496bbe416e352b0ec3fc
ACR-941bdd8439c94cf4ba67cf19ffd13288
ACR-dc2009b6fa3d4d72a0a4d466c7637e32
ACR-3832fb5c1a804ea39600f212c4cb69fc
ACR-88e503eac8e0414d8f878b0b72dae51a
ACR-a3d8be44e7c147f8b06ee73a0ee030fe
ACR-cfb16650ddd54171864f8bf31f1dba25
ACR-995526c3e2294556bb96007731bc04b3
 */
package org.sonarsource.sonarlint.core.analysis.api;

import javax.annotation.Nullable;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.commons.api.TextRange;

import static org.assertj.core.api.Assertions.assertThat;

class IssueLocationTests {

  @Test
  void it_should_return_text_range_details_if_provided() {
    var issueLocation = newIssueLocation(new TextRange(1, 2, 3, 4));

    assertThat(issueLocation.getStartLine()).isEqualTo(1);
    assertThat(issueLocation.getStartLineOffset()).isEqualTo(2);
    assertThat(issueLocation.getEndLine()).isEqualTo(3);
    assertThat(issueLocation.getEndLineOffset()).isEqualTo(4);
  }

  @Test
  void it_should_return_null_details_if_no_text_range_provided() {
    var issueLocation = newIssueLocation(null);

    assertThat(issueLocation.getStartLine()).isNull();
    assertThat(issueLocation.getStartLineOffset()).isNull();
    assertThat(issueLocation.getEndLine()).isNull();
    assertThat(issueLocation.getEndLineOffset()).isNull();
  }

  private static IssueLocation newIssueLocation(@Nullable TextRange textRange) {
    return new IssueLocation() {
      @Override
      public ClientInputFile getInputFile() {
        return null;
      }

      @Override
      public TextRange getTextRange() {
        return textRange;
      }

      @Override
      public String getMessage() {
        return null;
      }
    };
  }
}
