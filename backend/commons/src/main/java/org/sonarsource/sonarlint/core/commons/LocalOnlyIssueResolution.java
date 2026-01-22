/*
ACR-dc86a0098b7f4f368cde57f1bb32ce13
ACR-272019a9faa848e28e41ab0f38ab7f0e
ACR-68d4e752835b4a0cb4d348e98d16294b
ACR-47e775b5cf9743c69fe427623ec10503
ACR-514b65da6f814f81bd679547417af087
ACR-07078ccbdc104caab1714b02b48b0f13
ACR-4d8fbc827bff4a1cba5a0e3fa6a4b757
ACR-0df8988b82e446fa87996afde875b750
ACR-ea12ef8864244be09db421a4a77e0895
ACR-06c70d54c1004715b78e7b81a1a4dcc6
ACR-d371d7546a4c492da1b1bee68122b2ec
ACR-8171776c4de44b138fe54c7af75fcc4b
ACR-bb3350bdeee149d4997be0014f5aa4f2
ACR-469e712545b9404f97fa1267e783c6fa
ACR-bc89a2e9188747cb9699df83355dfda4
ACR-8c22c8ec199e475bb61b50243b51cf6b
ACR-db1abc9bb3584c43b43d270a15a07510
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
