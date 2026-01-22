/*
ACR-9fa36fca421440afb7946c89ba8263ba
ACR-43bbb13ae74c4345ae3fdbaff81ec8d0
ACR-9ef8e9273b0c47b1a79945af3d9b5a81
ACR-b7ead6728b2049139cd984ad4f009219
ACR-48e3bd6f8fc847b38a93efe822b361bd
ACR-10ec4b9b28ab4f7a9e7e6dd9c04197f9
ACR-9380fa638ee64f04ab983ccef12c5e92
ACR-539834e7b891409e90f0a9682edadf15
ACR-f6c7397778234a5da7c73438ca410409
ACR-41e723a333bd4adfad5b46e5f88353bf
ACR-1f4ba3ad235e4c23ac504d3ace4d7418
ACR-ec2cd81eab814126820b26b7c627ed44
ACR-1b9eae6707554412a7bd0f69050b8d40
ACR-af2df3074a08497aa4c2b820686fc359
ACR-fa5d91023f4e4744bcdbf52a0c144c85
ACR-8c330d962d154f08af86baec3d658e3d
ACR-1fcd63270fcb4466ad239d2e92e72bb9
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.progress;

import com.google.gson.annotations.JsonAdapter;
import org.sonarsource.sonarlint.core.rpc.protocol.adapter.EitherProgressNotificationAdapterFactory;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;

public class ReportProgressParams {
  /*ACR-d50f5c9543e643aa93834ee1421e0333
ACR-341921a87cdb43238f77710b32f36b1c
ACR-381c4c13bb2c439e9cb7a0ce3379f1ec
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
