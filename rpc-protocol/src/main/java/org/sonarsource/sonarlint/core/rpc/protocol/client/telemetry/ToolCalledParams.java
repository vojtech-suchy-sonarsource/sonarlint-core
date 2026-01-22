/*
ACR-c94f4e7457744272ab088a7f6982f317
ACR-b29ed64880d94d71a7ece0fa649d29b9
ACR-316b7a77291e40089a856ef4d69a8b3d
ACR-915fcf3065c84c8aa7c0502a1517671a
ACR-e694049fe716473ea62fc9ec85c17ff8
ACR-2f83edd46eda4922a252a56731d43381
ACR-b78ceb2ea18c4245938a355e21307058
ACR-4b72deb310644d91a72a572a5c9c2bf5
ACR-d78355bfbb144d708dcc1c8467bf39f1
ACR-6cb4909597274b11ac276869f6974dd5
ACR-4773dab3af08475597c9d85eadce9ee0
ACR-788ce01501804ad7bf1c65be3771045e
ACR-baa47993167343fd8409a2bfd161eccf
ACR-8db5e09b2da749c5a792db75899aa2c1
ACR-0039b0e7981f44bb939668db4af776e3
ACR-82909d527a5543548eb0c276bdc4fc68
ACR-a9780be3428b482ab1949f34dfcc93c8
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry;

public class ToolCalledParams {
  private final String toolName;
  private final boolean succeeded;

  public ToolCalledParams(String toolName, boolean succeeded) {
    this.toolName = toolName;
    this.succeeded = succeeded;
  }

  public String getToolName() {
    return toolName;
  }

  public boolean isSucceeded() {
    return succeeded;
  }
}
