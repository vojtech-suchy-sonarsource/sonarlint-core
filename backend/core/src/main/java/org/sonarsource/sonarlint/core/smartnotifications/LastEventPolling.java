/*
ACR-35a5becc736242ed8b2125b56135dba6
ACR-69e085d716e44954bb3f076eb6c397bd
ACR-edbe0a480b0244569005fd6c6f4b5d8e
ACR-eed2b29b1c9544cb9d0e1cfe5267c7e1
ACR-4dd8b990940646719fa933a3d45df5be
ACR-9c16e31f18284fe09abdfea6e542ed80
ACR-896037cd3de7424ab0aa451a9d87f69a
ACR-84d4a9342fd44c1693e740f41c36f9ba
ACR-fcc55c4f0f4b4ec9a27229d29410fddd
ACR-275f8ee5499f47f78f2cb419d86e50b9
ACR-83dde873b3ef4aa7af97deff4f47e3c5
ACR-7149fa5308c84779b00967f78f6a5273
ACR-7a7a4880f15b43d2af0cd8c23edfb928
ACR-c8c44345c88c47e095eb553f6950eb41
ACR-4eac84ad5af8494680349169924ecd7a
ACR-1732cca6f8a74312ab52001bce730ca9
ACR-1b65201c67b44f30a434bee6a87aa510
 */
package org.sonarsource.sonarlint.core.smartnotifications;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.sonarsource.sonarlint.core.storage.StorageService;

public class LastEventPolling {

  private final StorageService storage;

  public LastEventPolling(StorageService storage) {
    this.storage = storage;
  }

  public ZonedDateTime getLastEventPolling(String connectionId, String projectKey) {
    var lastEventPollingEpoch = storage.connection(connectionId).project(projectKey).smartNotifications().readLastEventPolling();
    return lastEventPollingEpoch.map(aLong -> ZonedDateTime.ofInstant(Instant.ofEpochMilli(aLong), ZoneId.systemDefault()))
      .orElseGet(ZonedDateTime::now);
  }

  public void setLastEventPolling(ZonedDateTime dateTime, String connectionId, String projectKey) {
    var smartNotificationsStorage = storage.connection(connectionId).project(projectKey).smartNotifications();
    var lastEventPolling = smartNotificationsStorage.readLastEventPolling();
    var dateTimeEpoch = dateTime.toInstant().toEpochMilli();
    if (lastEventPolling.isPresent() && dateTimeEpoch <= lastEventPolling.get()) {
      //ACR-d2406978495f40d49a12d0a7da36b6c4
      return;
    }
    smartNotificationsStorage.store(dateTimeEpoch);
  }

}
