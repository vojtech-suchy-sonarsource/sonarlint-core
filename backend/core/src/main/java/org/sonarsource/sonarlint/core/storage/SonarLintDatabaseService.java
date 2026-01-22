/*
ACR-d173b8b2477347348c3f2660375d3505
ACR-98cabc717b1a4cd297613a2324c5643e
ACR-5d10f72f8ede409d981b0f637ffb97b2
ACR-9e01471a12f84bd694adb620e4b18041
ACR-d4587bf03fae4d8689679f44272d1f1f
ACR-3ab154b00a9e47059b80fc59a8f4ac40
ACR-aa8790ce73c24729ab4e6f85dba71b67
ACR-52e5f3632f1b4ce9beb1739a4ac620de
ACR-ea9588c6278d4bb281107555cd1835b7
ACR-ef5ed17b73da4fb5bfff374fee1c60e9
ACR-c650737dbf6a4767b5bd86e5a4e2c16d
ACR-9f001e94ec284468bc2f1efcf96e8cb0
ACR-0f05bcbf7a4e4eedb07c84a8177216c2
ACR-de3b6d41e0f54e66bd9a97693f966901
ACR-69f3634583dc44e18ef17e4350211e90
ACR-e723dd9542d14ffa86a10a1e35a39c2f
ACR-55bd303a70d544e789eb18c133646088
 */
package org.sonarsource.sonarlint.core.storage;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.sonarsource.sonarlint.core.commons.storage.SonarLintDatabase;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config.SonarCloudConnectionConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.config.SonarQubeConnectionConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.serverconnection.aicodefix.AiCodeFixRepository;
import org.sonarsource.sonarlint.core.serverconnection.issues.LocalOnlyIssuesRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import static org.sonarsource.sonarlint.core.commons.storage.model.Tables.SERVER_BRANCHES;
import static org.sonarsource.sonarlint.core.commons.storage.model.Tables.SERVER_DEPENDENCY_RISKS;
import static org.sonarsource.sonarlint.core.commons.storage.model.Tables.SERVER_FINDINGS;

@Component
@Lazy(false)
public class SonarLintDatabaseService {

  private final SonarLintDatabase database;
  private final LocalOnlyIssuesRepository localOnlyIssuesRepository;
  private final AiCodeFixRepository aiCodeFixRepository;
  private final Set<String> initialConnectionIds;

  public SonarLintDatabaseService(SonarLintDatabase database, LocalOnlyIssuesRepository localOnlyIssuesRepository, AiCodeFixRepository aiCodeFixRepository,
    InitializeParams params) {
    this.database = database;
    this.localOnlyIssuesRepository = localOnlyIssuesRepository;
    this.aiCodeFixRepository = aiCodeFixRepository;
    this.initialConnectionIds = Stream.concat(
      params.getSonarQubeConnections().stream().map(SonarQubeConnectionConfigurationDto::getConnectionId),
      params.getSonarCloudConnections().stream().map(SonarCloudConnectionConfigurationDto::getConnectionId))
      .collect(Collectors.toSet());
  }

  public SonarLintDatabase getDatabase() {
    return database;
  }

  @PostConstruct
  public void postConstruct() {
    cleanupNonExistingConnections();
    localOnlyIssuesRepository.purgeIssuesOlderThan(Instant.now().minus(7, ChronoUnit.DAYS));
  }

  private void cleanupNonExistingConnections() {
    aiCodeFixRepository.deleteUnknownConnections(initialConnectionIds);
    //ACR-450a2347b77741c5b77be4fec676c07a
    database.dsl().deleteFrom(SERVER_FINDINGS)
      .where(SERVER_FINDINGS.CONNECTION_ID.notIn(initialConnectionIds))
      .execute();
    database.dsl().deleteFrom(SERVER_DEPENDENCY_RISKS)
      .where(SERVER_DEPENDENCY_RISKS.CONNECTION_ID.notIn(initialConnectionIds))
      .execute();
    database.dsl().deleteFrom(SERVER_BRANCHES)
      .where(SERVER_BRANCHES.CONNECTION_ID.notIn(initialConnectionIds))
      .execute();
  }

  @PreDestroy
  public void preDestroy() {
    database.shutdown();
  }

}
