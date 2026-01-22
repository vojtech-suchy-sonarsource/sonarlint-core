/*
ACR-06b55e6fd99d4d1d930d331491e85fcc
ACR-e3af85ac148a4dc0975d5918b6ea4efa
ACR-0b2d41a218bb414fae2e15a67ca33015
ACR-7b0247fa2f514c80ac7255b6f02c9792
ACR-9bec606a0df743098206d829bbb2b547
ACR-d2ba5701afb74630901dd4102de28a93
ACR-4942081d15a94fa5829f5e879b70ab99
ACR-446bf34c6096463f85290f3302c584ee
ACR-3c42c798ab974962aa4d6aa8413fabb1
ACR-b823a9b6af7d48ad992c4ec88fb09225
ACR-a3d2eb6a8d7846e89962e8d5605684b0
ACR-67b7dbe639f54900a30f13c82ee4999c
ACR-eadccf30564c4af08532bc76f06063a1
ACR-0ef9fb4b1c8d4282ad07d24b850252de
ACR-795091b13f5048709cd86a1daaaa9ed9
ACR-ae7e2e6e5ce34285852cd4c82b26b040
ACR-fa7549a09f2942339f42553e0c5ae4ab
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
