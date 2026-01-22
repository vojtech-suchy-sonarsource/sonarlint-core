/*
ACR-010c90bf2494477c8fb07e27c4235eb4
ACR-a7b6869be1eb43fc95bef8ea23d21821
ACR-69af41835ca34b76a7ae1884d2870b78
ACR-5edcf4e563db460385465a61d0334477
ACR-06aabb6844c04bccb16932cb5d2ce168
ACR-41e13adc6b524672893c8ef5a2a1cc51
ACR-aca349fb89224e3b89130cd2db0cf547
ACR-8604ae591a9e451e942cef73fa069846
ACR-8bf4759af73c445280f5647c30d0ed4d
ACR-e89f8c4ac16b40b7bfa6452547b99e8f
ACR-54eeb6cb4b634937bd756ab5ffc6eb75
ACR-070b745d9ef9446889efb962d9a3ab8e
ACR-da336e574dc64f97ab952ba7805c720e
ACR-2f354257493243d6bff64017b71e1561
ACR-f54459894cf643c49eb20eb2f2f57ab9
ACR-e40682e7fdde4327bc69e2b33e7967a7
ACR-61c2aab570b64d328ab3e2325d5219bf
 */
package org.sonarsource.sonarlint.core.telemetry;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.TelemetryMigrationDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TelemetryLocalStorageManagerTests {

  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  private final LocalDate today = LocalDate.now();
  private Path filePath;

  @BeforeEach
  void setUp(@TempDir Path temp) {
    filePath = temp.resolve("usage");
  }

  @Test
  void test_default_data() {
    var storage = new TelemetryLocalStorageManager(filePath, mock(InitializeParams.class));

    var data = storage.tryRead();
    assertThat(filePath).doesNotExist();

    assertThat(data.installTime()).is(within3SecOfNow);
    assertThat(data.lastUseDate()).isNull();
    assertThat(data.numUseDays()).isZero();
    assertThat(data.enabled()).isTrue();
  }

  private final Condition<OffsetDateTime> within3SecOfNow = new Condition<>(p -> {
    var now = OffsetDateTime.now();
    return Math.abs(p.until(now, ChronoUnit.SECONDS)) < 3;
  }, "within3Sec");

  @Test
  void should_update_data() {
    var storage = new TelemetryLocalStorageManager(filePath, mock(InitializeParams.class));

    storage.tryRead();
    assertThat(filePath).doesNotExist();

    storage.tryUpdateAtomically(TelemetryLocalStorage::setUsedAnalysis);
    assertThat(filePath).exists();

    var data2 = storage.tryRead();

    assertThat(data2.lastUseDate()).isEqualTo(today);
    assertThat(data2.numUseDays()).isEqualTo(1);
  }

  @Test
  void should_fix_invalid_installTime() {
    var storage = new TelemetryLocalStorageManager(filePath, mock(InitializeParams.class));

    storage.tryUpdateAtomically(data -> {
      data.setInstallTime(null);
      data.setNumUseDays(100);
    });

    var data2 = storage.tryRead();
    assertThat(data2.installTime()).is(within3SecOfNow);
    assertThat(data2.lastUseDate()).isNull();
    assertThat(data2.numUseDays()).isZero();
  }

  @Test
  void should_fix_invalid_numDays() {
    var storage = new TelemetryLocalStorageManager(filePath, mock(InitializeParams.class));

    var tenDaysAgo = OffsetDateTime.now().minusDays(10);

    storage.tryUpdateAtomically(data -> {
      data.setInstallTime(tenDaysAgo);
      data.setLastUseDate(today);
      data.setNumUseDays(100);
    });

    var data2 = storage.tryRead();
    assertThat(data2.installTime()).isEqualTo(tenDaysAgo);
    assertThat(data2.lastUseDate()).isEqualTo(today);
    assertThat(data2.numUseDays()).isEqualTo(11);
  }

  @Test
  void should_fix_dates_in_future() {
    var storage = new TelemetryLocalStorageManager(filePath, mock(InitializeParams.class));

    storage.tryUpdateAtomically(data -> {
      data.setInstallTime(OffsetDateTime.now().plusDays(5));
      data.setLastUseDate(today.plusDays(7));
      data.setNumUseDays(100);
    });

    var data2 = storage.tryRead();
    assertThat(data2.installTime()).is(within3SecOfNow);
    assertThat(data2.lastUseDate()).isEqualTo(today);
    assertThat(data2.numUseDays()).isEqualTo(1);
  }

  @Test
  void should_not_crash_when_cannot_read_storage(@TempDir Path temp) {
    InternalDebug.setEnabled(false);
    assertThatCode(() -> new TelemetryLocalStorageManager(temp, mock(InitializeParams.class)).tryRead())
      .doesNotThrowAnyException();

  }

  @Test
  void should_not_crash_when_cannot_write_storage(@TempDir Path temp) {
    InternalDebug.setEnabled(false);
    assertThatCode(() -> new TelemetryLocalStorageManager(temp, mock(InitializeParams.class)).tryUpdateAtomically(d -> {}))
      .doesNotThrowAnyException();
  }

  @Test
  void should_increment_open_hotspot_in_browser() {
    var storage = new TelemetryLocalStorageManager(filePath, mock(InitializeParams.class));

    storage.tryUpdateAtomically(TelemetryLocalStorage::incrementOpenHotspotInBrowserCount);
    storage.tryUpdateAtomically(TelemetryLocalStorage::incrementOpenHotspotInBrowserCount);

    var data2 = storage.tryRead();
    assertThat(data2.openHotspotInBrowserCount()).isEqualTo(2);
  }

  @Test
  void should_increment_hotspot_status_changed() {
    var storage = new TelemetryLocalStorageManager(filePath, mock(InitializeParams.class));

    storage.tryUpdateAtomically(TelemetryLocalStorage::incrementHotspotStatusChangedCount);
    storage.tryUpdateAtomically(TelemetryLocalStorage::incrementHotspotStatusChangedCount);
    storage.tryUpdateAtomically(TelemetryLocalStorage::incrementHotspotStatusChangedCount);

    var data = storage.tryRead();
    assertThat(data.hotspotStatusChangedCount()).isEqualTo(3);
  }

  @Test
  void should_increment_issue_status_changed() {
    var storage = new TelemetryLocalStorageManager(filePath, mock(InitializeParams.class));

    storage.tryUpdateAtomically(telemetryLocalStorage -> telemetryLocalStorage.addIssueStatusChanged("ruleKey1"));
    storage.tryUpdateAtomically(telemetryLocalStorage -> telemetryLocalStorage.addIssueStatusChanged("ruleKey2"));

    var data = storage.tryRead();
    assertThat(data.issueStatusChangedCount()).isEqualTo(2);
    assertThat(data.issueStatusChangedRuleKeys()).containsExactlyInAnyOrder("ruleKey1", "ruleKey2");
  }

  @Test
  void should_increment_issue_ai_fixable() {
    var storage = new TelemetryLocalStorageManager(filePath, mock(InitializeParams.class));
    var uuid1 = UUID.randomUUID();
    var uuid2 = UUID.randomUUID();
    var uuid3 = UUID.randomUUID();
    storage.tryUpdateAtomically(telemetryLocalStorage -> telemetryLocalStorage.addIssuesWithPossibleAiFixFromIde(Set.of(uuid1, uuid2)));
    storage.tryUpdateAtomically(telemetryLocalStorage -> telemetryLocalStorage.addIssuesWithPossibleAiFixFromIde(Set.of(uuid1, uuid3)));

    var data = storage.tryRead();
    assertThat(data.getCountIssuesWithPossibleAiFixFromIde()).isEqualTo(3);
  }

  @Test
  void should_migrate_telemetry() {
    var initializeParams = mock(InitializeParams.class);
    var expectedInstallTime = OffsetDateTime.now();
    when(initializeParams.getTelemetryMigration()).thenReturn(new TelemetryMigrationDto(expectedInstallTime, 42, false));

    var storageManager = new TelemetryLocalStorageManager(filePath, initializeParams);

    var localStorage = storageManager.tryRead();
    var actualInstallTime = localStorage.installTime();
    var numUseDays = localStorage.numUseDays();
    var enabled = localStorage.enabled();

    assertThat(enabled).isFalse();
    assertThat(numUseDays).isEqualTo(42);
    assertThat(actualInstallTime).isEqualTo(expectedInstallTime);
  }
}
