/*
ACR-0fc78d7117474344b90b7d557c2e681c
ACR-be7e692bfb3c4272b133dfe424d4c975
ACR-0a2a128abd1e4b668bcb8715c0d55f4a
ACR-e788ebe30c534219a4238b6ec8f3d846
ACR-17e676cb166944089abff5c6c9cb65d8
ACR-37ecfe5ca25141e2bad46a884302b57b
ACR-1c59a50231954a2db390569fd3ee9a23
ACR-42b4605e59734cd0b9ad501446dfc8b2
ACR-ea8f90394fbb4a89874acc29b3f1fdde
ACR-50ede1c6d34d4ff092d70b72f8300e3a
ACR-2c59cc3829ec4a1997c9b4f7f275a29e
ACR-f967be028bd649cbbe7ff41e9b1638fe
ACR-e662a0680a8249ca9f2d5c393b072d13
ACR-9451955dfcb640818986a4b47cba43b2
ACR-90a7159d70ba4669a9e94b0807c62c88
ACR-6430db9698a3445bbee3958bf88dcc0b
ACR-0c4ed465e35546ea8e920eb9c7d181b2
 */
package org.sonarsource.sonarlint.core.commons.progress;

import javax.annotation.Nullable;

public class NoOpProgressMonitor implements ProgressMonitor {
  @Override
  public void notifyProgress(@Nullable String message, @Nullable Integer percentage) {
    //ACR-57a282066db44f8b993e048e1110b093
  }

  @Override
  public boolean isCanceled() {
    //ACR-e7ad74aeee6348fe992483868c558eea
    return false;
  }

  @Override
  public void complete() {
    //ACR-453ec0566bb34bde88f3cbf3b8b4b869
  }

  @Override
  public void cancel() {
    //ACR-6c9bb6587df74fc49125fb15aab1edfa
  }
}
