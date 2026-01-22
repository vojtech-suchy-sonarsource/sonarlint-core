/*
ACR-45631c2f0e8a40088c5314b0a077799c
ACR-60c091fd67b34cc38618b9fab7fb5326
ACR-2801d25719eb4f2a8f20b34b51b9ee88
ACR-ef1bd044fc2246adbcede46f209fae81
ACR-8cec71db21be48f2b3f2acd521ca3fe1
ACR-eca2e248021448a9976a01901be29c6c
ACR-5697ad344a184af1b1ec84c6885a1b7c
ACR-0ca853d905014893b17754c362df2ad8
ACR-4c246ee30d1e4bd8ad4a6dab19af6cac
ACR-f6e90bea15504b05b6458ced8adcefd0
ACR-f99be0a1a3dc471fbdf3b5094fd1c122
ACR-be3b479c81b34a4cbd511b2cfdfdcdf1
ACR-2d8a6a68ddbb4761bc4377822ee9a196
ACR-b3c0f69bd808415fae99c4a9399ec2d5
ACR-441e5617b3f14341bb14bc90655289f1
ACR-743421bed1b34f00937e78c980d5f2b0
ACR-393de8ac5a874d95aae589eb62fca3d9
 */
package org.sonarsource.sonarlint.core.smartnotifications;

import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.UserPaths;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.serverconnection.FileUtils;
import org.sonarsource.sonarlint.core.serverconnection.proto.Sonarlint;
import org.sonarsource.sonarlint.core.serverconnection.storage.ProtobufFileUtil;
import org.sonarsource.sonarlint.core.storage.SonarLintDatabaseService;
import org.sonarsource.sonarlint.core.storage.StorageService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.sonarsource.sonarlint.core.serverconnection.storage.ProjectStoragePaths.encodeForFs;

class LastEventPollingTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private static final ZonedDateTime STORED_DATE = ZonedDateTime.now().minusDays(5);
  private static final String PROJECT_KEY = "projectKey";
  private static final String CONNECTION_ID = "connectionId";
  private static final String FILE_NAME = "last_event_polling.pb";

  @Test
  void should_retrieve_stored_last_event_polling(@TempDir Path tmpDir) {
    var storageFile = tmpDir.resolve(encodeForFs(CONNECTION_ID)).resolve("projects").resolve(encodeForFs(PROJECT_KEY)).resolve(FILE_NAME);
    FileUtils.mkdirs(storageFile.getParent());
    ProtobufFileUtil.writeToFile(Sonarlint.LastEventPolling.newBuilder()
      .setLastEventPolling(STORED_DATE.toInstant().toEpochMilli())
      .build(), storageFile);
    var databaseService = mock(SonarLintDatabaseService.class);
    var storage = new StorageService(userPathsFrom(tmpDir), databaseService);
    var lastEventPolling = new LastEventPolling(storage);

    var result = lastEventPolling.getLastEventPolling(CONNECTION_ID, PROJECT_KEY);

    assertThat(result).isEqualTo(STORED_DATE.truncatedTo(ChronoUnit.MILLIS));
  }

  @Test
  void should_store_last_event_polling(@TempDir Path tmpDir) {
    var databaseService = mock(SonarLintDatabaseService.class);
    var storage = new StorageService(userPathsFrom(tmpDir), databaseService);
    var lastEventPolling = new LastEventPolling(storage);
    lastEventPolling.setLastEventPolling(STORED_DATE, CONNECTION_ID, PROJECT_KEY);

    var result = lastEventPolling.getLastEventPolling(CONNECTION_ID, PROJECT_KEY);

    assertThat(result).isEqualTo(STORED_DATE.truncatedTo(ChronoUnit.MILLIS));
  }

  @Test
  void should_not_retrieve_stored_last_event_polling(@TempDir Path tmpDir) {
    var databaseService = mock(SonarLintDatabaseService.class);
    var storage = new StorageService(userPathsFrom(tmpDir), databaseService);
    var lastEventPolling = new LastEventPolling(storage);

    var result = lastEventPolling.getLastEventPolling(CONNECTION_ID, PROJECT_KEY);

    assertThat(result).isBeforeOrEqualTo(ZonedDateTime.now()).isAfter(ZonedDateTime.now().minusSeconds(3));
  }

  private static UserPaths userPathsFrom(Path tmpDir) {
    var mock = mock(UserPaths.class);
    when(mock.getStorageRoot()).thenReturn(tmpDir);
    when(mock.getWorkDir()).thenReturn(tmpDir);
    return mock;
  }

}
