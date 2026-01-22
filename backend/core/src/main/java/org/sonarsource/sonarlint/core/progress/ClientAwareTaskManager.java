/*
ACR-2f7c8c2bde70413db3ce2a0839527896
ACR-09cf5a2c880a4e119b270536d14ec6c5
ACR-ba3be43b07004fbd9d5c14ebad78aeb7
ACR-e1898df791bf42069687fed49ed38781
ACR-bc77ab22f7f748bfb87a71c6c14e340b
ACR-1e5e293c61084d179dac901c7909d594
ACR-4e954600c4c84a24a0e949ef51e5a40e
ACR-bd7ae4d6525b4952bc1565439e1da41c
ACR-1b959e1525f9436c84ab2458722924e9
ACR-de03933e09f3492eb2ecc5d7973fbdc3
ACR-c005f255221d44588446a2883dc17bf0
ACR-80835028b4094615a9a53edd1cb37330
ACR-bada9649ecf64b278f16b6c1003b43f8
ACR-ef2b4e4e3d5a4e289f7dce1cc9fcaf43
ACR-2cfff698ff8f491a942200abee76e3dc
ACR-b83424c5317748eb95e884fbbcd29332
ACR-9e824946fd2e404aa429f0c5fd467c30
 */
package org.sonarsource.sonarlint.core.progress;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.api.progress.CanceledException;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.ProgressMonitor;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.commons.progress.TaskManager;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.client.progress.StartProgressParams;

public class ClientAwareTaskManager extends TaskManager {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private final SonarLintRpcClient client;

  public ClientAwareTaskManager(SonarLintRpcClient client) {
    this.client = client;
  }

  @Override
  protected void startProgress(@Nullable String configurationScopeId, UUID taskId, String title, @Nullable String message, boolean indeterminate, boolean cancellable,
    SonarLintCancelMonitor cancelMonitor) {
    try {
      client.startProgress(new StartProgressParams(taskId.toString(), configurationScopeId, title, message, indeterminate, cancellable)).get();
    } catch (InterruptedException e) {
      LOG.error("The progress report for the '" + title + "' was interrupted", e);
      Thread.currentThread().interrupt();
      throw new CanceledException();
    } catch (ExecutionException e) {
      LOG.error("The client was unable to start progress, cause:", e);
      super.startProgress(configurationScopeId, taskId, title, message, indeterminate, cancellable, cancelMonitor);
    }
  }

  @Override
  protected ProgressMonitor createProgress(UUID taskId, SonarLintCancelMonitor cancelMonitor) {
    return new ClientAwareProgressMonitor(client, taskId, cancelMonitor);
  }
}
