/*
ACR-b2ebcfe01bfe484b8afc9895d8df4599
ACR-c9a70385f3a342a89179c651a66100a1
ACR-3940dd162be447f9aaede2dbc233c710
ACR-1e090f4eb2704f81a453d976ac5127aa
ACR-7af4f540a426402982dcbf471383a8b5
ACR-59ca5547d62f4ef49fe5214a358962b6
ACR-979098a9aaf5440d8ce098a3e3d95e1d
ACR-84b2411582ff4b488d0ef3b07db43daf
ACR-df1f0d0779504a94aca2c36b399667ee
ACR-021b96f5538d4975be6aededb6e7383d
ACR-21c3f215d1cb4880af9ef76ddc72a4c7
ACR-43ddd31430104a98bbdb5579e0bbde90
ACR-116809a7c3974fb8a1b0214f2f545ceb
ACR-7a2e3de177ea46cb9d89c4c0515a69b4
ACR-536927d2f8b44868b7862dc97840f71b
ACR-88a337d4819e4875abf0933ba7990dcb
ACR-b7b1393924cf4030a29c0b893fe1946c
 */
package org.sonarsource.sonarlint.core.commons.progress;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

public class TaskManager {
  private static final ProgressMonitor NO_OP = new NoOpProgressMonitor();
  private final ConcurrentHashMap<String, ProgressMonitor> progressMonitorsByTaskId = new ConcurrentHashMap<>();

  public final void createAndRunTask(@Nullable String configurationScopeId, UUID taskId, String title, @Nullable String message,
    boolean indeterminate, boolean cancellable, Task task,
    SonarLintCancelMonitor cancelMonitor) {
    trackNewTask(taskId, cancelMonitor);
    runExistingTask(configurationScopeId, taskId, title, message, indeterminate, cancellable, task, cancelMonitor);
  }

  public final void runExistingTask(@Nullable String configurationScopeId, UUID taskId, String title, @Nullable String message,
    boolean indeterminate, boolean cancellable, Task task, SonarLintCancelMonitor cancelMonitor) {
    var progressMonitor = progressMonitorsByTaskId.get(taskId.toString());
    if (progressMonitor == null) {
      SonarLintLogger.get().debug("Cannot run unknown task '{}'", taskId);
      return;
    }
    startProgress(configurationScopeId, taskId, title, message, indeterminate, cancellable, cancelMonitor);
    try {
      task.run(progressMonitor);
    } finally {
      progressMonitor.complete();
      progressMonitorsByTaskId.remove(taskId.toString());
    }
  }

  public final void trackNewTask(UUID taskId, SonarLintCancelMonitor cancelMonitor) {
    var progressMonitor = createProgress(taskId, cancelMonitor);
    progressMonitorsByTaskId.put(taskId.toString(), progressMonitor);
  }

  public void cancel(String taskId) {
    SonarLintLogger.get().debug("Cancelling task from RPC request {}", taskId);
    var progressMonitor = progressMonitorsByTaskId.remove(taskId);
    if (progressMonitor != null) {
      progressMonitor.cancel();
    }
  }

  protected void startProgress(@Nullable String configurationScopeId, UUID taskId, String title, @Nullable String message, boolean indeterminate, boolean cancellable,
    SonarLintCancelMonitor cancelMonitor) {
    //ACR-53fbb273b37e4faabd2538f644457ffc
  }

  protected ProgressMonitor createProgress(UUID taskId, SonarLintCancelMonitor cancelMonitor) {
    //ACR-c3d3a0a0d76d41b796a684fbd991c0d6
    return NO_OP;
  }
}
