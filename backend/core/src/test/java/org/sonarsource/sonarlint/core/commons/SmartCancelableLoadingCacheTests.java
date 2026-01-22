/*
ACR-1884acd83cb44f5ab410e8854ba63930
ACR-bb5de61840ad4d638bbad79146f9d99d
ACR-e1bc8ab81cc54dce84b4e4afe4b54b0d
ACR-a247d099b6e047b2af79ee126bc6465b
ACR-cba1a38c53a243769c0345a5fe438d7e
ACR-ecbddbe9b0f5434fa54b6510377d50a4
ACR-8a4d66b3f5824698bce49de9f19005c4
ACR-f56805b2ce9c4e4f9ea2024d7137fa1d
ACR-83438cc94bdb431dbb38c0f881907d1d
ACR-63d391eb4f0b4ce7b39bfc4b8a04bb85
ACR-9cbcc7e48a7549c8bfe53fb59493d5d2
ACR-6c1c6316a7e94438b6ba8087c55aa38c
ACR-ecfa06bc9f914123b1b9c871489d9827
ACR-1a6303aa51814316aebd129140f588b9
ACR-f0571934f04f4d989b569e3fd6915d62
ACR-b8432fa610314c69b519ac57f864ff39
ACR-2bf73f1515134246b025277519cdfb97
 */
package org.sonarsource.sonarlint.core.commons;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.stubbing.Answer;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import testutils.TakeThreadDumpAfter;
import testutils.ThreadDumpExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(ThreadDumpExtension.class)
class SmartCancelableLoadingCacheTests {

  public static final String ANOTHER_VALUE = "anotherValue";
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester(true);

  public static final String A_VALUE = "aValue";
  public static final String A_KEY = "aKey";
  public static final String ANOTHER_KEY = "anotherKey";
  private final SmartCancelableLoadingCache.Listener<String, String> listener = mock(SmartCancelableLoadingCache.Listener.class);
  private final BiFunction<String, SonarLintCancelMonitor, String> computer = mock(BiFunction.class);
  private final SmartCancelableLoadingCache<String, String> underTest = new SmartCancelableLoadingCache<>("test", computer, listener);

  @AfterEach
  void close() {
    underTest.close();
  }

  @Test
  @TakeThreadDumpAfter(seconds = 10)
  void should_cache_value_and_notify_listener_once() {
    when(computer.apply(eq(A_KEY), any(SonarLintCancelMonitor.class))).thenReturn(A_VALUE);

    assertThat(underTest.get(A_KEY)).isEqualTo(A_VALUE);
    assertThat(underTest.get(A_KEY)).isEqualTo(A_VALUE);
    assertThat(underTest.get(A_KEY)).isEqualTo(A_VALUE);

    verify(listener).afterCachedValueRefreshed(A_KEY, null, A_VALUE);
    verify(computer, times(1)).apply(eq(A_KEY), any());
  }

  @Test
  @TakeThreadDumpAfter(seconds = 10)
  void should_wait_for_long_computation() {
    when(computer.apply(eq(A_KEY), any(SonarLintCancelMonitor.class))).thenAnswer(invocation -> {
      Thread.sleep(100);
      return A_VALUE;
    });

    assertThat(underTest.get(A_KEY)).isEqualTo(A_VALUE);
  }

  @Test
  @TakeThreadDumpAfter(seconds = 10)
  void should_throw_if_failure_while_loading() {
    when(computer.apply(eq(A_KEY), any(SonarLintCancelMonitor.class))).thenThrow(new RuntimeException("boom"));

    assertThrows(RuntimeException.class, () -> underTest.get(A_KEY));
    assertThrows(RuntimeException.class, () -> underTest.get(A_KEY));
    assertThrows(RuntimeException.class, () -> underTest.get(A_KEY));

    verify(computer, times(1)).apply(eq(A_KEY), any());
    verifyNoInteractions(listener);
  }

  @Test
  @TakeThreadDumpAfter(seconds = 10)
  void should_refresh_value() {
    when(computer.apply(eq(A_KEY), any(SonarLintCancelMonitor.class)))
      .thenReturn(A_VALUE)
      .thenReturn(ANOTHER_VALUE);

    assertThat(underTest.get(A_KEY)).isEqualTo(A_VALUE);
    verify(listener).afterCachedValueRefreshed(A_KEY, null, A_VALUE);

    underTest.refreshAsync(A_KEY);

    verify(listener, timeout(1000)).afterCachedValueRefreshed(A_KEY, A_VALUE, ANOTHER_VALUE);
    assertThat(underTest.get(A_KEY)).isEqualTo(ANOTHER_VALUE);

    verify(computer, times(2)).apply(eq(A_KEY), any());
  }

  @Test
  @TakeThreadDumpAfter(seconds = 10)
  void should_cancel_previous_computation_on_refresh() throws InterruptedException {
    var firstComputationStarted = new CountDownLatch(1);
    var cancelled = new AtomicBoolean();
    when(computer.apply(eq(A_KEY), any(SonarLintCancelMonitor.class)))
      .thenAnswer(waitingForCancellation(firstComputationStarted, cancelled))
      .thenReturn(ANOTHER_VALUE);

    //ACR-3104f563e7dd4a6988f1a14f35ec6b36
    underTest.refreshAsync(A_KEY);
    firstComputationStarted.await();

    //ACR-7fa805fd0ad0449f95e74e156ba268b9
    underTest.refreshAsync(A_KEY);

    assertThat(underTest.get(A_KEY)).isEqualTo(ANOTHER_VALUE);
    assertThat(cancelled.get()).isTrue();

    verify(computer, times(2)).apply(eq(A_KEY), any());
  }

