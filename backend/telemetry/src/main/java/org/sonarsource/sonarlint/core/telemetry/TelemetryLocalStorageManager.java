/*
ACR-83cf9efd84394e11b09a06a06e301907
ACR-84f6552a5e6a4152914bdc7c87ee9a15
ACR-4a01e1b6ab6a4376933ce9bf3d2a67e3
ACR-49667f019c2e4b11af7dc8defdb0d124
ACR-8be2685a46d1424e9fe949723240c994
ACR-8d2a83f409d349b0973583b1e25ebae0
ACR-41acf91565a54a9597b6cd77f97a9b02
ACR-df867b9fb19e426ca64f868efd821550
ACR-b058fec8028c43f08e75c179af562824
ACR-f3efe0b0792b4fed9ae9ed8d4c8fe1b9
ACR-afdf548dbc1c42ab83ffb7f676284d89
ACR-a6694fad0ebe435b9d61d3517cfcbd69
ACR-f398343dc4c04637a346ecb6d4135a3a
ACR-ec0060b2e62c484783af1874a34a2465
ACR-6e99802dc6f04d49b3d22770200e7cff
ACR-6b1d0be034764034a18d240ea088d9f1
ACR-fc7ac9214d9743f1ae7911dfd0a75fba
 */
package org.sonarsource.sonarlint.core.telemetry;

import com.google.common.annotations.VisibleForTesting;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.storage.local.FileStorageManager;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.TelemetryMigrationDto;
import org.springframework.beans.factory.annotation.Qualifier;

/*ACR-6f0ac1bc25d1490ca48beea510c4e562
ACR-8d6a08f63642463585e7c91d8d798d6b
 */
public class TelemetryLocalStorageManager {

  private final FileStorageManager<TelemetryLocalStorage> fileStorageManager;
  @Nullable
  private final TelemetryMigrationDto telemetryMigration;

  public TelemetryLocalStorageManager(@Qualifier("telemetryPath") Path telemetryPath, InitializeParams initializeParams) {
    fileStorageManager = new FileStorageManager<>(telemetryPath, TelemetryLocalStorage::new, TelemetryLocalStorage.class);
    this.telemetryMigration = initializeParams.getTelemetryMigration();
  }

  @VisibleForTesting
  TelemetryLocalStorage tryRead() {
    return getStorage();
  }

  private TelemetryLocalStorage getStorage() {
    var inMemoryStorage = fileStorageManager.getStorage();
    applyTelemetryMigration(inMemoryStorage);
    return inMemoryStorage;
  }

  private void applyTelemetryMigration(TelemetryLocalStorage inMemoryStorage) {
    if (needToMigrateTelemetry(inMemoryStorage)) {
      inMemoryStorage.setEnabled(telemetryMigration.isEnabled());
      inMemoryStorage.setInstallTime(telemetryMigration.getInstallTime());
      inMemoryStorage.setNumUseDays(telemetryMigration.getNumUseDays());
    }
  }

  private boolean needToMigrateTelemetry(TelemetryLocalStorage inMemoryStorage) {
    if (telemetryMigration == null) {
      return false;
    }
    var duration = Duration.between(inMemoryStorage.installTime(), OffsetDateTime.now());
    return duration.getSeconds() < 10 && inMemoryStorage.numUseDays() == 0;
  }

  public void tryUpdateAtomically(Consumer<TelemetryLocalStorage> updater) {
    fileStorageManager.tryUpdateAtomically(updater);
  }

  public LocalDateTime lastUploadTime() {
    return getStorage().lastUploadTime();
  }

  public boolean isEnabled() {
    return getStorage().enabled();
  }

  public OffsetDateTime installTime() {
    return getStorage().installTime();
  }
}
