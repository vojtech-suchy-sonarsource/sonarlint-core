/*
ACR-59537bbbbd874bada2acadd84784c067
ACR-0eb67e8c083645fe9796a070550df173
ACR-e7bdd00d01ac49609dffd60f54855195
ACR-cb98e4fac711418983d4061a1bb45d4b
ACR-b3a730cd91a14941817a76ae6b722573
ACR-31ba09c8e3a244bd8d9e993eb59e307c
ACR-181bf0d5d7034037ba6dcc37196a1485
ACR-b92ef08c2c5842dfb1c8d531aa867d27
ACR-b97dafa10cdc440dab2596be85a378ec
ACR-bb6dbc5e70fc43cabedea8d01ff65094
ACR-7677e05a82c4420b9938d8a67509970b
ACR-6aec10ea6b444ee4b330c8aa3bad685e
ACR-3946f243138a42e0bd8118fdd1ec4a07
ACR-14441b0bad7844d3aa76a4f576ca8408
ACR-33e0b51e3a674186a0ab0e485132cb07
ACR-a1bde7365513416da5fd0f5c3670511f
ACR-5a605aa38c4d42a6be9568e3f1627195
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

/*ACR-1d99a96907f64c388fe0385fa8dcb644
ACR-d28f675d4ba64e1284bf250ce0091901
ACR-43f835819db74f5a913ed9bbcc120b5d
ACR-5735f3e725394978b769e5e9e4d67975
ACR-426927d24754475a809f40690027b841
ACR-f4b287d0671a466c8da0b03aea43e1e5
ACR-1e0e194944404f1c9614f92acf332ccc
ACR-5aeb7131e5734c25b195045ef2d1f5f7
ACR-bb7dc973773543dfab5534b6b8690147
ACR-3c4895643a3d4a3aa7a32291779f70f3
ACR-eb0bfdbcfac04c1d83c86b907ce08802
ACR-48086cb980564fc8ab5e1803097f655d
ACR-33289930097a4564a86718b0dd8a5018
ACR-addab270d4e14dd685e10dd187c642ba
ACR-f44ea633043542d5ac8d82a662b5d1b5
ACR-9b3e168da6274ba7a435a5278ba34d22
ACR-d574b5d030ed4775806689f1837bdd34
ACR-12f071ad6ea048629a7397153082a8b6
ACR-337f458cc5534500a0e2ebfa6d4c753e
ACR-15ab15752fcd4b7ebd3e8abf82cbd639
ACR-e83084d7e8d3440a95e0f3705135bec4
ACR-e390be9a77ec4cff8e0fbd6350af2e1e
ACR-dc74493507d447fdb7283c44e08aa181
ACR-fbe558f4bc3e4934950af6e0ce794af9
ACR-65f73e0f65db4fa4bfd77ba1a5f11e47
ACR-197b607590c249448ba56d2c87c5cfd0
ACR-db6bc8338d3743d5bc28b9f55615f12d
ACR-3dea67ed96a2447dbda0e5e68e659394
ACR-34829f0c7c024f06af250c6c24043c96
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

  /*ACR-a0e87bcc1f2f4d4aa331ecad9e32cc97
ACR-c60fc5b542c041b4942cbc8b10684368
   */
  public List<String> logs() {
    return List.copyOf(logs);
  }

  /*ACR-70433c81455d4613965b46ef648e7f60
ACR-2ca88ef3d80e491cab65060960ad10d5
ACR-eab97da2d1b94e0c9defbe07923c83a2
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
