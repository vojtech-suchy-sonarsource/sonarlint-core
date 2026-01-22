/*
ACR-b44b4371dc344736b31ced683169937f
ACR-8a22ba4301c0493b835fba0481fabf83
ACR-97c0264c2530415dabbd4e6619364955
ACR-cf826424a1c04717a05d3b4e80be2477
ACR-d129dd4ab1ff4befac7eea0fd964194d
ACR-7462b284df684c5f89c438e3febcd932
ACR-4c61edb32e85475d9160f93ed50ee0ca
ACR-961c41fc37604e078d3473d70f8a5a99
ACR-34dfc02d857b40528651dd15dff19964
ACR-8a03394d75ab4adaa6105d2d2f775704
ACR-2180e9a274154443b2291a06fba81fdb
ACR-aa71195965ac409686f9df9410bb9bf6
ACR-eeef222e02f8461e87665e70b1a039bb
ACR-335937c3ed1c4bfdb8b7824983117222
ACR-72c4ff868613477989d6444ee627bc03
ACR-f092e221a8b742d9b5ad0a56b4588fe5
ACR-99abb7a7ad6140c0b96d4833474c32d9
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry;

public class HelpAndFeedbackClickedParams {
  private final String itemId;

  public HelpAndFeedbackClickedParams(String itemId) {
    this.itemId = itemId;
  }

  public String getItemId() {
    return itemId;
  }
}
