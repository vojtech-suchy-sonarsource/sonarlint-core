/*
ACR-78ccf4bc1ad840c09b91cb2869fb19c0
ACR-cc226a6f2cd649e684076a6a73c11873
ACR-d5187ab33e69426680c22f857b0b9e4b
ACR-e2fb9286938441298eef3578c0b4f25e
ACR-e8406d24d3574b5aa92b2be2dd520cc4
ACR-c8ba3f3eff1c40158cbb22aa6c214d46
ACR-a9600cc4892d4c3e890ad806ceaa37c5
ACR-20c892b894e749238d6155e2f97bb166
ACR-7deae18567624918aae6f4c366c7a2fa
ACR-51c4130f41d24bf5bfc82a24e3b8f194
ACR-777f611778234d529e215e8952f8155c
ACR-9406734b8158409cbcbf6c86803c8e2c
ACR-b913c325c8db409f97d564663d59c736
ACR-760aadb52260429e9c5d9ed29771735c
ACR-d57246fd2f9d4bf2962fc5453cc0fab1
ACR-8cf01a1b46fe43e3a71edfe9c8dbe647
ACR-9e2669bbc7b14e6590aeb56a2e9473c9
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

/*ACR-959901c608244a9eb1c97c3f348ed74f
ACR-7234dee70f564e8f988eaf93edfa99dd
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
