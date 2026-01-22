/*
ACR-af3099b031ed4e26bb5afc8b32631cc2
ACR-7cfc05a2185740c284ed0aa3c98108a1
ACR-c9067679702044a2a9a32e75271a5117
ACR-256f940ac3534fa28868e7984ba8d355
ACR-6dde58ee34584dc2b277845ef72083ac
ACR-e21e3a470c7240ff9359f1d3ccdff973
ACR-bd7eff961f1d4036b4b24d1a27660cfe
ACR-84a6ac729e9a4376a602ae698c84ceba
ACR-066ef80e20b9415f93a4a4f6d7240303
ACR-b11b42d67e1847518bb4d8afc4b7f29b
ACR-9c8ea13a367446ac8082ac780ec32e11
ACR-e8ad53b920f34121887969fd85aefb93
ACR-e2e73fb1b0be4c84bf7c03a7865725df
ACR-fb8af98968fd40828ae5bee826f27725
ACR-7faa96f60b5e4dc1a0e524d4908ac07b
ACR-4135e5950ed849cf9426bc8ee51e1887
ACR-a871a2a2620c452588562371d3cee66b
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
    //ACR-1f0668a762b74b3eb4307f258ef8cae2
  }

  protected ProgressMonitor createProgress(UUID taskId, SonarLintCancelMonitor cancelMonitor) {
    //ACR-01678fd7229a4e99a5717175210958e1
    return NO_OP;
  }
}
