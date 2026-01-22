/*
ACR-fe7b8868bc9643ea8894b51b410b57d9
ACR-2f9cd4745e4f42209333e35bea90c14a
ACR-1896bdd91632481ca3b48b605bc84e59
ACR-5319ceda96c44ba79bf33c0ba34c3ee1
ACR-f20b25c8bf43423c8af23b2e48826875
ACR-66edfedfb57d43ef83047498af7b2983
ACR-62b2655fdb7f4f63809a494ff112e4fd
ACR-81d50931b17844c686f9276c574b75e9
ACR-4f0e72d6a3794457b011b266ce148381
ACR-b4b95fd258fc49a8bf625d0b81142644
ACR-7a3c43755cd741c9a01a1aa5af54eb6a
ACR-33900bc882f74b9d856b6dbb42750f23
ACR-fc6799168d534df2a9c099443b2aa515
ACR-9a1520d8bb454e248b955b174c5020c2
ACR-95745b34eea143c48d1b058708189aa5
ACR-6df67d1fb48146ffa10320bac1ced73f
ACR-60e7b2ee0008468487d577788a8f5045
 */
package org.sonarsource.sonarlint.core;

import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.embedded.server.AwaitingUserTokenFutureRepository;
import org.sonarsource.sonarlint.core.embedded.server.EmbeddedServer;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.auth.HelpGenerateUserTokenParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.auth.HelpGenerateUserTokenResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.OpenUrlInBrowserParams;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;

import static org.sonarsource.sonarlint.core.serverapi.UrlUtils.urlEncode;

public class TokenGeneratorHelper {

  private final SonarLintRpcClient client;
  private final EmbeddedServer embeddedServer;
  private final AwaitingUserTokenFutureRepository awaitingUserTokenFutureRepository;

  private final String clientName;

  public TokenGeneratorHelper(SonarLintRpcClient client, EmbeddedServer embeddedServer, AwaitingUserTokenFutureRepository awaitingUserTokenFutureRepository,
    InitializeParams params) {
    this.client = client;
    this.embeddedServer = embeddedServer;
    this.awaitingUserTokenFutureRepository = awaitingUserTokenFutureRepository;
    this.clientName = params.getClientConstantInfo().getName();
  }

  public HelpGenerateUserTokenResponse helpGenerateUserToken(String serverBaseUrl, @Nullable HelpGenerateUserTokenParams.Utm utm, SonarLintCancelMonitor cancelMonitor) {
    client.openUrlInBrowser(new OpenUrlInBrowserParams(ServerApiHelper.concat(serverBaseUrl, getUserTokenGenerationRelativeUrlToOpen(utm))));
    var shouldWaitIncomingToken = embeddedServer.isStarted();
    if (shouldWaitIncomingToken) {
      var future = new CompletableFuture<HelpGenerateUserTokenResponse>();
      awaitingUserTokenFutureRepository.addExpectedResponse(serverBaseUrl, future);
      cancelMonitor.onCancel(() -> future.cancel(false));
      return future.join();
    } else {
      return new HelpGenerateUserTokenResponse(null);
    }
  }

  private String getUserTokenGenerationRelativeUrlToOpen(@Nullable HelpGenerateUserTokenParams.Utm utm) {
    var params = new StringBuilder("ideName=" + urlEncode(clientName) + (embeddedServer.isStarted() ? ("&port=" + embeddedServer.getPort()) : ""));
    if (utm != null) {
      params.append(String.format("&utm_medium=%s&utm_source=%s&utm_content=%s&utm_term=%s",
          urlEncode(utm.getMedium()), urlEncode(utm.getSource()), urlEncode(utm.getContent()), urlEncode(utm.getTerm())
        ));
    }
    return "/sonarlint/auth?" + params;
  }

}
