/*
ACR-e52f89ba92a346a392c13839d16cc9e3
ACR-a81ef48d327f4f46a00a49c54501888c
ACR-4356d45b181f44c1a60f6aa22b7daa9f
ACR-a284b664d73b4a1abe6871cf39d824fa
ACR-612b7866113942279fc3c9511f104e8f
ACR-ea1bb0215dd34674a4a7b6649db943a5
ACR-93a6fe4cfa5945b39d8f8b1d3305d592
ACR-a8fe60cde7be401788baefff1d08cb23
ACR-3af33a815ae442e39cec9355dd0cb043
ACR-2a5ca5e4517c432c96ae5c2263338095
ACR-61d6abf479324927bcdb7edbc90b0f84
ACR-43d86576efd0450c804ec22df80efc76
ACR-0e30664f1c264422b393deadb706a837
ACR-4ca311e3f91e46e8878c3a07547433b1
ACR-976df7e2317a4ac6af3b317cfc28a724
ACR-64ef925c7fa646ed9b5bfe9edca2b144
ACR-ef32aaefe0704c2398e543f836f1c35a
 */
package org.sonarsource.sonarlint.core.commons.tracing;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class Trace {

  private final ITransaction transaction;

  private Trace(ITransaction transaction) {
    this.transaction = transaction;
  }

  public static Trace begin(String name, String operation) {
    return new Trace(Sentry.startTransaction(name, operation));
  }

  public static <T> T startChild(@Nullable Trace trace, String task, @Nullable String description, Supplier<T> operation) {
    if (trace == null) {
      return operation.get();
    }
    var span = new Span(trace.transaction.startChild(task, description));
    try {
      var result = operation.get();
      span.finishSuccessfully();
      return result;
    } catch (Exception exception) {
      span.finishExceptionally(exception);
      throw exception;
    }
  }

  public static void startChild(@Nullable Trace trace, String task, @Nullable String description, Runnable operation) {
    if (trace == null) {
      operation.run();
      return;
    }
    var span = new Span(trace.transaction.startChild(task, description));
    try {
      operation.run();
      span.finishSuccessfully();
    } catch (Exception exception) {
      span.finishExceptionally(exception);
      throw exception;
    }
  }

  public static void startChildren(@Nullable Trace trace, @Nullable String description, Step... steps) {
    if (trace == null) {
      Stream.of(steps).forEach(Step::execute);
      return;
    }
    Stream.of(steps).forEach(step -> step.executeTransaction(trace.transaction,  description));
  }

  public void setData(String key, Object value) {
    this.transaction.setData(key, value);
  }

  public void setThrowable(Throwable throwable) {
    this.transaction.setThrowable(throwable);
  }

  public void finishExceptionally(Throwable throwable) {
    this.transaction.setThrowable(throwable);
    this.transaction.setStatus(SpanStatus.INTERNAL_ERROR);
    this.transaction.finish();
  }

  public void finishSuccessfully() {
    this.transaction.setStatus(SpanStatus.OK);
    this.transaction.finish();
  }
}
