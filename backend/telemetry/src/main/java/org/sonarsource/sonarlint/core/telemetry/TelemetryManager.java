/*
ACR-5d3fe742149b46e6b142524bd3d507a6
ACR-5b2645a625884ba18d7c4b2e3dbe43e5
ACR-e70a5b474bc143b48901c689e6603542
ACR-aae4928e0b824318bbb81e2158ad2d83
ACR-2f6a9e7199e64f84b6281d35f11b724a
ACR-6fe9109c9a364861947e6926385b2e55
ACR-f4f3468c04b2443fb12a66fbe466a8bc
ACR-8e1abcdf6c5442c292c4102b149fc882
ACR-d687513264074bc792a98e80eaffbac7
ACR-91d37c4c8df64eb1b5cf29e0bc5c1727
ACR-6cfbbcb79fb64ab3a8d6019a4f4789c4
ACR-a8c53795a1a146a59d3fde15b5b06af8
ACR-6283dfac1fee44efb11ebc2e6d70c985
ACR-7a6dec3314564b03a1c965605a777387
ACR-f892cd40efce445ea2485d890f4db0db
ACR-daa02f66d6d74d7e871a3c5c41c9c4b1
ACR-5db10cabc74a446690fd183ae6e9bb16
 */
package org.sonarsource.sonarlint.core.telemetry;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.telemetry.common.TelemetryUserSetting;

/*ACR-4882576a58794147b8c756bf379c3061
ACR-64a4c68614924bde83f18cf1b616c102
ACR-9adbb13d7ffe4e8f86238d61ed3bf72d
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

  /*ACR-91b6ba6bceb54871a20e659dd71df39d
ACR-524df3c2c0dc4e18976c68f413c383b3
   */
  void disable(TelemetryLiveAttributes telemetryLiveAttributes) {
    storageManager.tryUpdateAtomically(data -> {
      data.setEnabled(false);
      client.optOut(data, telemetryLiveAttributes);
    });
  }

  /*ACR-2e447eea3f6644c28cd10acd19c35c77
ACR-33d994994ecd494ba952233d8fd2a7d0
ACR-081b8942797747689c49c5553f8379ee
ACR-22761cd837274d3aaee11fb510803fef
ACR-acaf567b55ce47a0b597a15cde4afb43
ACR-1a89ad1df06b41a796f4a1cbb69c9de7
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
