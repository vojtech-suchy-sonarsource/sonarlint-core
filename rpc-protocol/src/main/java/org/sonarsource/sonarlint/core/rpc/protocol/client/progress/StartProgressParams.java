/*
ACR-f51731ec0b504185b5bde5a6c111d3f9
ACR-e8937c1201c84b6db42c1343150ddff8
ACR-cd4f2f93f300413a8923f1d5e999f131
ACR-0e0c6a8931f14496b19762545cadf685
ACR-d07b234402e044b9acb7dafec90e3ad4
ACR-3fbcc3e5a0994b44b876ab1641bad3c7
ACR-8ce8da51ee004a37a8fab95de0a38d13
ACR-c58863f654bd40a0a943b95218c0d5b6
ACR-5e13f758e9154b81b104d68d0445cd19
ACR-b1d845d75f2f431db3b3d496e99296d1
ACR-ed0ce84821d141958b97273f48b8de2c
ACR-a3ae8591e8b44f77875e9e1cdb0a54e5
ACR-832d81b3d4d943a18b8018af54825aeb
ACR-e260434337c3474e84e5d09625e60107
ACR-e53102aa29214dddbc35dc857d66c180
ACR-5f015edd00f743d5a370eeb005bd1718
ACR-7f89855afd3b49ce8b82f8890fbb3d45
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.progress;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.progress.CancelTaskParams;

public class StartProgressParams {
  /*ACR-3281403fddb14c938d996cc52d86a49a
ACR-378a4973ac8142bd90c8dfb26f0abd66
ACR-675b3e05aa944ed8afb60d92fe51ddcf
ACR-b3fa16391fc34dd690995435aa4e86a9
   */
  private final String taskId;
  /*ACR-f8ebf0c5c0294b03bedd4d3352ec90c9
ACR-f5d794496ea247069a0e192db2b83d3d
ACR-dc39a1b34eec4e5d82996e348abfe0f3
ACR-c23b4fc414c74f4b9c062af7801370e3
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
