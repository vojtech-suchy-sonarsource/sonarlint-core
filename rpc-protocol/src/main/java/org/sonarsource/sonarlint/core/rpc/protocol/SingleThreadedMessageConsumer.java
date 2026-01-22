/*
ACR-d1d0e1ce8a68482f855872cb67f90a08
ACR-36d7c6c17e014c3086d6137af488c22d
ACR-2cbe6154fa1e483386d3b421afd7ff55
ACR-522f1dd327994b71b669eb78fedd270d
ACR-4c9c2bccc6854267b4b10cc078eb037a
ACR-8a58ec07ed3e490790632d25a79d0fbc
ACR-89f497bd199440749dd03401455fbef3
ACR-54336aac203f49ba9e589977e4db5103
ACR-90bd02b9211b4677b22d62da047a9580
ACR-83053551f18e424280dddedd363bb0f6
ACR-62d7ed493be144fc827b287b86cdda26
ACR-72b09fe37cec465cbbe6cd4078159303
ACR-5cbfafdd1ce7488cb13c7fa5cc319049
ACR-d3aeacf13fe745ed871a8e7c6dce855c
ACR-a90a9a0b29e8496399c54ef389324f0b
ACR-07c14a333d3946c783912156c310d0f8
ACR-b426a73c19f94e38bf8dac3e9ce11181
 */
package org.sonarsource.sonarlint.core.rpc.protocol;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import org.eclipse.lsp4j.jsonrpc.JsonRpcException;
import org.eclipse.lsp4j.jsonrpc.MessageConsumer;
import org.eclipse.lsp4j.jsonrpc.MessageIssueException;
import org.eclipse.lsp4j.jsonrpc.messages.Message;

/*ACR-81017fd2ded14529a457198a65c927e7
ACR-905ecdfb593844ae8051bb2d5810fda9
ACR-1fd5c3e2361f4d54b9432701fe9755e0
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
