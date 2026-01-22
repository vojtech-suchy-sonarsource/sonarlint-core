/*
ACR-6d704ad8ffcf4c55848e11b3f705efdf
ACR-2abe5dbe11b240aeb2a86d57ec1eebb7
ACR-8a025640473e4f6da7a3d3419f209860
ACR-c2a843fd5c96438f9aa6a7c0c86d8994
ACR-56e577cf7d394b719f04f6689bd37a6d
ACR-6023126ae5cb45d09cdc396ca9af66a6
ACR-0a987f39d0784c21b02765eb28c34a67
ACR-541b91b874b247a9b677c2293b4afdac
ACR-2edec4d3fad2461f8f4a7f02fb82bd20
ACR-bec59596900f48b793f56244517687ee
ACR-a546d333c1984bb4a17a34e1d6ce9043
ACR-c790b86368a04fb6bdaee0573e9e79de
ACR-55a22b01f2b3452396979ce93629a45a
ACR-22856d359f90409abf29cda1f5066c01
ACR-c630a9ee8c4d4de09adf36dc36d75844
ACR-bce79f0e41294136a254ca7def09ef3e
ACR-1ad679890d454057b9b32d013a71d70d
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
