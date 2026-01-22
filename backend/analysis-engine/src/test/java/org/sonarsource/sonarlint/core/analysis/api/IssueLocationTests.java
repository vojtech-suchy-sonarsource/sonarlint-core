/*
ACR-4519bcf85cbf4656852485634102b163
ACR-328af5cf30da405dac0d2c6c4dce3d43
ACR-155a22068d98420dad392d162122baa9
ACR-32271a1817f140d2850e74ef5a151507
ACR-7e742291eb16403baceaa60ccc8a3ea2
ACR-327a0bcb2320444aa829089c08f307e9
ACR-871902b6c7d9464fa31916ac2226613d
ACR-facc8466e0ea419a8bbcf000be87b3dd
ACR-dee1e6effafa4eed9d14b83c5a39890f
ACR-ce7bde3074f347ca8ada6f259bb58b36
ACR-97291c82730b42719f721792ef6b4e39
ACR-6f5b3e687fbc4a49a1ade8034ec8d2dc
ACR-f05cbfeda8f3469e88f41c27924d6753
ACR-a4d73c82dc664a049dfa4367b62df40c
ACR-e2c01f7193ed4ffb9a29a42c53b0897b
ACR-82f0a55a0fd94adbbaff9301f5eeb708
ACR-a45ca1ddfd0146479624b07fa5078800
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
