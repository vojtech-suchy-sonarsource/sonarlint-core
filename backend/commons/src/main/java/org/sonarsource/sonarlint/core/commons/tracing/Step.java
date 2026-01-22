/*
ACR-9e3257e4ae18468c8ba34da3e379d2cb
ACR-573ca7b1f9a1417bbbc4dbd4740f5f2a
ACR-1daa95f0d5674e2f965de951d1f89384
ACR-51b7d39a747b4eb6bd66f44010f5b903
ACR-ce24a987db1b4842801a5d2d811e6fe5
ACR-f2c3722df65d480ebd7aeb17f7250034
ACR-aed79a6332004e42af6fc6476bff2799
ACR-bc5947c4947746148aaa291a0c9d11ab
ACR-75f4e08734704157b7f033b99bdc376e
ACR-26dc5734e2694068babd7ff538d7658c
ACR-d20aee47e1b34adeb16b16ee13d0327c
ACR-bc85a60b8ff44fd797feff63ef478c5c
ACR-70c17dead003452ca14fb9889a469840
ACR-bc9d24a80a85414f86d6e88ce25a8c5b
ACR-dac9ddf1a2cf46e9949d39c86d289147
ACR-c40fd90d224947b1b44993cce7040079
ACR-22a78518fadb4f5eae1c0ab584a3fe9c
 */
package org.sonarsource.sonarlint.core.commons.tracing;

import io.sentry.ITransaction;
import javax.annotation.Nullable;

public class Step {

  private final String task;
  private final Runnable operation;

  public Step(String task, Runnable operation) {
    this.task = task;
    this.operation = operation;
  }

  public void execute() {
    operation.run();
  }

  public void executeTransaction(ITransaction transaction, @Nullable String description) {
    var span = new Span(transaction.startChild(task, description));
    try {
      operation.run();
      span.finishSuccessfully();
    } catch (Exception exception) {
      span.finishExceptionally(exception);
      throw exception;
    }
  }

}
