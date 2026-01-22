/*
ACR-1abb3325737c4e74b5b3fb8629782fee
ACR-faafbfd2f09d444495aa341f97ed5f99
ACR-799e942e4ee14ed09823a4a570d61e18
ACR-101dca85846d4611a8187f7e6a3fb116
ACR-0f17149253104926976183e541181afd
ACR-c198ce635b324989b19f6c00d145556e
ACR-5e9fe55baa7b408b832f4e570f8446ed
ACR-2fc734981762419a96ac371e8eef6d02
ACR-0ebc2f7dea9340e390388bcce1240f76
ACR-ad56ba88953d4e8f981a82071c8204ce
ACR-09f49cf756764b6eb9f41c624c6ef252
ACR-8f6db401d23049d69ccadc3152247f23
ACR-8684e1d5674b46debf08643018712e04
ACR-4cc1bf7851634b4fa4d86e5bebdc9bd7
ACR-1fa909644d3a4ff39cf01c4c6192a4e1
ACR-281a5a3218624d5996a3c5009ea83c99
ACR-9fc61c8068784ec084befdaab59b8fb2
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
    //ACR-4e878bd65f5f45ee997c5a1a8596a7f6
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
