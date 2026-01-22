/*
ACR-4ac58f26146b479a84e0d2a9f519aebe
ACR-09f1541e9be64ae287e636cbe48aa6b2
ACR-45fc004eec564ddfa971889b5a433097
ACR-588b444b2dba4fb6a5c3cf324a608771
ACR-dbcdb46b5f124049b8650a68d4999bd6
ACR-01c57ea430914d9bae688d5aa46a710c
ACR-1a18cb755e094945a2e73b129532d1fb
ACR-5176730869f643dea57029874079daed
ACR-6bc51e14953e45eeb08e251b2562aed4
ACR-99cce2becd284997a2b5de5442763184
ACR-c0bd0bf2765e460f83ce38319be989bf
ACR-f9075648077d4b0fb85b6feef422fec2
ACR-ea13bb8121c44f31914888341ad3c57e
ACR-6e493e5085e4468087f08010ce941e8f
ACR-4abfe7a3c624409ca4ccad171c48fb6c
ACR-979a3171fa554bef80fc364e78e04cf0
ACR-a78de1abffc84deba173e411838eac1f
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
