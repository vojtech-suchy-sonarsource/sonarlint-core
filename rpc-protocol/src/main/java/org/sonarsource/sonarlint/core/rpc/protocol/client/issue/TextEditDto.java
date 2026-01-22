/*
ACR-7f9c03d6611e4ecf9328dab43900de1b
ACR-ee27a66e6f6b427c92987c8595737b68
ACR-ffe67d3edfff4f0f89bcd361f97b65f1
ACR-c4a58ce56b3f414b9cd635c4b11ae50a
ACR-04d28d486e4d49c0827859956efaec16
ACR-b027b7b226564335abcd31fcd55b0c9a
ACR-8bf7a8c76b354dadb227dfc0f3ec7875
ACR-d0c009b9a64347239bb1e596e38ecbf2
ACR-49cd8d16372f41dea50d8be6f86f8cf4
ACR-e86f22cf7067464a81cdcc8104003dcb
ACR-defc20f696894795ab9225164a352ec1
ACR-023ff17407004339af534da0989963c1
ACR-49168a7d9ff64aeb80a803b46a6a8a26
ACR-66215d3cdda247c78cd2ed59d70d3a82
ACR-a4b3622389cf4035b0ad107919998bf9
ACR-dfc38d20fe6947e9828d74469d28c5cb
ACR-a08c1bb7f1624ec0a17668c0de67d67c
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.issue;

import org.sonarsource.sonarlint.core.rpc.protocol.common.TextRangeDto;

public class TextEditDto {
  private final TextRangeDto range;
  private final String newText;

  public TextEditDto(TextRangeDto range, String newText) {
    this.range = range;
    this.newText = newText;
  }

  public TextRangeDto range() {
    return range;
  }

  public String newText() {
    return newText;
  }

}
