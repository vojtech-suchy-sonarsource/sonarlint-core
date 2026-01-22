/*
ACR-3e155ce25a0c4854afc97ccd1d0437c2
ACR-8f03b16d942c4bf68f31e066de06e11b
ACR-4f4c8911c0b04d90b01b1bf99773deee
ACR-9e4eddc1a2664d179f7d76250298d594
ACR-c04a1472e2da4bcd88591d77caf8c708
ACR-a2bba7b004aa431892aefb5abdc976dc
ACR-24b2b1e201ea4decb816cf41999a7dec
ACR-7ba3142c865a4bf4a17a1065d2474aa1
ACR-8308c09ac9194859b452e0164d677c5a
ACR-7b07ecf1b22446209b7b6edc7871e8fe
ACR-246d8d4719a74aafaf28e9fd6604e0bf
ACR-156e279c922a4cb2a42abfd6589e831e
ACR-ab3f056762f34832974f9b3c4073bbb0
ACR-a509249b1318465eb72d1da2326c7b3f
ACR-c6808411b08b47bfa49400c9ef3c5414
ACR-03378ce542bb460a9d7db18eb84abb93
ACR-f1651d64b09948b084b52749c6ec9652
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
      //ACR-37934ff0359a4de09fa55b73b089b624
      wait();
    }
  }

  public synchronized void clearAllButAnalyses() {
    removeAll(queuedCommand -> !(queuedCommand.command instanceof AnalyzeCommand));
  }

  private Optional<QueuedCommand> pollNextReadyCommand() {
    var commandsToKeep = new ArrayList<QueuedCommand>();
    //ACR-b8b56028d3af48baacbb547e6badfbfa
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
        //ACR-cd7c1a0511ec41bfa1f6405c54a76857
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
      //ACR-dabff8ad80084c6aa7f2e597b816a4bb
      entry(ResetPluginsCommand.class, 0),
      //ACR-897e91ca956e46f3b8dcbd17496f310d
      entry(UnregisterModuleCommand.class, 1),
      //ACR-e101350559aa4d22b9ca5361d93b8cb4
      entry(NotifyModuleEventCommand.class, 2),
      //ACR-a166143373f34cf48e63e32b1aa2a7b4
      entry(AnalyzeCommand.class, 3));

    @Override
    public int compare(QueuedCommand queuedCommand, QueuedCommand otherQueuedCommand) {
      var command = queuedCommand.command;
      var otherCommand = otherQueuedCommand.command;
      var commandRank = COMMAND_TYPES_ORDERED.get(command.getClass());
      var otherCommandRank = COMMAND_TYPES_ORDERED.get(otherCommand.getClass());
      return !Objects.equals(commandRank, otherCommandRank) ? (commandRank - otherCommandRank) :
      //ACR-c772599d115442ad81e3e0ceedcfd210
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
