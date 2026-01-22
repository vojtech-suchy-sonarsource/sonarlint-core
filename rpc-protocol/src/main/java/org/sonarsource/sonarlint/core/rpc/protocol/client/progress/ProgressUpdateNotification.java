/*
ACR-08ce24ee1ccd4f90b33df6e82d24f158
ACR-0e7d3ae6489b420a8c727ed1c4f71d1b
ACR-bc343302be324dfa8db73f4419358fd1
ACR-50d14ff3b4454eada1227f17a38e597c
ACR-609f000faca54a44858136056369278b
ACR-77fb0326f1e044ab803b735d5b0cf754
ACR-a9d0ff57760943e1964f72112a78fa23
ACR-a32d6e29dcdd463a8499e6baced207d2
ACR-bb61a8d031404386bdefcfe06ec01297
ACR-c3bf4ac991c44953ad47545e3874a1f9
ACR-1f0ffb29193848649289bae9f655c941
ACR-85342203a1f0499fbced1e28047ab9bd
ACR-8ddc2d7ba3ee4045890bacd237ad5b91
ACR-132687ca05bb4eb79958b0a78e4cacb3
ACR-79d923528c36472cb77e39764e50fcc2
ACR-05dd2d0f65de48929ae947b0d8e76fa4
ACR-e6f7a985de4446459a7d383c3fd95641
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.progress;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class ProgressUpdateNotification {
  /*ACR-d1835e64dbc245ac8f23f055bf113705
ACR-1e2d062238114d049981f713251a967b
   */
  private final String message;
  /*ACR-580f859d0f7d4fa19109ee14f8473833
ACR-86f321cba3ad418e8ce22f3b4ff52e87
ACR-f699bcf500564676941ec49721fefd4f
   */
  private final Integer percentage;

  public ProgressUpdateNotification(@Nullable String message, @Nullable Integer percentage) {
    this.message = message;
    this.percentage = percentage;
  }

  @CheckForNull
  public String getMessage() {
    return message;
  }

  @CheckForNull
  public Integer getPercentage() {
    return percentage;
  }
}
