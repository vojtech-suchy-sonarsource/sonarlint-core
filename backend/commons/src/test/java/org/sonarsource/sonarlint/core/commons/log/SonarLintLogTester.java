/*
ACR-c4f9227784814c2c8fe53b4e1250b726
ACR-65e3474021ca4c43a3b2c4a002235e5d
ACR-07580e8a87894b1fae7bb5f28a942eca
ACR-0ab68e164db049dcbe2c627299e617a7
ACR-8cb968e9719c4dd89aeb160705085265
ACR-1d5cb57900d94668ba5790093f6d86e3
ACR-1d69999707374a4a9d3c3808c894c2ce
ACR-4d4b341cad3f4ab78b63cd6b66736ace
ACR-d79c5fa0654e453d8f4801aeb12eeab4
ACR-e27a2e8c5b51422cabe58eaf8c73a653
ACR-871cb9405b6645d69c32fda1e9b4584e
ACR-163e45960ad446e8a4f664567bc8a31e
ACR-5f28d391d2764b3cb08c3547cd43ee49
ACR-fc7b0ea09bf547b78d82a0c917f193f3
ACR-330a51226f9b409e8d0fe876710472d6
ACR-e6ddb901b2f648b3983a1a2b3d595103
ACR-37deb6affee34c6eaa674918d5be169e
 */
package org.sonarsource.sonarlint.core.commons.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.annotation.Nullable;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.LoggerFactory;
import org.sonarsource.sonarlint.core.commons.log.LogOutput.Level;

/*ACR-de7d2e0cf7bf453786e173d8c0f431ec
ACR-32c0b1ca077747ff9871069736f929bf
ACR-590a5a56f6034c319239e1e395c60b88
ACR-59ab91222b9a431b861a25f72bd02e91
ACR-91f3b360a6f74efea4e459da92629058
ACR-f0befcb63856467f9ab635b3848f2112
ACR-0dca9c354eb348da8faebce947d8b21b
ACR-ae68a47a2f9348479ba13ecd0e872e0b
ACR-0897619def824859b1c6681344ef3152
ACR-34c71dab7713404f9f0b339943cb82a5
ACR-f1269adfc9d44711807645627faffd54
ACR-ef7a29df08ed43ac8ef88a3c1b10090f
ACR-21c8c6f13bcd48a4b6e509b8a61fd146
ACR-587d81803fb642768fb87aa61124213f
ACR-f8e0849899e44833958183baa9f84303
ACR-89d3db658bcd41a9b2daf19a42a500db
ACR-c47d03eece874760ac94f19ac37f0fb7
ACR-b0000d3d51364ad386bac5954f5d947b
ACR-bc5a0e904c2f47be9997da4d45a270e9
ACR-92d322c095f1473e893931ffe38dd617
ACR-0e4d0d96feb24a5fbb8be57fc7b8ed6a
ACR-0e56285ae2c0431cbec646e8acefe2fd
ACR-d3bb749aa0a94882b3867d5042ab28b8
ACR-96b16f86ce504757a79876a8a6cc8dfd
ACR-6ae4dc25261a4caface0f99c058ae0f6
ACR-95228a7c119348c6b0b32f385757d71c
ACR-d8d6d879cf3046daa46a39897d38f22f
ACR-5a00b3eb3eea469aa13b79b3d9daa6ed
ACR-35e0afccbbfd432f95d3db6e016e9c6d
 */
public class SonarLintLogTester implements AfterTestExecutionCallback, BeforeAllCallback, AfterAllCallback {

  private final Queue<String> logs = new ConcurrentLinkedQueue<>();
  private final Map<Level, Queue<String>> logsByLevel = new ConcurrentHashMap<>();
  private final LogOutput logOutput;

  private final ConcurrentListAppender<ILoggingEvent> listAppender = new ConcurrentListAppender<>();

  public SonarLintLogTester(boolean writeToStdOut) {

    logOutput = new LogOutput() {
      @Override
      public void log(@Nullable String formattedMessage, Level level, @Nullable String stacktrace) {
        if (formattedMessage != null) {
          logs.add(formattedMessage);
          logsByLevel.computeIfAbsent(level, l -> new ConcurrentLinkedQueue<>()).add(formattedMessage);
        }
        if (stacktrace != null) {
          logs.add(stacktrace);
          logsByLevel.computeIfAbsent(level, l -> new ConcurrentLinkedQueue<>()).add(stacktrace);
        }
        if (writeToStdOut) {
          System.out.println(level + " " + (formattedMessage != null ? formattedMessage : ""));
          if (stacktrace != null) {
            System.out.println(stacktrace);
          }
        }
      }
    };
  }

  public SonarLintLogTester() {
    this(false);
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
    clear();
  }

  public void clear() {
    logs.clear();
    logsByLevel.clear();
    listAppender.list.clear();
  }

  public LogOutput getLogOutput() {
    return logOutput;
  }

  /*ACR-cddd4b62046c4bd596cecce51d46a704
ACR-af55e29aad464c1d93e2534ea908f873
   */
  public List<String> logs() {
    return List.copyOf(logs);
  }

  /*ACR-f670c2cd3e0b4021a45a98cdbfd3b6db
ACR-4ea143c960714466863e9661e7bb6082
ACR-3f35de45bd3d42d29bda246ad284d95b
   */
  public List<String> logs(Level level) {
    return Optional.ofNullable(logsByLevel.get(level)).map(List::copyOf).orElse(List.of());
  }

  @Override
  public void afterAll(ExtensionContext context) {
    SonarLintLogger.get().setTarget(null);
    listAppender.stop();
    listAppender.list.clear();
    getRootLogger().detachAppender(listAppender);
  }

  @Override
  public void beforeAll(ExtensionContext context) {
    SonarLintLogger.get().setLevel(Level.DEBUG);
    SonarLintLogger.get().setTarget(logOutput);
    getRootLogger().addAppender(listAppender);
    listAppender.start();
  }

  private static ch.qos.logback.classic.Logger getRootLogger() {
    return (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
  }

}