  @Test
  @TakeThreadDumpAfter(seconds = 10)
  void should_cancel_previous_computation_on_clear() throws InterruptedException {
    var firstComputationStarted = new CountDownLatch(1);
    var cancelled = new AtomicBoolean();
    when(computer.apply(eq(A_KEY), any(SonarLintCancelMonitor.class)))
      .thenAnswer(waitingForCancellation(firstComputationStarted, cancelled));

    //ACR-82642a7527b64311bc7d748f8c98554b
    underTest.refreshAsync(A_KEY);
    firstComputationStarted.await();

    underTest.clear(A_KEY);

    await().untilAsserted(() -> assertThat(cancelled.get()).isTrue());

    verify(computer, times(1)).apply(eq(A_KEY), any());
  }

  @Test
  @TakeThreadDumpAfter(seconds = 10)
  void should_cancel_all_previous_computation_on_close() throws InterruptedException {
    var key1ComputationStarted = new CountDownLatch(1);
    var cancelledKey1 = new AtomicBoolean();
    when(computer.apply(eq(A_KEY), any(SonarLintCancelMonitor.class)))
      .thenAnswer(waitingForCancellation(key1ComputationStarted, cancelledKey1));

    //ACR-563e8713fdcf4015b4761f309da9d1b7
    underTest.refreshAsync(A_KEY);
    key1ComputationStarted.await();

    var key2ComputationStarted = new CountDownLatch(1);
    var cancelledKey2 = new AtomicBoolean();
    when(computer.apply(eq(ANOTHER_KEY), any(SonarLintCancelMonitor.class)))
      .thenAnswer(waitingForCancellation(key2ComputationStarted, cancelledKey2));

    //ACR-ee36a9fd5a13448d8ae5815555b74e5c
    underTest.refreshAsync(ANOTHER_KEY);

    underTest.close();

    await().untilAsserted(() -> assertThat(cancelledKey1.get()).isTrue());
    //ACR-59f4b32a5f7f4abfbf8e386bb4ea8da5
    await().untilAsserted(() -> assertThat(key2ComputationStarted.getCount()).isEqualTo(1));

    verify(computer, times(1)).apply(eq(A_KEY), any());
    verifyNoMoreInteractions(computer);
  }

  @Test
  @TakeThreadDumpAfter(seconds = 10)
  void previously_queued_get_should_receive_latest_value_on_cancellation() throws InterruptedException {
    var firstComputationStarted = new CountDownLatch(1);
    var cancelled = new AtomicBoolean();
    when(computer.apply(eq(A_KEY), any(SonarLintCancelMonitor.class)))
      .thenAnswer(waitingForCancellation(firstComputationStarted, cancelled))
      .thenReturn(ANOTHER_VALUE);

    //ACR-9119d698cd214257b2157a789abaf296
    AtomicReference<String> value = new AtomicReference<>();
    var t = new Thread(() -> {
      value.set(underTest.get(A_KEY));
    });
    t.start();
    firstComputationStarted.await();

    //ACR-ecb63e258f7e4d7c9761c4081cd6388e
    underTest.refreshAsync(A_KEY);

    assertThat(underTest.get(A_KEY)).isEqualTo(ANOTHER_VALUE);
    t.join();
    assertThat(value.get()).isEqualTo(ANOTHER_VALUE);
    assertThat(cancelled.get()).isTrue();
  }


  @Test
  @TakeThreadDumpAfter(seconds = 10)
  void should_notify_once_in_case_of_cancellation() throws InterruptedException {
    var firstComputationStarted = new CountDownLatch(1);
    when(computer.apply(eq(A_KEY), any(SonarLintCancelMonitor.class)))
      .thenAnswer(waitingForCancellation(firstComputationStarted, null))
      .thenReturn(ANOTHER_VALUE);

    underTest.refreshAsync(A_KEY);
    firstComputationStarted.await();

    underTest.refreshAsync(A_KEY);

    verify(listener, timeout(1000).times(1)).afterCachedValueRefreshed(A_KEY, null, ANOTHER_VALUE);
    verifyNoMoreInteractions(listener);
  }

  @Test
  void should_notify_once_if_multiple_refresh() {
    when(computer.apply(eq(A_KEY), any(SonarLintCancelMonitor.class)))
      .thenReturn(A_VALUE)
      .thenReturn(ANOTHER_VALUE);

    assertThat(underTest.get(A_KEY)).isEqualTo(A_VALUE);
    verify(listener, timeout(1000).times(1)).afterCachedValueRefreshed(A_KEY, null, A_VALUE);

    underTest.refreshAsync(A_KEY);
    underTest.refreshAsync(A_KEY);
    underTest.refreshAsync(A_KEY);
    underTest.refreshAsync(A_KEY);
    underTest.refreshAsync(A_KEY);

    verify(listener, timeout(1000).times(1)).afterCachedValueRefreshed(A_KEY, A_VALUE, ANOTHER_VALUE);
    verifyNoMoreInteractions(listener);
  }


  private static Answer<String> waitingForCancellation(CountDownLatch startedLatch, @Nullable AtomicBoolean wasCancelled) {
    return invocation -> {
      var cancelChecker = (SonarLintCancelMonitor) invocation.getArgument(1);
      startedLatch.countDown();
      while (!cancelChecker.isCanceled()) {
        Thread.sleep(100);
      }
      if (wasCancelled != null) {
        wasCancelled.set(true);
      }
      throw new CancellationException();
    };
  }

}
