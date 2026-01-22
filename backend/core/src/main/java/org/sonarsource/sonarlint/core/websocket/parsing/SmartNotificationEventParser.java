/*
ACR-a0affcc836ff4b0fbc892422dd54c66e
ACR-8a28cd29fe3f4523998673528a9a777a
ACR-f4ce2615782d47b2b7bbfb45a9e8c032
ACR-5119769d4aeb46949e6382a829aca7dc
ACR-57a6eb5d44214d3ea1243ffa3952be05
ACR-0c47dfcb07694926a04278ce9f2e7887
ACR-acf6841899104f5c818a36640cd1ff4b
ACR-08362e855eea48beac9ff65b4fc6764d
ACR-75ce59f78d624d9b8863a0541e06dbff
ACR-775da6166ca44806a33c1178a6168df2
ACR-269fe81c06f94345a76f52ed33c158df
ACR-b38f023ab06d473a958c8807d2f20d38
ACR-ee9b255cf44c4ddbaada5bc7b3981ec7
ACR-7d7addab9d314d39a39646b2d3986899
ACR-3cbf46d36ad3412b9e3482495b0e1cc6
ACR-bda391de5a4849e79d44a5cae2f83b2e
ACR-7cd2511690574d428fb287fed08f0668
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
