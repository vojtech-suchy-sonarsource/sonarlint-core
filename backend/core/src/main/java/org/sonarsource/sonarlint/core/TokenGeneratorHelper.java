/*
ACR-decaed967b334dc3a62e0c43f55d169d
ACR-4290eef1bf04441c87c89e18be69b6ac
ACR-d11d9ef412ec4d8cb1a711534303f232
ACR-cc0a7395e6504e698d0df9a70871ffee
ACR-038d7dbd26ca4956943a540200f3dcfc
ACR-d29ce68302544d7d88e3b5dcffd6ad37
ACR-481764e3403d404aa3b594c2b354aa25
ACR-a8436a12c3934f6b99d9ff6f2883abe8
ACR-22a182f9ef3b46189ddf2fb8d3aede95
ACR-3fca92949ed4425497f20a3ae33ea150
ACR-bfd1f35b8221498f8866c6b711b8925c
ACR-2a66688a5ff841e6abc12aad04af2438
ACR-ef7844d2022e482c9a7131137259d6c2
ACR-d50f8eff82ca4c079033450472387a0a
ACR-89a25693354949aa891c317ccbeb16fe
ACR-6bab96bb4cb04f28b429198f35471e8f
ACR-b601b35dd35d4566b04027c8eabf7eb5
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
