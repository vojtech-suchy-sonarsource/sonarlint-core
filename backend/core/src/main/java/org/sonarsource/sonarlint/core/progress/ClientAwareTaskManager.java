/*
ACR-94a93b74fb4a46dabc3557e012c34296
ACR-54ed3401387341ca98627a62836c4460
ACR-8bd21b50a82546fbade4566001651d16
ACR-cf8880398b704810b5dd9779f9cf99e5
ACR-a48f7c78ed154e389445b6ffad5c587e
ACR-8bb1ba69a04048da8ad01bf32ce1db04
ACR-dbbc1299a1f54a3998aeb5638b577020
ACR-a53c08104e7f4f809e2117d640eed76e
ACR-003e6b0a11154ad9ac9e8fb8afe22207
ACR-53c23a0c6a924cfeabc9dfe6324074c4
ACR-9e17f37ca2ac4b1493cd8c7b1f0dc4eb
ACR-791c7cd453fb41bca7eab1beb0e38819
ACR-17d4274c5d8f4e7f91f6a5072ad39d89
ACR-055a5d81be5e4461a8818cc70b3770b5
ACR-a598434bc1604e4aaab818e16c221439
ACR-b4846e70b11e4457ae87dc36849a9691
ACR-9eb994378e534420b09aa10ea64fa150
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
