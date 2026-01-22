/*
ACR-a5f72e20f8c84783b37dcc2cbd2c880e
ACR-7e47efd845a245fdb942a24db6df6200
ACR-9bd1135b5d53421dba2c11535ce95842
ACR-0f34a60314734bb8994e64d91a442b86
ACR-ad1492fa357e41fb94812c4ae5344bbb
ACR-7c35d69c34f048e3998d56462f2cadf8
ACR-9737bdd9e6fb4cd9bfc93d0ff699ee53
ACR-11791468c0854d1e8a7de84b8b8484de
ACR-78a05e4e427f456e9e4c666bbdcaf9c1
ACR-af7f9ca370fc45d8823dbcd1393cbc60
ACR-52967d0ecbf6408aadca4ab732f4d7ab
ACR-32101b2542bb4c01bc1476afc3c87a47
ACR-e8ef132cadfc4feba1326135e8c0425d
ACR-746c90d0191040b19bb471dd297abf9c
ACR-539b8f63c313405da1e90e2af4f23747
ACR-c698feea81344bfe9f016ae7adcdc7ea
ACR-c1c33826e5f7443aa6394ec693e69ba1
 */
package org.sonarsource.sonarlint.core.flight.recorder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.UserPaths;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FlightRecorderStorageServiceTests {

  public static final Clock BASE_TIME = Clock.fixed(
    Instant.parse("2007-12-03T10:15:30.00Z"),
    ZoneOffset.of("+2"));
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester(true);

  @TempDir
  private Path sonarUserHome;
  private FlightRecorderStorageService tested;

  @BeforeEach
  void setUp() {
    UserPaths userPaths = mock(UserPaths.class);
    when(userPaths.getUserHome()).thenReturn(sonarUserHome);
    tested = new FlightRecorderStorageService(userPaths);
  }

  @Test
  void should_create_file_with_date_time_in_name_and_date_row() {
    tested.appendData(BASE_TIME, Map.of());

    assertThat(getTimestampedFolder()).isDirectoryContaining("glob:**" + FlightRecorderStorageService.FILE_NAME);
    assertThat(getTestFile()).content()
      .contains("_____03/12/2007-12:15_____");
  }

  @Test
  void should_append_line_with_date_time() {
    tested.appendData(BASE_TIME, Map.of());
    tested.appendData(Clock.offset(BASE_TIME, Duration.ofMinutes(5)), Map.of());
    tested.appendData(Clock.offset(BASE_TIME, Duration.ofMinutes(15)), Map.of());

    assertThat(getTimestampedFolder()).isDirectoryContaining("glob:**" + FlightRecorderStorageService.FILE_NAME);
    assertThat(getTestFile()).content()
      .containsSubsequence(
        "_____03/12/2007-12:15_____",
        "_____03/12/2007-12:20_____",
        "_____03/12/2007-12:30_____"
      );
  }

  @Test
  void should_write_data_after_date_time() {
    tested.appendData(BASE_TIME, Map.of("param", "1", "param2", "2"));
    tested.appendData(Clock.offset(BASE_TIME, Duration.ofMinutes(5)), Map.of("param", "10"));
    tested.appendData(Clock.offset(BASE_TIME, Duration.ofMinutes(15)), Map.of("param", "100"));

    assertThat(getTimestampedFolder()).isDirectoryContaining("glob:**" + FlightRecorderStorageService.FILE_NAME);
    assertThat(getTestFile()).content()
      .containsSubsequence(
        "_____03/12/2007-12:15_____",
        "_____03/12/2007-12:20_____",
        "_____03/12/2007-12:30_____")
      .satisfies(content -> assertSegmentsInOrder(content, List.of(
        List.of("param=1", "param2=2"),
        List.of("param=10"),
        List.of("param=100"))));
  }

  @Test
  void should_create_new_folder_and_file_if_they_are_missing() throws IOException {
    var expectedFolder = getLogFolder().resolve("flight-recording-session-03-12-2007-12-20");

    tested.appendData(BASE_TIME, Map.of());
    Files.delete(getTestFile());
    tested.appendData(Clock.offset(BASE_TIME, Duration.ofMinutes(5)), Map.of());
    tested.appendData(Clock.offset(BASE_TIME, Duration.ofMinutes(15)), Map.of());

    assertThat(getTestFile()).doesNotExist();
    assertThat(expectedFolder).isDirectoryContaining("glob:**" + FlightRecorderStorageService.FILE_NAME);
    assertThat(expectedFolder.resolve(FlightRecorderStorageService.FILE_NAME)).content()
      .containsSubsequence(
        "_____03/12/2007-12:20_____",
        "_____03/12/2007-12:30_____"
      );
  }

  @Test
  void should_add_init_data_in_the_beginning_of_recreated_file() throws IOException {
    var expectedFolder = getLogFolder().resolve("flight-recording-session-03-12-2007-12-20");
    tested.populateSessionInitData(Map.of("initParam", "2"));

    tested.appendData(BASE_TIME, Map.of("param", "1"));
    Files.delete(getTestFile());
    tested.appendData(Clock.offset(BASE_TIME, Duration.ofMinutes(5)), Map.of("param", "10"));
    tested.appendData(Clock.offset(BASE_TIME, Duration.ofMinutes(15)), Map.of("param", "100"));

    assertThat(getTestFile()).doesNotExist();
    assertThat(expectedFolder).isDirectoryContaining("glob:**" + FlightRecorderStorageService.FILE_NAME);
    assertThat(expectedFolder.resolve(FlightRecorderStorageService.FILE_NAME)).content()
      .containsSubsequence(
        "_____03/12/2007-12:20_____",
        "_____03/12/2007-12:30_____")
      .satisfies(content -> assertSegmentsInOrder(content, List.of(
        List.of("param=10", "initParam=2"),
        List.of("param=100"))));
  }

  private static void assertSegmentsInOrder(String content, List<List<String>> segments) {
    var parts = content.split("_____.*_____");
    assertThat(parts).hasSize(segments.size() + 1);
    for (var i = 0; i < segments.size(); i++) {
      assertThat(parts[i + 1]).contains(segments.get(i));
    }
  }

  private Path getLogFolder() {
    return sonarUserHome.resolve("log");
  }

  private Path getTimestampedFolder() {
    return getLogFolder().resolve("flight-recording-session-03-12-2007-12-15");
  }

  private Path getTestFile() {
    return getTimestampedFolder().resolve(FlightRecorderStorageService.FILE_NAME);
  }
}
