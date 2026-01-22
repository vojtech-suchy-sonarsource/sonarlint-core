/*
ACR-215bf19626554d56aca9d8a41bdc96ba
ACR-b2b2a6ef7bdb41cc9ae5e551b4296fa1
ACR-9fb2c03023d34f45a0f054e219708c0c
ACR-d8b67e1cf6d244bbb22198029d9ccf9b
ACR-5d29181e8a7e48c990deefddf6ce4971
ACR-7bdc2c98938540f88482420df3fd7b43
ACR-38d6a18137384268b33285ddd8550071
ACR-d60f22b2260d442da9de67f1970431ef
ACR-f395ef87402c497b8c8a6c04e4b19421
ACR-d70c941f44344ef8a2e9b349c6d23df5
ACR-da1322a09ca5499b8fd9479c3a42c111
ACR-f3c9687316774173939caff6f270ea9c
ACR-32daf55d32bb40079cabecd2b72daea5
ACR-c3efd8c32d344419b487d46f5fb47167
ACR-97c7e90d10d744a694b856195f5d8996
ACR-c25a76d9ddb046d292445462679654aa
ACR-65e4fbb64dbd42e6a4faffefd637794c
 */
package org.sonarsource.sonarlint.core.analysis;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.sonarsource.sonarlint.core.analysis.command.AnalyzeCommand;
import org.sonarsource.sonarlint.core.analysis.command.Command;
import org.sonarsource.sonarlint.core.analysis.command.NotifyModuleEventCommand;
import org.sonarsource.sonarlint.core.analysis.command.ResetPluginsCommand;
import org.sonarsource.sonarlint.core.analysis.command.UnregisterModuleCommand;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

import static java.util.Map.entry;

public class AnalysisQueue {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  public static final String ANALYSIS_EXPIRATION_DELAY_PROPERTY_NAME = "sonarqube.ide.internal.analysis.expiration.delay";
  private static final Duration ANALYSIS_EXPIRATION_DEFAULT_DELAY = Duration.ofMinutes(1);
  private final Duration analysisExpirationDelay = getAnalysisExpirationDelay();

  private final PriorityQueue<QueuedCommand> queue = new PriorityQueue<>(new CommandComparator());

  public synchronized void post(Command command) {
    queue.add(new QueuedCommand(command));
    LOG.debug("Posting command in analysis queue: {}, new size is {}", command, queue.size());
    notifyAll();
  }

  public synchronized void wakeUp() {
    notifyAll();
  }

  public synchronized List<Command> removeAll() {
    var pendingTasks = new ArrayList<>(queue);
    queue.clear();
    return pendingTasks.stream().map(QueuedCommand::getCommand).toList();
  }

  public synchronized Command takeNextCommand() throws InterruptedException {
    while (true) {
      var firstReadyCommand = pollNextReadyCommand();
      if (firstReadyCommand.isPresent()) {
        var queuedCommand = firstReadyCommand.get();
        LOG.debug("Picked command from the queue: {}, {} remaining", queuedCommand.command, queue.size());
        return tidyUp(queuedCommand);
      }
      //ACR-1b895cfd300f4a63b411d54e7f45464e
      wait();
    }
  }

  public synchronized void clearAllButAnalyses() {
    removeAll(queuedCommand -> !(queuedCommand.command instanceof AnalyzeCommand));
  }

  private Optional<QueuedCommand> pollNextReadyCommand() {
    var commandsToKeep = new ArrayList<QueuedCommand>();
    //ACR-e9bb27f8bfed4741b5ca370402fb37ef
    while (!queue.isEmpty()) {
      var candidateCommand = queue.poll();
      if (candidateCommand.command.shouldCancelQueue()) {
        candidateCommand.command.cancel();
        LOG.debug("Not picking next command {}, is canceled", candidateCommand.command);
      } else {
        if (candidateCommand.command.isReady()) {
          queue.addAll(commandsToKeep);
          return Optional.of(candidateCommand);
        }
        LOG.debug("Not picking next command {}, is not ready", candidateCommand.command);
        commandsToKeep.add(candidateCommand);
      }
    }
    queue.addAll(commandsToKeep);
    return Optional.empty();
  }

  private Command tidyUp(QueuedCommand nextCommand) {
    cleanUpExpiredCommands(nextCommand);
    return batchAutomaticAnalyses(nextCommand.command);
  }

