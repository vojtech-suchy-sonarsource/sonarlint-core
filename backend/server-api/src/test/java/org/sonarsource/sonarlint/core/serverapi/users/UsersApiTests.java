/*
ACR-0ec57539f5094b8b836ebc130327f32c
ACR-9c27db9f4f314088819c248c48d2aa93
ACR-43fae06fb61e4a8c974c438546b30610
ACR-9533c519d7884ac692a1737a50f72cd2
ACR-deaa639045ed43fdb65cf3a3f276d240
ACR-bb6fde20de1341e2bff2568d23e1a872
ACR-f882ab56c6364bf2bf9daf86acd78671
ACR-a3d292259c8c4fb28e8059f8108b639c
ACR-e8cda87e4cfe40ac91bb74589ab90519
ACR-5de548c1365e422ba7d64c0201aceb26
ACR-890304fe40f1412a89bd18a9267c2b36
ACR-021a86cce30240acad58a0c3a770df32
ACR-29e325fb97524b45afc0d4e27e6e3f77
ACR-e817dd5c504049f6a131caf32908471f
ACR-4957dba313674e7f999f4bd31a22c227
ACR-82ca6c799a264f41b77baae3dfac22b6
ACR-f10e865d0cd440a48794b3c989e0db4c
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
    //ACR-d3413b6cc1a64ec9ac742c40e50fb8e9
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
    var helperSqs = mockServer.serverApiHelper(null); //ACR-1a2de27c009544609b9964958b090fd2
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


