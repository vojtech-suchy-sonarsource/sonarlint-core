/*
ACR-804d7c7173474af2a0e2237a9e5f64d8
ACR-100364574583441ba58bcd1404d0b626
ACR-855edff350e64771a0f78b1f793caa53
ACR-acbb82c8b0e4453288527e1afb310f17
ACR-70b1ab5640b641d2ae9e7ae13855d85e
ACR-bb549d459fdf46189ed89397e95b0121
ACR-17b704edd1404469b7674ae4d34461bc
ACR-fc5167f87e534bd9ad4848ea1c474c24
ACR-9a0494f5d9554c999f744e251e1483e5
ACR-417251d7d5ee4935b6d8b9bcfdac75fd
ACR-8a075ed35d184eb0b512dc14fe3c8366
ACR-788195963c644fb7bddf3646a73f87dc
ACR-3efedb49904041b68d791866c57a1548
ACR-0f8d415fa7994a49817dc0ded5a101e7
ACR-e66aadf29840475b85589f3ec80ebaf8
ACR-adcd6bcaf341464081ead450b1bbfc32
ACR-245ffda0b9ca468a90196b60a7923f7c
 */
package org.sonarsource.sonarlint.core.progress;

import java.util.UUID;
import org.jetbrains.annotations.Nullable;
import org.sonarsource.sonarlint.core.commons.progress.ProgressMonitor;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.client.progress.ProgressEndNotification;
import org.sonarsource.sonarlint.core.rpc.protocol.client.progress.ProgressUpdateNotification;
import org.sonarsource.sonarlint.core.rpc.protocol.client.progress.ReportProgressParams;

public class ClientAwareProgressMonitor implements ProgressMonitor {
  private final SonarLintRpcClient client;
  private final UUID taskId;
  private final SonarLintCancelMonitor cancelMonitor;

  public ClientAwareProgressMonitor(SonarLintRpcClient client, UUID taskId, SonarLintCancelMonitor cancelMonitor) {
    this.client = client;
    this.taskId = taskId;
    this.cancelMonitor = cancelMonitor;
  }

  @Override
  public void notifyProgress(@Nullable String message, @Nullable Integer percentage) {
    client.reportProgress(new ReportProgressParams(taskId.toString(), new ProgressUpdateNotification(message, percentage)));
  }

  @Override
  public boolean isCanceled() {
    return cancelMonitor.isCanceled();
  }

  @Override
  public void cancel() {
    cancelMonitor.cancel();
  }

  @Override
  public void complete() {
    client.reportProgress(new ReportProgressParams(taskId.toString(), new ProgressEndNotification()));
  }
}
