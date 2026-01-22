/*
ACR-c91c4490d2234e0fb1c2766fbd0970bd
ACR-8ceef34443294415a35579b58b1e5065
ACR-bf402fa105d8468dbf034e775041c21b
ACR-0242264112744b0aa7788fa59d2a9146
ACR-882ac9428e7e443aa7880ed3142ba7a9
ACR-1c7d538f3c744c9c92a5ddcc61d29e31
ACR-b8e39814d8d44f1da798822441c01ca6
ACR-294ba73a4a7c46b28afce79227ffe9d5
ACR-97b63cd66531489aab733cf8d90d3852
ACR-45f5112ff8cd43979a33817f4496d6cb
ACR-9ff21e2194b6406ba8566832f123ad57
ACR-5c1282f2cde64cc08519aa8a2ef26905
ACR-e14ae0fa089c4a4888d82f7b3b636886
ACR-25a8a53879c84b24b8257457d38309ad
ACR-7449673ff54647ac9b2668493293a73a
ACR-de7a94d69fea4279a570945139458a18
ACR-2850249093744f7f9cd0969487aabd53
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

    //ACR-1423dd76c87c4f52b682dde1dac26209
    underTest.refreshAsync(A_KEY);
    firstComputationStarted.await();

    //ACR-d7407e56b4a3460da709d97338171253
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

    //ACR-475cf3e4685847fe9e21d550a3ca45b3
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

    //ACR-7f3ce55840ab474a8cb3cd9c1a495ef6
    underTest.refreshAsync(A_KEY);
    key1ComputationStarted.await();

    var key2ComputationStarted = new CountDownLatch(1);
    var cancelledKey2 = new AtomicBoolean();
    when(computer.apply(eq(ANOTHER_KEY), any(SonarLintCancelMonitor.class)))
      .thenAnswer(waitingForCancellation(key2ComputationStarted, cancelledKey2));

    //ACR-183be99d4e894f76a6c7e984f2c08d15
    underTest.refreshAsync(ANOTHER_KEY);

    underTest.close();

    await().untilAsserted(() -> assertThat(cancelledKey1.get()).isTrue());
    //ACR-5516ab434d12479db9c55ca1b8c0541c
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

    //ACR-2c906a17850945828c107e878d08b08f
    AtomicReference<String> value = new AtomicReference<>();
    var t = new Thread(() -> {
      value.set(underTest.get(A_KEY));
    });
    t.start();
    firstComputationStarted.await();

    //ACR-4d5031744c8e4891a9edee83d9390496
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
