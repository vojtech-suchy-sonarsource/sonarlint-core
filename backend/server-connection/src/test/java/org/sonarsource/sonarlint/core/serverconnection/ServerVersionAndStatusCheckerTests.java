/*
ACR-414178a25dcf44a08183c7f0dda83e34
ACR-8a265d676ad74d53b491e222ee5e5f27
ACR-4d100abb22564e3d84829fde732395f7
ACR-d3d949677e754d90b8f03843f71ca8c2
ACR-c72d770b6063465caca032561e203ae7
ACR-d3555a555060450d8b928cdcaeff6793
ACR-87595d5c5a6c419eba6cf11762ed56b1
ACR-bc175c243fca42419d08cc900c922489
ACR-75f89222ee9a48a18729fe16e9d918d5
ACR-7dd028d485de43768b3abdb2e4f7b357
ACR-a99ef6dc6a95417f92ad4fb2fe352db2
ACR-bf87283e3c3d45d086b05936b06ee444
ACR-3e6c756902f84c7a93049266bca57749
ACR-48b1e9512fc0473da0d73ed1c0932ed3
ACR-fb842b1060324e70a1f057168e41024c
ACR-542dd4415266491c9ab38ab42e67690c
ACR-0fe21c069363499bbfd98f22e9a54182
 */
package org.sonarsource.sonarlint.core.serverconnection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApi;
import testutils.MockWebServerExtensionWithProtobuf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class ServerVersionAndStatusCheckerTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  @RegisterExtension
  static MockWebServerExtensionWithProtobuf mockServer = new MockWebServerExtensionWithProtobuf();
  private ServerVersionAndStatusChecker underTest;

  @BeforeEach
  void setUp() {
    underTest = new ServerVersionAndStatusChecker(new ServerApi(mockServer.serverApiHelper()));
  }

  @Test
  void failWhenServerNotReady() {
    mockServer.addStringResponse("/api/system/status", "{\"id\": \"20160308094653\",\"version\": \"5.5-SNAPSHOT\",\"status\": \"DOWN\"}");

    var throwable = catchThrowable(() -> underTest.checkVersionAndStatus(new SonarLintCancelMonitor()));

    assertThat(throwable).hasMessage("Server not ready (DOWN)");
  }

  @Test
  void failWhenIncompatibleVersion() {
    mockServer.addStringResponse("/api/system/status", "{\"id\": \"20160308094653\",\"version\": \"6.7\",\"status\": \"UP\"}");

    var throwable = catchThrowable(() -> underTest.checkVersionAndStatus(new SonarLintCancelMonitor()));

    assertThat(throwable).hasMessage("Your SonarQube Server instance has version 6.7. Version should be greater or equal to 9.9");
  }

  @Test
  void shouldNotFailWhenIncompatibleVersionSc() {
    underTest = new ServerVersionAndStatusChecker(new ServerApi(mockServer.serverApiHelper("orgKey")));
    mockServer.addStringResponse("/api/system/status", "{\"id\": \"20160308094653\",\"version\": \"6.7\",\"status\": \"UP\"}");

    var throwable = catchThrowable(() -> underTest.checkVersionAndStatus(new SonarLintCancelMonitor()));

    assertThat(throwable).isNull();
  }

  @Test
  void responseParsingError() {
    mockServer.addStringResponse("/api/system/status", "bla bla");

    var throwable = catchThrowable(() -> underTest.checkVersionAndStatus(new SonarLintCancelMonitor()));

    assertThat(throwable).hasMessage("Unable to parse server infos from: bla bla");
  }

}
