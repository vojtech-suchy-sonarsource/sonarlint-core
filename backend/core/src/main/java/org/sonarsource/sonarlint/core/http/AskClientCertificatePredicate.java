/*
ACR-1b86c37110c6407897902f517202ec17
ACR-da89837d929c4230aeb91841a0c1edce
ACR-a41fabf0058e4678af0e579bb27391f7
ACR-fa7d7302d2fa416b89da75e0ecdfcc15
ACR-650e2504493a4326be1fb4457548ae33
ACR-5f7d647e11c74ae0a545f75ffcd5783c
ACR-0dcbae0cbbfc42eca55aad62d46828af
ACR-8264d780777c43aeb0cbfec680b993de
ACR-0188de4878b748ad9eda29a04da5cb75
ACR-0fd4fd244a1a4dee8a30dfce264f2342
ACR-51b33d995fdf4a4abb0683d171ebad90
ACR-3fdb5a82b2854758bc7e14ba53abf64d
ACR-5f07bdb546c746eca225f921882f965e
ACR-80a1994e354f4e6980f414867157ad39
ACR-07d777a43f3e4848990e133aafd54f48
ACR-1a76d12cdc6649f8b45f5dd485aa2134
ACR-2757e83dfb5c4740ba553f845f2147e8
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
