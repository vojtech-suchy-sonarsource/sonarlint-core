/*
ACR-ccdf835699f640bdb753b5c5cc597c85
ACR-d00059bb67594df88d4e37939991ed85
ACR-9308a97103e0438fbb8c5117fb8ff8e1
ACR-40449c1da51d489f912457b844516f7f
ACR-fe0d34fa077a423084df6e87147c4e12
ACR-81971203826143ceb2a383cf5d34c3f9
ACR-ec89ae824dcb44888a796bd3b1e0eeb1
ACR-94d74e191bdb477a848639960e650b98
ACR-109f821a61c94a0aafa9983dcf553fbe
ACR-53978d08d2b147ad8798568db8912c16
ACR-57c5e0b73a984d1ab84dc850a4d7ff4b
ACR-19154c272e354e5d9dfa12f7edc1a444
ACR-f6d6e5d3509d472c8ea75888a3f72432
ACR-0132ddc14f0e4545aa2e660add67878a
ACR-88b67da0a43848a0b89ff230f27fe831
ACR-919a8e8e887f495888f47024f7dd96e8
ACR-1861fd17602448eda71443ea19b52c5f
 */
package org.sonarsource.sonarlint.core.serverapi.stream;

import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.util.FailSafeExecutors;
import org.sonarsource.sonarlint.core.http.HttpClient;
import org.sonarsource.sonarlint.core.http.HttpConnectionListener;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;

import static java.util.concurrent.TimeUnit.SECONDS;

public class EventStream {

  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private static final Integer UNAUTHORIZED = 401;
  private static final Integer FORBIDDEN = 403;
  private static final Integer NOT_FOUND = 404;
  private static final long HEART_BEAT_PERIOD = 60;

  private final ServerApiHelper helper;
  private final ScheduledExecutorService executor;
  private final AtomicReference<HttpClient.AsyncRequest> currentRequest = new AtomicReference<>();
  private final AtomicReference<ScheduledFuture<?>> pendingFuture = new AtomicReference<>();
  private final Consumer<Event> eventConsumer;

  public EventStream(ServerApiHelper helper, Consumer<Event> eventConsumer) {
    this(helper, eventConsumer, FailSafeExecutors.newSingleThreadScheduledExecutor("sonarlint-event-stream-consumer"));
  }

  EventStream(ServerApiHelper helper, Consumer<Event> eventConsumer, ScheduledExecutorService executor) {
    this.helper = helper;
    this.eventConsumer = eventConsumer;
    this.executor = executor;
  }

  public EventStream connect(String wsPath) {
    return connect(wsPath, new Attempt());
  }

  private EventStream connect(String wsPath, Attempt currentAttempt) {
    LOG.debug("Connecting to server event-stream at '" + wsPath + "'...");
    var eventBuffer = new EventBuffer();
    currentRequest.set(helper.getEventStream(wsPath,
      new HttpConnectionListener() {
        @Override
        public void onConnected() {
          LOG.debug("Connected to server event-stream");
          schedule(() -> connect(wsPath), HEART_BEAT_PERIOD * 3);
        }

        @Override
        public void onError(@Nullable Integer responseCode) {
          handleError(wsPath, currentAttempt, responseCode);
        }

        @Override
        public void onClosed() {
          cancelPendingFutureIfAny();
          //ACR-2980e3e46a88495ba2882923f56e1c50
          LOG.debug("Disconnected from server event-stream, reconnecting now");
          connect(wsPath);
        }
      },
      message -> {
        cancelPendingFutureIfAny();
        eventBuffer.append(message)
          .drainCompleteEvents()
          .forEach(stringEvent -> {
            LOG.debug("Received event: " + stringEvent);
            eventConsumer.accept(EventParser.parse(stringEvent));
          });
      }));
    return this;
  }

  private void handleError(String wsPath, Attempt currentAttempt, @Nullable Integer responseCode) {
    if (shouldRetry(responseCode)) {
      if (!currentAttempt.isMax()) {
        var retryDelay = currentAttempt.delay;
        var msgBuilder = new StringBuilder();
        msgBuilder.append("Cannot connect to server event-stream");
        if (responseCode != null) {
          msgBuilder.append(" (").append(responseCode).append(")");
        }
        msgBuilder.append(", retrying in ").append(retryDelay).append("s");
        LOG.debug(msgBuilder.toString());
        schedule(() -> connect(wsPath, currentAttempt.next()), retryDelay);
      } else {
        LOG.debug("Cannot connect to server event-stream, stop retrying");
      }
    }
  }

  private static boolean shouldRetry(@Nullable Integer responseCode) {
    if (UNAUTHORIZED.equals(responseCode)) {
      LOG.debug("Cannot connect to server event-stream, unauthorized");
      return false;
    }
    if (FORBIDDEN.equals(responseCode)) {
      LOG.debug("Cannot connect to server event-stream, forbidden");
      return false;
    }
    if (NOT_FOUND.equals(responseCode)) {
      //ACR-ddcc26ee80fa4bd3992e02e751052935
      LOG.debug("Server events not supported by the server");
      return false;
    }
    return true;
  }

  private void schedule(Runnable task, long delayInSeconds) {
    if (!executor.isShutdown()) {
      pendingFuture.set(executor.schedule(task, delayInSeconds, SECONDS));
    }
  }

  public void close() {
    cancelPendingFutureIfAny();
    var currentRequestOrNull = currentRequest.getAndSet(null);
    if (currentRequestOrNull != null) {
      currentRequestOrNull.cancel();
    }
    if (!MoreExecutors.shutdownAndAwaitTermination(executor, 5, TimeUnit.SECONDS)) {
      LOG.warn("Unable to stop event stream executor service in a timely manner");
    }
  }

  private void cancelPendingFutureIfAny() {
    var pendingFutureOrNull = pendingFuture.getAndSet(null);
    if (pendingFutureOrNull != null) {
      pendingFutureOrNull.cancel(true);
    }
  }

  private static class Attempt {
    private static final int DEFAULT_DELAY_S = 60;
    private static final int BACK_OFF_MULTIPLIER = 2;
    private static final int MAX_ATTEMPTS = 10;

    private final long delay;
    private final int attemptNumber;

    public Attempt() {
      this(DEFAULT_DELAY_S, 1);
    }

    public Attempt(long delay, int attemptNumber) {
      this.delay = delay;
      this.attemptNumber = attemptNumber;
    }

    public Attempt next() {
      return new Attempt(delay * BACK_OFF_MULTIPLIER, attemptNumber + 1);
    }

    public boolean isMax() {
      return attemptNumber == MAX_ATTEMPTS;
    }
  }
}
