/*
ACR-44577b1e3def4bff8da28c5505200bd7
ACR-7a7012dc2cd54f3eab6c9e49ded10e02
ACR-398e97ba4efb44dfbdad41ada77b3d7d
ACR-ab6175bd60b14d9c9f25988b366f0d3b
ACR-7fc6e4876fbe46ebad9b9394100ecf93
ACR-55b19b6ac1874b7d8faf063c156daccf
ACR-0268594b1e194916857ceabb142edf36
ACR-ab6cd7609a914dac920e5d0bce5e0485
ACR-522dd65ca39e4da4bbee3edcce670c9a
ACR-f89a18fc1d88447cad06a4ba53b216b8
ACR-443bd6eff17245fea99df23edaca4c33
ACR-57f29a9ed35c4ce2891a0a6afcd9f541
ACR-64c8c355e39e4038ae4bbb6fa0000bd5
ACR-4eaebd925052470dae024cb7201b4de6
ACR-e4288299440d4fb088398ce1dd4072fc
ACR-972a1a3211d54bad9ae3cf25dcf50823
ACR-b9d8ba48555d4620a450422f4b25d03a
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
