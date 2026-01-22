/*
ACR-a077aca0a31c4453bf5f2e7d81a4815d
ACR-14c63eb4f94e4bf58c5145dad0d0cbff
ACR-6c8416c18d1746f891a1589a07ad5355
ACR-e205868b83c54c7aa19d964ffb5466b7
ACR-3cad45d1e0fe4af68ad47bf77a2bf201
ACR-60a0e921abf943f3831e900cf55e4744
ACR-0ce718afe89d408886c54040c5ff4767
ACR-3968393a62fd4caab05d7ac8a5a91286
ACR-cb99c23f990f4932955459b973243e43
ACR-add859ecfddf4972bbf1720af74e91d2
ACR-b28188ae3a62459ea47b63def832c7ba
ACR-1f8bf4dd2a57410eb5e5fb494b0c6558
ACR-94a244f9872e4006b21aab6460334a72
ACR-7547ab63f78649599b74dc44b930efce
ACR-39d902d02ef04b76a310d217bb9d3d1c
ACR-240b69c8b63a47d2a384b231f6541c79
ACR-71da66a9857d4c489bc5facaf2713d69
 */
package org.sonarsource.sonarlint.core.rpc.client;

import java.util.concurrent.CancellationException;

/*
ACR-dcaa31c4bbb34344b177571db6bc3b4d
ACR-b8a36f46fa8b4460a54b2d530ccaacc3
ACR-ca11111a97c643049dfdf6536e0ebc7f
 */
public class SonarLintCancelChecker {

  private final org.eclipse.lsp4j.jsonrpc.CancelChecker lsp4JCancelChecker;

  public SonarLintCancelChecker(org.eclipse.lsp4j.jsonrpc.CancelChecker cancelChecker) {
    this.lsp4JCancelChecker = cancelChecker;
  }

  /*ACR-08fb1da4a1af4222b191809ef8fc4c40
ACR-7d69c4c0d8f8455fad2bad71972c6ead
ACR-70a624be213f4c1f80c11c7f50a2cdfb
   */
  public void checkCanceled() {
    lsp4JCancelChecker.checkCanceled();
  }

  /*ACR-7c8949a7e43041f3acf11870d91de68e
ACR-8bd248ea7908440ba85caacef01c96c1
   */
  public boolean isCanceled() {
    return lsp4JCancelChecker.isCanceled();
  }
}
