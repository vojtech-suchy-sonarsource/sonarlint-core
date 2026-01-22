/*
ACR-0042ec82790b4a088e25c86ad518fa6f
ACR-21a7bc87846d411b807de11812f48051
ACR-a7bcb6dbf4824cedb21a0a66077a29ef
ACR-df3efb1aec774f05900dc24738f72399
ACR-a3e1427ff33f4df2b649cc3214b111ea
ACR-2e37ec6fb68d4f9a8e296d7e1b79e954
ACR-1553153c0d714da3baba75e55f1083f9
ACR-71792ab1a7d546e8bd5ef7441c00f5c1
ACR-042effca60204dceb4a566203b35338f
ACR-84abaeb8fd0343c5800b9aaa53e15800
ACR-a2f77cd11c4041a2b8d7d4130bbf5141
ACR-98ed2e59070e430fa4e446515b8debd8
ACR-7caa1d6d38554c2085abadeb558b5fb5
ACR-9c4f048192f141f0bf69ab4199292614
ACR-2f770da3d0324b63a604473115c9525d
ACR-0211d0de8a69486c89b046792feb31cd
ACR-41acd0930c7e49c2a7385c719928d6ee
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.rules;

public class RuleMonolithicDescriptionDto {
  private final String htmlContent;

  public RuleMonolithicDescriptionDto(String htmlContent) {
    this.htmlContent = htmlContent;
  }

  /*ACR-615b8a7e3bd04a2394084fcf4084ea22
ACR-9d2185afadf34bf58c5a2df513ec9e19
   */
  public String getHtmlContent() {
    return htmlContent;
  }
}
