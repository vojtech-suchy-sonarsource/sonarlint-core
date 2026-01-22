/*
ACR-86767dd0f27d4f80be46e8d1e8c56f00
ACR-a5f36fc2f4f94dfcb7d1f4553f2c53fe
ACR-32bfe0efa11f4b068f585d581dacbfe9
ACR-27955f5b77da4431a5e890e49e2ac135
ACR-51085403d0f74f68ac8f35181a6f1f9f
ACR-3e71ce95f4724dfda12dd467c65c8a67
ACR-4591e58b8d0d490cace5e7ec96d0e41c
ACR-0473d9bf854d499a94ee4408b223ad87
ACR-f81ce84c8c7042d1835abbc1e2a8874d
ACR-dcd2f79348ee4f77bdc0821648aee6a0
ACR-e7674d0b64854b41b85a9708dac9a012
ACR-ecaf67f5d4934d45b8ae012c7cfe76fe
ACR-ef76866a7736481cacff70b424fccf74
ACR-5a6dbf1f764b44149d79995107e81c15
ACR-be09248e37cb42bfb4e619ab1f8b2699
ACR-b0c3e65f86af4590b3782279626ca10d
ACR-162ce3475337450d9331daa38c115e10
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.message;

/*ACR-5597fa8f589c44c29359831981761f45
ACR-af49cbbc393b47068c281c257bcfab6b
 */
public class MessageActionItem {

  /*ACR-63007e159d1b43968df89274238b2b60
ACR-d7c6d5da80fb48109c64864afde3ad6b
   */
  private final String key;
  /*ACR-a207ca2178cc48ba92f3b08df6635dee
ACR-5edf5981e2d948b79137278f61db632d
   */
  private final String displayText;
  /*ACR-594c7496905648dc9815d3f259a38e07
ACR-067143261ec14ffd82ed1b926c3f197c
ACR-6543ef57892d425487a3ab5d04459b36
   */
  private final boolean isPrimaryAction;

  public MessageActionItem(String key, String displayText, boolean isPrimaryAction) {
    this.key = key;
    this.displayText = displayText;
    this.isPrimaryAction = isPrimaryAction;
  }

  public String getKey() {
    return key;
  }

  public String getDisplayText() {
    return displayText;
  }

  public boolean isPrimaryAction() {
    return isPrimaryAction;
  }
}
