/*
ACR-6373267c5f9744eba392688cfe2275d2
ACR-c5f2b1ea7ff64de791be434834b6ee2b
ACR-b3eaf3db0993408f8448d785ffbe71f7
ACR-12f04992ac704b5c8a5612a147407cb3
ACR-d80921a8a359466eb154d38b565fe7fd
ACR-5a51cf9457cf48aeb4ca1a30acd11410
ACR-bdb89a869cfc4a009252175427247e4e
ACR-ae8b11ede33c464ca6175337b4b580f1
ACR-363366347dee4df68afe9e88eb943230
ACR-41fff756258940aba9725d78a7249b12
ACR-f69a6371da164419b042bfb6dd70b50e
ACR-0a221759144c406ca26e18bf352f6123
ACR-a776495e773e42f6ab87553dd78cef9a
ACR-6c66136b2da84265987458dfb54a2df8
ACR-434c4e0ed8b24958afa5337e3dda65ab
ACR-42ae19533d594fe09b88caad55e399d8
ACR-373041a6de204a30b6d211d6f54680d6
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
