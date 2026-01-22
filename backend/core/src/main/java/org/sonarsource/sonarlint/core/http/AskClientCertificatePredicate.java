/*
ACR-0c7274ac65b04ed6abd2a4f6ecb6465f
ACR-e3847f905bee4daf9c10814d4c9db284
ACR-31e3cef1dfb94dc5af3eace38f24b555
ACR-d1b956c4212c4798b9f27bad49d02541
ACR-cca3f08a42a04cb88a4e7300315f4fb1
ACR-b7a6973cdcf246ed89379e038dc32a7e
ACR-5f874dea2dd94d03bba99bd987a0f462
ACR-7896bbacc7454c12b6cb44948e8e7ae7
ACR-1518a6c919424cecba965842045be360
ACR-e55b43d125b44ce3a92989efd6883b49
ACR-6fd91b32f0b742f0b2b6f4737ada945b
ACR-2d6278b9a57748cb94121db08ddbe21f
ACR-0c093cb3ad7e4afdb3d6c09358fbddbf
ACR-39510756692e4501bace6e9dfdddaaa1
ACR-99aa8831d41c48c187eca9122fac32c0
ACR-4fe5e22ec37347f980a220e0f14a87e9
ACR-c6cc15d7ed694f7d9fdd854060265d5f
 */
package org.sonarsource.sonarlint.core.http;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import nl.altindag.ssl.model.TrustManagerParameters;
import nl.altindag.ssl.util.CertificateUtils;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.client.http.CheckServerTrustedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.http.X509CertificateDto;

public class AskClientCertificatePredicate implements Predicate<TrustManagerParameters> {

  private final SonarLintRpcClient client;

  public AskClientCertificatePredicate(SonarLintRpcClient client) {
    this.client = client;
  }

  @Override
  public boolean test(TrustManagerParameters trustManagerParameters) {
    try {
      return client
        .checkServerTrusted(new CheckServerTrustedParams(
          Arrays.stream(trustManagerParameters.getChain())
            .map(c -> new X509CertificateDto(CertificateUtils.convertToPem(c))).toList(),
          trustManagerParameters.getAuthType()))
        .get()
        .isTrusted();
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      return false;
    } catch (ExecutionException ex) {
      throw new RuntimeException(ex);
    }
  }
}
