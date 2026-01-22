/*
ACR-29340fc2c3db459aa2148676d6f0a2be
ACR-2ec2f4d1a2ec4a87902c2f59561f2043
ACR-a49774e8234a48e2b280ee8fdaa1946f
ACR-05a4b32b263646c88cc20b37472cad1f
ACR-c31b775a6f6d48d285350682c81a75b0
ACR-e6cdc77fb85e41c3b326133e0381238a
ACR-a6b5b2c5adcd41e0aff116a4b510a5c6
ACR-1abd7bf10380460eaeaac34ac6d16b59
ACR-51b4f233d3b14d14af4bd88825fb9306
ACR-df782ae28b564cfba0d6fd269dd9e126
ACR-45e55d1745cd480b89e51313ef284fde
ACR-ddbb8913eb6f4c66b50e557189fa6720
ACR-c0c211cc79044d7bb89d0481bd79af81
ACR-db93ea7e92cf4b4fa4e24a3a6c0b50b0
ACR-78f927ae481841e88b571f70b1499a20
ACR-aa8b3b2c130c475d8ffd717398076657
ACR-4fce0d3735c24ac8a9841e580e5fc7db
 */
package org.sonarsource.sonarlint.core.rpc.client;

import java.util.concurrent.CancellationException;

/*
ACR-19fb2a24279f47d5af5540b1204a2270
ACR-578ee53b26c6407fbc37600576efd8d6
ACR-8da5c5a4a83b474ab72495bc267e7060
 */
public class SonarLintCancelChecker {

  private final org.eclipse.lsp4j.jsonrpc.CancelChecker lsp4JCancelChecker;

  public SonarLintCancelChecker(org.eclipse.lsp4j.jsonrpc.CancelChecker cancelChecker) {
    this.lsp4JCancelChecker = cancelChecker;
  }

  /*ACR-3760cda4d436424c989c7998deebe269
ACR-04baa98cae0d477587ba6c0131475759
ACR-30a03883e8d044999a327f613e623309
   */
  public void checkCanceled() {
    lsp4JCancelChecker.checkCanceled();
  }

  /*ACR-dff251fa4df1474795d54ceeb3725ba7
ACR-bac2da9b75e344c2b6fcdb21663900fa
   */
  public boolean isCanceled() {
    return lsp4JCancelChecker.isCanceled();
  }
}
