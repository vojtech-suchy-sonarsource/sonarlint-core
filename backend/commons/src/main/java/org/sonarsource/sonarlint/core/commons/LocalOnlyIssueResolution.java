/*
ACR-db543792f1dd4f92950bbf69cd82eeaa
ACR-0cdd4c560ebf47fb9db4204504ad9f31
ACR-9b27c3a9282043acb1ec5239e707e70b
ACR-333aa901db9c438db757b6189c606b62
ACR-71ec73686b4f4ea4a62192aac619d0f5
ACR-76a0c0fd7c8c4ec388ff026fd7712e03
ACR-0024e016b7e34f9b9ac8550393407b38
ACR-63d39c165b634997acee28ca7f037988
ACR-5c2c2697af7e461a8a5a9d4a701d48eb
ACR-5e6731810d5e43d29340c6602a25ab2e
ACR-2c8ac8d3cf3c4bf5b8c030269263cf9d
ACR-f3ebb5aeffdf45eba047334d50574738
ACR-d948b85b540e49b7b01eed6efe73c19f
ACR-03adde7728434aaeba15127e1758e987
ACR-b1cf87c70c344bc79e9ae4b9312b03e9
ACR-123fe96f390e45459bf36a35eb36f42a
ACR-19961565575745b78711e1728c4139c0
 */
package org.sonarsource.sonarlint.core.commons;

import java.time.Instant;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class LocalOnlyIssueResolution {
  private final IssueStatus resolutionStatus;
  private final Instant resolutionDate;
  private String comment;

  public LocalOnlyIssueResolution(IssueStatus status, Instant resolutionDate, @Nullable String comment) {
    this.resolutionStatus = status;
    this.resolutionDate = resolutionDate;
    this.comment = comment;
  }

  public IssueStatus getStatus() {
    return resolutionStatus;
  }

  public Instant getResolutionDate() {
    return resolutionDate;
  }

  @CheckForNull
  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
}
