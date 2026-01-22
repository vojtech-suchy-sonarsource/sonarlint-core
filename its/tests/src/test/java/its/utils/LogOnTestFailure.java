/*
ACR-0302c8b7114b4165a284c2df3af74bb7
ACR-7a26bdc2194a4ab698a93924bb618707
ACR-3691ff9e21634cd9be9e0a9e75ed740c
ACR-46ef4783ad9149dc9abd539cd9dfe32c
ACR-2991269727764497b125c4d429adbd5c
ACR-13e3a8dfef544b03ac43bf634f437655
ACR-a4c07360de564226a25a123ee1b283e8
ACR-249455d1aeee491b887d2dda2c58e700
ACR-005c4e023a964a9ebc66d1f64c398535
ACR-0e18c6bff0c746b7a762701c95219316
ACR-d58318185418453e99907b596a0f52e1
ACR-d24db78218e647f69fa2b420942506c6
ACR-9b9676df158740119a5e9266f5e15c26
ACR-a3db2993c44546a5b46e83951f1a0500
ACR-9e9775b9de114476bb643ccb7a7d79a1
ACR-3daca83e14874b7bb5cc50511c70c8e3
ACR-ba12b46434d345159425c193a25e063d
 */
package its.utils;

import java.util.Queue;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.sonarsource.sonarlint.core.rpc.protocol.client.log.LogParams;

public class LogOnTestFailure implements TestWatcher {

  private final Queue<LogParams> logs;

  public LogOnTestFailure(Queue<LogParams> logs) {
    this.logs = logs;
  }

  @Override
  public void testFailed(ExtensionContext context, Throwable cause) {
    System.out.println("Test failed: " + context.getDisplayName());
    System.out.println("Client RPC logs: ");
    logs.forEach(l -> System.out.println("  " + l));
  }
}
