/*
ACR-34a91a51bc3848b892f15356b35a0dfc
ACR-4ef4eb49b85c4ecbb4d76039f2ffc5be
ACR-731ca8002f194953b87851e2bfc625aa
ACR-837573a1379e45578787b7ac8bb42197
ACR-01fedce167e74143aef9ba617399a75e
ACR-17698a6faa0b4095ac95cb7fa4186d8c
ACR-d4e1b422069d4d4085d00c39bdc191bc
ACR-049bba2caa554fbcbe9c5848ea0f28ae
ACR-85a79a400ad0413380563bd1d36246d3
ACR-faba9a5af90847c6b73a4f9e2d02ff0d
ACR-a599821f2e4a4c30bc15ae9a8bd14e27
ACR-9b36721a85604dad8075d0e0b9c14458
ACR-49091d34ebd14908930b2f7f661d79ff
ACR-7eb659142ea346a69b18192dac9c4b2c
ACR-89110e682b554e20b3dd9337d174ee34
ACR-036e63f0a0e34350911e4abd358dd50e
ACR-b6db8ada571540dfbe83b48e6893fc68
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
