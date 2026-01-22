/*
ACR-b5eff88d13f8432391508a781d77a292
ACR-2708ac63a50a40b589cf92b09f30b4e9
ACR-76bbf82eb7044e5a92c89c01337c35ab
ACR-e8620b05007c45abb3ba596be74b798b
ACR-47e0f3524b2040a09f8eb337cf82f0b8
ACR-517af3e3723c45968924a56fa9a3ab39
ACR-6002a6c4a540451da80c76fd1fd8bac4
ACR-419d67c030a74544b7829701821ac627
ACR-f551c05fe9604e61853c1f1dfb516b30
ACR-03554b18c64f4d1fb7b286c41c47f543
ACR-0ec30b8bb6c64b418aeac2f146215e92
ACR-2afd7889340e4c3980e00f840100b7e7
ACR-bf27b45a55fb41eca11e0dc1a5638a3c
ACR-47c2ff18982b4da698f74cd95c2e6683
ACR-7e8e5c541f9245358981c35778e9b0e4
ACR-4f279479f0f24f148e197dd86d77c49d
ACR-d060f67e634c4743bae7b80e70c4fde3
 */
package org.sonarsource.sonarlint.core.rpc.protocol;

public class SonarLintRpcErrorCode {

  public static final int CONNECTION_NOT_FOUND = -1;
  public static final int CONFIG_SCOPE_NOT_FOUND = -2;
  public static final int RULE_NOT_FOUND = -3;
  public static final int BACKEND_ALREADY_INITIALIZED = -4;
  public static final int ISSUE_NOT_FOUND = -5;
  public static final int CONFIG_SCOPE_NOT_BOUND = -6;
  public static final int HTTP_REQUEST_TIMEOUT = -7;
  public static final int HTTP_REQUEST_FAILED = -8;
  public static final int TASK_EXECUTION_TIMEOUT = -9;
  public static final int PROGRESS_CREATION_FAILED = -10;
  public static final int CONNECTION_KIND_NOT_SUPPORTED = -11;
  public static final int FILE_NOT_FOUND = -12;
  public static final int TOO_MANY_REQUESTS = -13;
  public static final int UNAUTHORIZED = -14;
  public static final int INVALID_ARGUMENT = -15;
}
