/*
ACR-4fa6f3b5572f4ed3a05034ce66f2d0d4
ACR-2ba7745472cf4c2eb5d776ab5c3cfb00
ACR-c7cb7173714b432aa517ca81a1249aa0
ACR-90a49be16d2048e8b7a6230cead59772
ACR-6e653c764ba7490a9177123d8c41d9bf
ACR-84741d373d884c43912653f27b5663fd
ACR-f075cca2a8a047f3ab5e6e73331e95b3
ACR-5d875c4e1976418c9caaf1fd3edd3b40
ACR-aba0e69781eb401cbd3f75ac75c55863
ACR-fd4797dd83a14d43b133019c3e4d48c5
ACR-0e03104120d548829d39219e8f1019d8
ACR-5dd5bcd88fc64dcbb4c3f570738a8e70
ACR-9168a29b2c3e41ed8da32666e1d15b09
ACR-e1b3898d03144633a5133cfba5519dfe
ACR-948321d1767d4593b5b0512bf473906f
ACR-bf83ae1a533d4ba0bae2f4a55f757f22
ACR-1ea59aa5412f4bcaa4eb99d2906291d3
 */
package org.sonarsource.sonarlint.core.flight.recorder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.sonarsource.sonarlint.core.UserPaths;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

public class FlightRecorderStorageService {

  public static final String FILE_NAME = "flight-records.txt";
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private static final DateTimeFormatter FILE_NAME_FORMATTER = DateTimeFormatter.ofPattern("'flight-recording-session'-dd-MM-yyyy-HH-mm");
  private static final DateTimeFormatter RECORD_HEADER_FORMATTER = DateTimeFormatter.ofPattern("_____dd/MM/yyyy-HH:mm_____");

  private final Map<String, String> sessionInitData = new HashMap<>();
  private final Path logFolder;
  private String sessionFolderName;

  public FlightRecorderStorageService(UserPaths userPaths) {
    logFolder = userPaths.getUserHome().resolve("log");
  }

  public void populateSessionInitData(Map<String, String> initData) {
    sessionInitData.putAll(initData);
  }

  public void appendData(Clock clock, Map<String, String> data) {
    var dateTime = LocalDateTime.now(clock);
    try {
      var filePath = getRecordFile(dateTime);
      var isEmptyFile = Files.size(filePath) == 0;
      var records = data;
      if (isEmptyFile) {
        records = populateWithInitialData(data);
      }
      var recordsWithHeader = getRecordsWithHeader(records, dateTime);
      Files.write(filePath, recordsWithHeader, StandardOpenOption.APPEND);
    } catch (IOException e) {
      LOG.error("Failed to write to a flight recorder file.", e);
    }
  }

  private HashMap<String, String> populateWithInitialData(Map<String, String> data) {
    var populated = new HashMap<>(sessionInitData);
    populated.putAll(data);
    return populated;
  }

  private static List<String> getRecordsWithHeader(Map<String, String> data, LocalDateTime dateTime) {
    return Stream.concat(
      Stream.of(RECORD_HEADER_FORMATTER.format(dateTime)),
      data.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
    ).toList();
  }

  private Path getRecordFile(LocalDateTime dateTime) throws IOException {
    if (sessionFolderName == null) {
      sessionFolderName = FILE_NAME_FORMATTER.format(dateTime);
    }
    if (!Files.exists(getFilePath())) {
      sessionFolderName = FILE_NAME_FORMATTER.format(dateTime);
      Files.createDirectories(logFolder.resolve(sessionFolderName));
      Files.createFile(getFilePath());
    }
    return getFilePath();
  }

  private Path getFilePath() {
    return logFolder.resolve(sessionFolderName).resolve(FILE_NAME);
  }
}
