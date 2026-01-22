/*
ACR-caa7781070b84c73bd9b2dd56d969523
ACR-ca18016aae2a448db1a00b8675860001
ACR-efa1077bff954e8aa41f4fc547a0fdbb
ACR-c3d70f51d41b429698caaf85374e4196
ACR-044da93b7dfa4d5daf75d91d5db8f616
ACR-61062074660042dcac49f00d6191e12b
ACR-278ba8dc18d04ccb8f18f1b72c324ee2
ACR-e1a165b0781547a391a4b82a0bf05509
ACR-32813fc497a64fde82da6570a0fbd8f8
ACR-043dd086f8664363a68e55c5df4fc54a
ACR-171d360f897945bd8f882239bb504f0c
ACR-1174f02c99f54f1a8b20c20cb02a8d81
ACR-5098046eee194715b31f18304aafea78
ACR-3dbe3e4cf9a8484caab8e2185491b203
ACR-8bfd6243fa734adcab3f10f2ab70fd7d
ACR-a5bc8eb850c342dfa5c72595eb0c3204
ACR-1e229e2a5ac142f4b4a3fa9faacbdfe3
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.progress;

import com.google.gson.annotations.JsonAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.adapter.EitherProgressNotificationAdapterFactory;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;

public class ReportProgressParams {
  /*ACR-b2f5e626d3d341cf95eef218986311a5
ACR-fc79f509984345ef836422880eced8ff
ACR-ba7d366ec3284f2e9e97e3aeff6160d2
   */
  private final String taskId;

  @JsonAdapter(EitherProgressNotificationAdapterFactory.class)
  private final Either<ProgressUpdateNotification, ProgressEndNotification> notification;

  public ReportProgressParams(String taskId, ProgressUpdateNotification notification) {
    this(taskId, Either.forLeft(notification));
  }

  public ReportProgressParams(String taskId, ProgressEndNotification notification) {
    this(taskId, Either.forRight(notification));
  }

  public ReportProgressParams(String taskId, Either<ProgressUpdateNotification, ProgressEndNotification> notification) {
    this.taskId = taskId;
    this.notification = notification;
  }

  public String getTaskId() {
    return taskId;
  }

  public Either<ProgressUpdateNotification, ProgressEndNotification> getNotification() {
    return notification;
  }
}
