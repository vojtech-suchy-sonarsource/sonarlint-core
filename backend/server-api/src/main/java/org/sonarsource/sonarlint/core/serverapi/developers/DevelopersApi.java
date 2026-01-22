/*
ACR-8fb05589bcc64b0c9d85c2ec128d40a8
ACR-278f1b32dc1147afb6dd8b9769646845
ACR-e1314e66f1344eb4a97c04e56c8b652b
ACR-683971ad33e54e5a9c28ed049b5eae3b
ACR-785fa00258a74f67a859229e5fef43d3
ACR-c91701f064254fc3bbb27b009a0d1a3b
ACR-16984ede87b246b785da907a3f811538
ACR-fdc153eabe4d48e5b3393446b81145fe
ACR-bb302f7ef5f1471a9ed46ecfbbf99517
ACR-9fde04a1b8744a008861eafee7bce32f
ACR-eb2f443f3b8b48cea5c06a8b8b483935
ACR-3c4bf78984364fea8e350cb393f662c4
ACR-a07100e72a48404c989f9bec9cdfb137
ACR-3e4c99807c514b1993db2aacc25bd164
ACR-5506a74957034b119697e4e5017d8376
ACR-01ac34016123412a878f52f976a8eb1e
ACR-5d6185c75df9493b9df11876da93cae1
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
    //ACR-f4b50d7e9b6045d09510e8e1b6ad3b5c
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
