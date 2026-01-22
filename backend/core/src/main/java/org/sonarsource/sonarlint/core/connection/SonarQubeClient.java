/*
ACR-f690361b50224803b944f179d5c96654
ACR-c316d8a757de4f4290937888f810af8d
ACR-c292ad7c1ab24006b4f50f14d30a6b58
ACR-cc3783d4148b4335949c9afe8fd65004
ACR-470aeb64861b48c2b92e358114f41f2d
ACR-e9a771ca25ad4aec90bba1063100c008
ACR-704c1e3504cb49229a4c17845db7c5b6
ACR-1b36d4a857be47ffa9878ef762962eef
ACR-97a9966678e043fb8cc0122ca895dc7e
ACR-b8238ca2eca741e8b5cd5a6756290f06
ACR-11fd0c7d17624614b51403b2bbe9e1fb
ACR-d41ac028849e40878b624baf381ddfad
ACR-51e65b9972db420d8aec1b5df9f426d5
ACR-0986d8caf2be453c92e4cee00ff43bf8
ACR-74f93a02a3744ebd81e426c5cba82e49
ACR-c1096e642a664875a756b02096e8e619
ACR-8df94430e5ec44fd8dd5e523a17e7fa0
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
