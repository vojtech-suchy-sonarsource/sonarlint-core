/*
ACR-2d3ffe0a5c98492ea536921faa5dedf5
ACR-9af233d787334b8780ceaf653318d818
ACR-4ee0524f363d44f39cdf3ebbd5c72913
ACR-ca6bb8dcb5fc412885395dea731fe565
ACR-eaaa09492421482298ea7f98619be1a9
ACR-a82fa48f080742278d190ce21f0e4cb5
ACR-ebcd0e8485b843d9b361ea33e38d57a2
ACR-347d84f2d2084e588a0164cbbbf87b5d
ACR-9c4e3926e3f84e5f8fcb869f11521cb7
ACR-4bf8975539394e559b252337d6be3d18
ACR-445b9256b2e346cd9c2abdd1ad03c240
ACR-6eed9b58722f48ffbbffc70ac091f3c5
ACR-0832f3fb311d459b90fc8bfa2f96a817
ACR-3a18081fd6fb4544ba0d2220af307eda
ACR-1cc6057ecf6944979494af0f20f9672e
ACR-fc9f39b1f0334fbea473b74b3b3d1559
ACR-e1082a08a3bc442bab3ccb43578d315d
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
          //ACR-542e063e4768470a8e409d09937d8d45
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
      //ACR-62064ec239274130aa3638605bd6c45f
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
