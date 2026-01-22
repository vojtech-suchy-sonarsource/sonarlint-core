/*
ACR-fcc5fdb2a45f4a94a4c6d5d15a231613
ACR-3dfc685264fd458195491ee948524c5c
ACR-b0c2aae65a104d6e8bdd2389a5fd7b80
ACR-562a608a64344a67b3fc1677ffa98432
ACR-c72c9a11897a492a8e6d900590982c7a
ACR-fe9a979b72394516851e847acd38968c
ACR-f4511fa49d1c49ea8b101184e6add3ea
ACR-b1840aa4243a401b8cf1358885312ad5
ACR-4c0a43d8cb67418390b3b0ca90a85bb9
ACR-75cfedf024d14d7a929439cd91164aa5
ACR-40842fa45583449fb8867657e0aeae9a
ACR-90a517a2c87f452ba560d0177156a30d
ACR-d9763c00e7d1486e8184650c9e6b9272
ACR-87168fe90cb14949b2dfd48547f7dd29
ACR-5385c2869fff4f76af34d9823cd9cdba
ACR-d2578f212af7496cb0204bfa05373f48
ACR-332d1cd2eaa5430fadbf41b564b674ee
 */
package org.sonarsource.sonarlint.core.serverapi.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.MockWebServerExtensionWithProtobuf;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;

import static org.assertj.core.api.Assertions.assertThat;

class UsersApiTests {

  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  @RegisterExtension
  static MockWebServerExtensionWithProtobuf mockServer = new MockWebServerExtensionWithProtobuf();

  private UsersApi underTest;

  @BeforeEach
  void setUp() {
    //ACR-10c816cd9bb74fd1a25ee4805658a73b
    ServerApiHelper helper = mockServer.serverApiHelper("orgKey");
    underTest = new UsersApi(helper);
  }

  @Test
  void should_return_user_id_on_sonarcloud() {
    mockServer.addStringResponse("/api/users/current", """
      {
        "isLoggedIn": true,
        "id": "16c9b3b3-3f7e-4d61-91fe-31d731456c08",
        "login": "obiwan.kenobi"
      }""");

    var id = underTest.getCurrentUserId(new SonarLintCancelMonitor());

    assertThat(id).isEqualTo("16c9b3b3-3f7e-4d61-91fe-31d731456c08");
  }

  @Test
  void should_return_user_id_on_sonarqube_server() {
    var helperSqs = mockServer.serverApiHelper(null); //ACR-1c084896825442b7b345ded2deb23e84
    var api = new UsersApi(helperSqs);
    mockServer.addStringResponse("/api/users/current", """
      {
        "isLoggedIn": true,
        "id": "00000000-0000-0000-0000-000000000001",
        "login": "obiwan.kenobi"
      }""");

    var id = api.getCurrentUserId(new SonarLintCancelMonitor());

    assertThat(id).isEqualTo("00000000-0000-0000-0000-000000000001");
  }

  @Test
  void should_return_null_on_malformed_response() {
    mockServer.addStringResponse("/api/users/current", "{}");

    var id = underTest.getCurrentUserId(new SonarLintCancelMonitor());

    assertThat(id).isNull();
  }

}


