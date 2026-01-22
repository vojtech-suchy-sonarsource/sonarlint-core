/*
ACR-450245d7fd434a30a14cd6b0b5bb01b3
ACR-9251c2305f664554830fcdefdbb414ca
ACR-83e48ac8da6b45c7a9c3c69fd8e66025
ACR-ff5569fa9f3c455984bc74dc67f895ee
ACR-01504503846048f5a99787fbed2e5a93
ACR-1c6c3865ed1c4fa18318e908d85a8f15
ACR-a7752e55d94a46348253a614eea62f0b
ACR-06d8b63d77474be2839c950e3a3cc995
ACR-035a015b65634374b1c3614b7a505dc3
ACR-e7758c4d602e46bab5b0235c7a7044bc
ACR-6ed8751202eb4a4591bb736544a6e7e0
ACR-3059e879e74a4735931fa7957e73373f
ACR-09d0d4bcd85243119d0b1328f50b6e25
ACR-a3210d45d5fe44eda9652923508a4628
ACR-5392fa5ebd994db0901c7545d18700de
ACR-31245175612f480881dc482915b88e2c
ACR-f8e1075b9db540e0946ee53a25c42381
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
