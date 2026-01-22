/*
ACR-12a169bb18de49b98bbd55a0d6e0d852
ACR-85c4c7a9ea2a41f6b4b24e790d7b4352
ACR-c0fec2ed459c4612ae81a2b0a35d6cf1
ACR-293cd222fb564785b07449116366fe35
ACR-319e361f57244fb4918e81a20d5dd385
ACR-a394be96b08741bf9991462a2bc4b85c
ACR-c2c4367b910340889770f5d9a957bab1
ACR-def1dc68675f46258b81117831def803
ACR-849c6679127c4fd9b67d45d4a7a9a605
ACR-5524361176514dd7a72a38c270fa2cf1
ACR-d0c5b4f0a2d44f8985cbacf71aba206b
ACR-62b497890e2f4db2a9f41d542b39354a
ACR-16d328be01aa4438a2116ac0cd0803e5
ACR-9dc9509135e84086b337ad783aaf149d
ACR-2222e4634384478f96c93f0ec3679d60
ACR-892181ca175344e3b7700bc3e2b05fbe
ACR-7fce2a47caf542a0b091adc278ed381e
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
      //ACR-e44ed599bc744dfebcc3079368833fc8
      return;
    }
    smartNotificationsStorage.store(dateTimeEpoch);
  }

}
