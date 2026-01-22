/*
ACR-c9fe8be9a0154b1bb0ba878e1f54aa20
ACR-bb4c1658072b4f6997260fd27b7057dc
ACR-57fa1faa808245fabdd83071e9e72d29
ACR-9d8cc854306148ae89b6651244e852d3
ACR-e8e2f9d66f7f461887bdf4d143fdf233
ACR-16faf729eda245fabe4eac61de159825
ACR-ef3c444168a145179b6634732310808c
ACR-5bb7e942586d4bd0ba67e40f02c5ad76
ACR-2700d46e62c34e73b9a12327848f3f45
ACR-d7746f2a5aeb433a9dd6d286a6dbf9eb
ACR-e35791d66f4d4e62b828a1916a26eb7f
ACR-aac4df5b3656468289420dd0fdf1ca58
ACR-e3ae1cda0b2442ccbc669cea6b6064f5
ACR-d445c98c676d4c3eaae62fbdecb69f00
ACR-fa9a208b6f5e4521b870ef885d02734b
ACR-17355f05969a4545a51f3bf7a736cb1d
ACR-c7f7f14309c4413fb076325792f23357
 */
package org.sonarsource.sonarlint.core.connection;

import java.time.Instant;
import java.time.Period;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.client.sync.InvalidTokenParams;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import org.sonarsource.sonarlint.core.serverapi.exception.UnauthorizedException;

public class SonarQubeClient {

  private static final Period WRONG_TOKEN_NOTIFICATION_INTERVAL = Period.ofDays(1);
  private final String connectionId;
  @Nullable
  private final ServerApi serverApi;
  private final SonarLintRpcClient client;
  private SonarQubeClientState state = SonarQubeClientState.ACTIVE;
  @Nullable
  private Instant lastNotificationTime;

  public SonarQubeClient(String connectionId, @Nullable ServerApi serverApi, SonarLintRpcClient client) {
    this.connectionId = connectionId;
    this.serverApi = serverApi;
    this.client = client;
  }

  public boolean isActive() {
    return serverApi != null && state == SonarQubeClientState.ACTIVE;
  }

  public <T> T withClientApiAndReturn(Function<ServerApi, T> serverApiConsumer) {
    try {
      var result = serverApiConsumer.apply(serverApi);
      state = SonarQubeClientState.ACTIVE;
      lastNotificationTime = null;
      return result;
    } catch (UnauthorizedException e) {
      state = SonarQubeClientState.INVALID_CREDENTIALS;
      notifyClientAboutWrongTokenIfNeeded();
    }
    return null;
  }

  public void withClientApi(Consumer<ServerApi> serverApiConsumer) {
    try {
      serverApiConsumer.accept(serverApi);
      state = SonarQubeClientState.ACTIVE;
      lastNotificationTime = null;
    } catch (UnauthorizedException e) {
      state = SonarQubeClientState.INVALID_CREDENTIALS;
      notifyClientAboutWrongTokenIfNeeded();
    }
  }

  private boolean shouldNotifyAboutWrongToken() {
    if (state != SonarQubeClientState.INVALID_CREDENTIALS && state != SonarQubeClientState.MISSING_PERMISSION) {
      return false;
    }
    if (lastNotificationTime == null) {
      return true;
    }
    return lastNotificationTime.plus(WRONG_TOKEN_NOTIFICATION_INTERVAL).isBefore(Instant.now());
  }

  private void notifyClientAboutWrongTokenIfNeeded() {
    if (shouldNotifyAboutWrongToken()) {
      client.invalidToken(new InvalidTokenParams(connectionId));
      lastNotificationTime = Instant.now();
    }
  }
}
