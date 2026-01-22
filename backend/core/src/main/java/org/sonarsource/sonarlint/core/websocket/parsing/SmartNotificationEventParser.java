/*
ACR-c00145805acf402fb706aeff1cd67e70
ACR-d813a87a6b7449ed8aee5cee2414be25
ACR-b87edb0de81d444ab130110f5e5d6a27
ACR-5291c87b8b16454ab26e6f8587b0f0b0
ACR-1dfcb847cbb044b8bb76275fb98a8beb
ACR-2b3b00ea677d4216b1e87b02a8d33c3d
ACR-a9a8e636955d4317a5ba0962c6b363b2
ACR-37270578bf65457096d95f199e7b7257
ACR-dca2b3c3e7f446ed8f8cd8082223866c
ACR-f0914221222f45e4be609bf523bc808b
ACR-47d9be402d1640cab1ed169d90ce4b97
ACR-c6a483b15b7c461ca3b90a07a6fd99b1
ACR-8250495d083f4ada99a3b6031be89764
ACR-bac1628eaf9e4cb6b63af67cfbaf386f
ACR-c8bef2dce2c34628b5ab313caf69b279
ACR-d8a2446bff364f0582813259b37fdf6f
ACR-14a125fc92d34ea0bb9dee33135274d0
 */
package org.sonarsource.sonarlint.core.websocket.parsing;

import com.google.gson.Gson;
import java.util.Optional;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.serverapi.push.parsing.EventParser;
import org.sonarsource.sonarlint.core.websocket.events.SmartNotificationEvent;

import static org.sonarsource.sonarlint.core.serverapi.util.ServerApiUtils.isBlank;

public class SmartNotificationEventParser implements EventParser<SmartNotificationEvent> {

  private final Gson gson = new Gson();
  private final String category;

  public SmartNotificationEventParser(String category) {
    this.category = category;
  }

  @Override
  public Optional<SmartNotificationEvent> parse(String jsonData) {
    var payload = gson.fromJson(jsonData, SmartNotificationEventPayload.class);
    if (payload.isInvalid()) {
      SonarLintLogger.get().error("Invalid payload for 'SmartNotification' event of category '" + category + "': {}", jsonData);
      return Optional.empty();
    }
    return Optional.of(new SmartNotificationEvent(
      payload.message,
      payload.link,
      payload.project,
      payload.date,
      category));
  }

  private static class SmartNotificationEventPayload {
    private String message;
    private String link;
    private String project;
    private String date;

    private boolean isInvalid() {
      return isBlank(message) || isBlank(link) || isBlank(project) || date == null;
    }
  }
}
