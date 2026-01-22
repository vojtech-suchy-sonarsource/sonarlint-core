/*
ACR-615895f845584871bac97f70944e28fc
ACR-41ec2b91354e404cbf357d9852477c7e
ACR-c5b0160d73304140bf76f56225b972ef
ACR-277c9ad208a44dae9d18a85faa136555
ACR-930647c219fb423e982341002f87e470
ACR-1ed5e36593a146a3a8871d3a91d79ed7
ACR-f0d9bcdf35db4c3093b5aa261711874f
ACR-dff824d3aac040eb9642acb2b95b2142
ACR-a0fc4b5073124a758ebf929b6b640f76
ACR-2c139eaacccb44b6a6c04abdca58523e
ACR-1c2a3a142ae14e68b090602cead77dbe
ACR-ef6d3b3b653e401b95c3612dabed25af
ACR-b050356a41cc4a0e874d6efb89a444e1
ACR-0d12ea533de0419aae46650e895cd366
ACR-fd68e84ddf384b2dae1ef6271df807da
ACR-7460b3e4a8f64e35b78cb8608b77f0c7
ACR-8f8f576e2f05431ca2c9e70ee1dea8de
 */
package org.sonarsource.sonarlint.core.rules;

public class RuleNotFoundException extends Exception {
  private final String ruleKey;

  public RuleNotFoundException(String message, String ruleKey) {
    super(message);
    this.ruleKey = ruleKey;
  }

  public String getRuleKey() {
    return ruleKey;
  }
}
