/*
ACR-483ea36ea2e24546b35f0d36f6c163fb
ACR-4f86266ca1fb47b58ad55ea426c0e8de
ACR-6ee2b990a63346e88276ce4be341c927
ACR-0c795768b4e849008e12c005a0ba8ccf
ACR-fe125bc63eb24467abe7512749a24847
ACR-67fa0ffc65a24bf1ba36175846bd5764
ACR-f04f2cdefade42a8a9d89c2f0517baae
ACR-f081e8557cb24eeaa7deaa633994765d
ACR-5544d1276e4245d3b1571f444069ea50
ACR-cc545c0c8c134e9c8de788540c5f9739
ACR-392b822ad2dc44f0bc5f8c13c1fcfdcf
ACR-ee0b25a9b9cf417493b105b18a62afc5
ACR-2ad87c2f229146a1937ae0d2b214ef0f
ACR-f41be7aac77c41d79ed97808ab78df4e
ACR-f7e084e6966a4abb8afff0946776d745
ACR-345989df98754c21a03abdb57299a04d
ACR-36a72422a6044456b109f54b96f6bf7c
 */
package org.sonarsource.sonarlint.core.telemetry;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.telemetry.common.TelemetryUserSetting;

/*ACR-f96be32b2d94428aa60e9aecaa1c5272
ACR-6b70b692e4684049a07bbe6ba8942be9
ACR-83870876f3a94ccba0a1117c46e903c1
 */
public class TelemetryManager implements TelemetryUserSetting {

  static final int MIN_HOURS_BETWEEN_UPLOAD = 5;

  private final TelemetryLocalStorageManager storageManager;
  private final TelemetryHttpClient client;

  TelemetryManager(TelemetryLocalStorageManager storageManager, TelemetryHttpClient client) {
    this.storageManager = storageManager;
    this.client = client;
  }

  void enable(TelemetryLiveAttributes telemetryLiveAttributes) {
    storageManager.tryUpdateAtomically(localStorage -> {
      localStorage.setEnabled(true);
      if (isGracePeriodElapsedAndDayChanged(localStorage.lastUploadTime())) {
        uploadAndClearTelemetry(telemetryLiveAttributes, localStorage);
      }
    });
  }

  private static boolean isGracePeriodElapsedAndDayChanged(@Nullable LocalDateTime lastUploadTime) {
    return TelemetryUtils.isGracePeriodElapsedAndDayChanged(lastUploadTime, MIN_HOURS_BETWEEN_UPLOAD);
  }

  private void uploadAndClearTelemetry(TelemetryLiveAttributes telemetryLiveAttributes, TelemetryLocalStorage localStorage) {
    client.upload(localStorage, telemetryLiveAttributes);
    localStorage.setLastUploadTime();
    localStorage.clearAfterPing();
  }

  /*ACR-77a379b51ecb408a88794218d12f4cfb
ACR-67ff81e9149a4c29a7c7a3a7dc325137
   */
  void disable(TelemetryLiveAttributes telemetryLiveAttributes) {
    storageManager.tryUpdateAtomically(data -> {
      data.setEnabled(false);
      client.optOut(data, telemetryLiveAttributes);
    });
  }

  /*ACR-537046527b6d41b4a2960b10375e9198
ACR-5d5f79422e944c2f8991db9670f7ca0f
ACR-c9680be38ba6471e96eb4b9060881569
ACR-5ed754f541ed4f49b3441cde81b7fa94
ACR-a30b8caeb3c44fdeb303f9b08abadfc2
ACR-09103c7ed3f74221890e4bc626b49a47
   */
  void uploadAndClearTelemetry(TelemetryLiveAttributes telemetryLiveAttributes) {
    if (isTelemetryEnabledByUser() && isGracePeriodElapsedAndDayChanged(storageManager.lastUploadTime())) {
      storageManager.tryUpdateAtomically(localStorage -> uploadAndClearTelemetry(telemetryLiveAttributes, localStorage));
    }
  }

  public void updateTelemetry(Consumer<TelemetryLocalStorage> updater) {
    if (isTelemetryEnabledByUser()) {
      storageManager.tryUpdateAtomically(updater);
    }
  }

  @Override
  public boolean isTelemetryEnabledByUser() {
    return storageManager.isEnabled();
  }

  public OffsetDateTime installTime() {
    return storageManager.installTime();
  }
}
