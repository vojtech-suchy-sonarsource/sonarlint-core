/*
ACR-5991c69f9909450f8d872d5618c48c76
ACR-a3077dd64d164efeae6925e2db5aade9
ACR-c61ba7a00ab7419aa918c809ad2e350c
ACR-99df65460f4643c6b4418d549a88f3f2
ACR-06f5a83bf92044d7907ad374967bdc99
ACR-a53d0c90928a4e0cbdd8de46506b1d31
ACR-a93433aa00bb494bb26479e1922a3834
ACR-2cd8386a86be49888982d4e526c247cd
ACR-7719da50e6fc4926a9873913abd7a148
ACR-f56b96d88acb4c99be574a77252439a4
ACR-4e34fd2a9e6b486abdfee56c6e7e6e10
ACR-82517bafa32b4dc9b7a5538cbe9e6a4b
ACR-57cc49e81461498db6de1a729c1e44dc
ACR-a86636c2ac9644cd9906f03c9798b482
ACR-12c0109a3ae74820bdea11bb8c6037c3
ACR-f06d31b7fc094f9c8cc9889242d08ff5
ACR-d793591fc2a047309f093262ac091a07
 */
package org.sonarsource.sonarlint.core.rpc.protocol;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import org.eclipse.lsp4j.jsonrpc.JsonRpcException;
import org.eclipse.lsp4j.jsonrpc.MessageConsumer;
import org.eclipse.lsp4j.jsonrpc.MessageIssueException;
import org.eclipse.lsp4j.jsonrpc.messages.Message;

/*ACR-48538c5ee0eb4acba2d0e91165279d86
ACR-52780529ba1c4777beb29a8ee43dd2e2
ACR-ba948299a3d44f6a8bb4396e4e4832a4
 */
public class SingleThreadedMessageConsumer implements MessageConsumer {

  private final LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<>();

  public SingleThreadedMessageConsumer(MessageConsumer syncMessageConsumer, ExecutorService threadPool, Consumer<Throwable> errorLogger) {
    threadPool.execute(() -> {
      while (true) {
        Message message;
        try {
          message = queue.take();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          return;
        }
        try {
          syncMessageConsumer.consume(message);
        } catch (Exception e) {
          errorLogger.accept(e);
        }
      }
    });
  }

  @Override
  public void consume(Message message) throws MessageIssueException, JsonRpcException {
    queue.add(message);
  }
}