  private void cleanUpExpiredCommands(QueuedCommand nextQueuedCommand) {
    var notReadyCommands = removeAll(queuedCommand -> !queuedCommand.command.isReady() && queuedCommand.getQueuedTime().plus(analysisExpirationDelay).isBefore(Instant.now()));
    if (!notReadyCommands.isEmpty()) {
      LOG.debug("Canceling {} not ready analyses", notReadyCommands.size());
    }
    if (nextQueuedCommand.command instanceof UnregisterModuleCommand unregisterCommand) {
      var expiredCommands = removeAll(
        queuedCommand -> (queuedCommand.command instanceof AnalyzeCommand analyzeCommand && analyzeCommand.getModuleKey().equals(unregisterCommand.getModuleKey()))
          || queuedCommand.command instanceof NotifyModuleEventCommand);
      if (!expiredCommands.isEmpty()) {
        LOG.debug("Canceling {} analyses expired by module unregistration", expiredCommands.size());
      }
    }
  }

  private Command batchAutomaticAnalyses(Command nextCommand) {
    if (nextCommand instanceof AnalyzeCommand analyzeCommand && analyzeCommand.getTriggerType().canBeBatchedWithSameTriggerType()) {
      var removedCommands = (List<AnalyzeCommand>) removeAll(otherQueuedCommand -> canBeBatched(analyzeCommand, otherQueuedCommand.command));
      if (removedCommands.isEmpty()) {
        return analyzeCommand;
      }
      LOG.debug("Batching {} analyses", removedCommands.size() + 1);
      return Stream.concat(Stream.of(analyzeCommand), removedCommands.stream())
        .sorted((c1, c2) -> (int) (c1.getSequenceNumber() - c2.getSequenceNumber()))
        .reduce(AnalyzeCommand::mergeWith)
        //ACR-06db8109d00847be9d8e12514fce727b
        .orElse(analyzeCommand);
    }
    return nextCommand;
  }

  private static boolean canBeBatched(AnalyzeCommand analyzeCommand, Command otherCommand) {
    return otherCommand instanceof AnalyzeCommand otherAnalyzeCommand && otherAnalyzeCommand.getModuleKey().equals(analyzeCommand.getModuleKey())
      && otherAnalyzeCommand.getTriggerType().canBeBatchedWithSameTriggerType();
  }

  private List<? extends Command> removeAll(Predicate<QueuedCommand> predicate) {
    var iterator = queue.iterator();
    var removedCommands = new ArrayList<Command>();
    while (iterator.hasNext()) {
      var queuedCommand = iterator.next();
      if (predicate.test(queuedCommand)) {
        iterator.remove();
        queuedCommand.command.cancel();
        removedCommands.add(queuedCommand.command);
      }
    }
    return removedCommands;
  }

  private static class QueuedCommand {
    private final Command command;
    private final Instant queuedTime = Instant.now();

    QueuedCommand(Command command) {
      this.command = command;
    }

    public Command getCommand() {
      return command;
    }

    public Instant getQueuedTime() {
      return queuedTime;
    }
  }

  private static class CommandComparator implements Comparator<QueuedCommand> {
    private static final Map<Class<?>, Integer> COMMAND_TYPES_ORDERED = Map.ofEntries(
      //ACR-c6a95fd6b5d9414dbda3a6f9b742dd2c
      entry(ResetPluginsCommand.class, 0),
      //ACR-8364ec31bde74b87af521613eed9afb8
      entry(UnregisterModuleCommand.class, 1),
      //ACR-e19cd456d84f4c349de84bb7734e3d31
      entry(NotifyModuleEventCommand.class, 2),
      //ACR-f72b142f05f847d5b457f11b59672455
      entry(AnalyzeCommand.class, 3));

    @Override
    public int compare(QueuedCommand queuedCommand, QueuedCommand otherQueuedCommand) {
      var command = queuedCommand.command;
      var otherCommand = otherQueuedCommand.command;
      var commandRank = COMMAND_TYPES_ORDERED.get(command.getClass());
      var otherCommandRank = COMMAND_TYPES_ORDERED.get(otherCommand.getClass());
      return !Objects.equals(commandRank, otherCommandRank) ? (commandRank - otherCommandRank) :
      //ACR-2ae6a5654a8945fdba853cba50a8a2c7
        (int) (command.getSequenceNumber() - otherCommand.getSequenceNumber());
    }
  }

  private static Duration getAnalysisExpirationDelay() {
    try {
      var analysisExpirationDelayFromSystemProperty = System.getProperty(ANALYSIS_EXPIRATION_DELAY_PROPERTY_NAME);
      var parsedDelay = Duration.parse(analysisExpirationDelayFromSystemProperty);
      LOG.debug("Overriding analysis expiration delay with value from system property: {}", parsedDelay);
      return parsedDelay;
    } catch (RuntimeException e) {
      LOG.debug("Using default analysis expiration delay: {}", ANALYSIS_EXPIRATION_DEFAULT_DELAY);
      return ANALYSIS_EXPIRATION_DEFAULT_DELAY;
    }
  }

}
