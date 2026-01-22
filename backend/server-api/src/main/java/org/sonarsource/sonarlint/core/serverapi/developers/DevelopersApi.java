/*
ACR-f7990fb2cc04470d843669e764c51253
ACR-8451ab6325ea448aa1102bee7c793559
ACR-578b793900bc4d1dbaf30e3aa08f62d2
ACR-7dbe02fe03104e1bb464a35e2fb9612b
ACR-83a00d8794da4a0d9299b6a48d0649a0
ACR-7c6ee6fa1e3343669f457cfd6ebcfa39
ACR-17d2ae52b3fe47b7bed8353b964d38ba
ACR-e62088cf6b6847beb625f92c278fd890
ACR-3d00950c191847c990f0af479b70bf77
ACR-be0ea78ed51e446ead826bd50629b95d
ACR-39da61ba6e554e58915a7556093f794f
ACR-c8ae43219581403395e734fd74ccbfba
ACR-e4da7eee43654697aff3d45541963895
ACR-fbad49505f14461f92473677defad168
ACR-adf56e8ae52d4f7ab93e05e89b855b57
ACR-f637ff67aaa84354a17af03b4d0e2ba0
ACR-f53068f42f524e1698c344079aae0748
 */
package org.sonarsource.sonarlint.core.serverapi.developers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;
import org.sonarsource.sonarlint.core.serverapi.UrlUtils;

public class DevelopersApi {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private static final String API_PATH = "api/developers/search_events";
  public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

  private final ServerApiHelper helper;

  public DevelopersApi(ServerApiHelper helper) {
    this.helper = helper;
  }

  public List<Event> getEvents(Map<String, ZonedDateTime> projectTimestamps, SonarLintCancelMonitor cancelMonitor) {
    var path = getWsPath(projectTimestamps);
    try (var wsResponse = helper.rawGet(path, cancelMonitor)) {
      if (!wsResponse.isSuccessful()) {
        LOG.debug("Failed to get notifications: {}, {}", wsResponse.code(), wsResponse.bodyAsString());
        return Collections.emptyList();
      }

      return parseResponse(wsResponse.bodyAsString());
    }
  }

  private static List<Event> parseResponse(String contents) {
    List<Event> notifications = new ArrayList<>();

    try {
      var root = JsonParser.parseString(contents).getAsJsonObject();
      var events = root.get("events").getAsJsonArray();

      for (JsonElement el : events) {
        var event = el.getAsJsonObject();
        var category = getOrFail(event, "category");
        var message = getOrFail(event, "message");
        var link = getOrFail(event, "link");
        var projectKey = getOrFail(event, "project");
        var dateTime = getOrFail(event, "date");
        var time = ZonedDateTime.parse(dateTime, TIME_FORMATTER);
        notifications.add(new Event(category, message, link, projectKey, time));
      }

    } catch (Exception e) {
      LOG.error("Failed to parse SonarQube notifications response", e);
      return Collections.emptyList();
    }
    return notifications;
  }

  private static String getOrFail(JsonObject parent, String name) {
    var element = parent.get(name);
    if (element == null) {
      throw new IllegalStateException("Failed to parse response. Missing field '" + name + "'.");
    }
    return element.getAsString();
  }

  private static String getWsPath(Map<String, ZonedDateTime> projectTimestamps) {
    //ACR-3445305c8b2e4e85aafb5f53d3e87400
    var sortedProjectKeys = projectTimestamps.keySet().stream().sorted().toList();
    var builder = new StringBuilder();
    builder.append(API_PATH);
    builder.append("?projects=");
    builder.append(sortedProjectKeys.stream()
      .map(UrlUtils::urlEncode)
      .collect(Collectors.joining(",")));

    builder.append("&from=");
    builder.append(sortedProjectKeys.stream()
      .map(projectTimestamps::get)
      .map(timestamp -> timestamp.format(TIME_FORMATTER))
      .map(UrlUtils::urlEncode)
      .collect(Collectors.joining(",")));

    return builder.toString();
  }
}
