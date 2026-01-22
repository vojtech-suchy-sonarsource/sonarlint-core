/*
ACR-ce85bfe158174802befbfc818162a276
ACR-e57038c30f7b4811a3ee96992edd8e48
ACR-623a636ff2b84e228cfdebd3f849dcce
ACR-dd796914fb91468794689229d50689ac
ACR-c8ad95a579e948909c6b9812a1571935
ACR-2e2d0233878b4622a181bf89c2dfd866
ACR-a3c83cd5e8ce4eb4b43792dec310ac58
ACR-742e0ba28b164aeaa834de4dca8753ef
ACR-aac27f81a6284e4daa496f411d50ee16
ACR-00be0f2eca3b4b7180dc875a01cd737f
ACR-21cb40bbcdda4073a5117c98eb8aa6cf
ACR-93ec7ed65306461f8dd9baee54ebdce2
ACR-5379ab4182f74b3d8010b582427d2dc7
ACR-f8ad3132aba44d02924a305adc5ea50e
ACR-90ec5f30acf64b05a17341006fb1e89d
ACR-c25d878b5741491ca0b62a2a7f610cfe
ACR-689f8dc5b09e4a41af2953331d897f22
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.progress;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.progress.CancelTaskParams;

public class StartProgressParams {
  /*ACR-a94b3cba650945aaabde8e2b6f443acb
ACR-75572c2daa8d431abb5d99ea9f4c8919
ACR-b3084f0354e14df2a331c4fa23454dac
ACR-67a32c830ef4412cb96adfa99bbcdaa6
   */
  private final String taskId;
  /*ACR-091161313e524e488941466b0f2f6177
ACR-e50b36002bae4d2bb644fac2e6c6dee9
ACR-fb918d3522a64796bd3a000e087719bc
ACR-d75f97aaf6134daf896a4c6f72980c77
   */
  private final String configurationScopeId;
  private final String title;
  private final String message;
  private final boolean indeterminate;
  private final boolean cancellable;

  public StartProgressParams(String taskId, @Nullable String configurationScopeId, String title, @Nullable String message, boolean indeterminate,
    boolean cancellable) {
    this.taskId = taskId;
    this.configurationScopeId = configurationScopeId;
    this.title = title;
    this.message = message;
    this.indeterminate = indeterminate;
    this.cancellable = cancellable;
  }

  public String getTaskId() {
    return taskId;
  }

  @CheckForNull
  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public String getTitle() {
    return title;
  }

  @CheckForNull
  public String getMessage() {
    return message;
  }

  public boolean isIndeterminate() {
    return indeterminate;
  }

  public boolean isCancellable() {
    return cancellable;
  }
}
