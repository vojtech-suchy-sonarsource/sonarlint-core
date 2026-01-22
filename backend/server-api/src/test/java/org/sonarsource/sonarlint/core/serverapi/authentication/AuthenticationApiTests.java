/*
ACR-0cfbc25d726d45b780650613b3079ae4
ACR-70d59fdcc0c9434bab83a6b5440bbf5b
ACR-37a6a7e6bae2454aa833376f0dc19648
ACR-319612a99050426daf455ba961534608
ACR-6cf2e7d37d25411a893e8cddb533f96f
ACR-0af5bdf515d443a69f95756f332662df
ACR-829884cd8bd740ca8e0411db10a0f988
ACR-b87e776c039f4d48b7c9c5df110ffd47
ACR-9775be2e1a874665b56a1c5812afa89f
ACR-7987558c50f24b7ca101f8be8a0a55f4
ACR-ed6cc4fe934b49649f04ccd7302c19bd
ACR-504a0a02d7584aa59ef3b20a29ee1824
ACR-248a094d3d3b4dbeab8c4469bc4a532e
ACR-b4f530c9629349459a453344581ff729
ACR-3f0eae162bdb43dc97b5d38393d216d2
ACR-762825eb97354e79a86e015289e3cb04
ACR-cec6bd61e97b4d00a8ab003e4adceb15
 */
package org.sonarsource.sonarlint.core.serverapi.authentication;

import mockwebserver3.MockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.MockWebServerExtensionWithProtobuf;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticationApiTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  @RegisterExtension
  static MockWebServerExtensionWithProtobuf mockServer = new MockWebServerExtensionWithProtobuf();
  private AuthenticationApi underTest;

  @BeforeEach
  void setUp() {
    underTest = new AuthenticationApi(mockServer.serverApiHelper());
  }

  @Test
  void test_authentication_ok() {
    mockServer.addStringResponse("/api/authentication/validate?format=json", "{\"valid\": true}");

    var validationResult = underTest.validate(new SonarLintCancelMonitor());

    assertThat(validationResult.success()).isTrue();
    assertThat(validationResult.message()).isEqualTo("Authentication successful");
  }

  @Test
  void test_authentication_ko() {
    mockServer.addStringResponse("/api/authentication/validate?format=json", "{\"valid\": false}");

    var validationResult = underTest.validate(new SonarLintCancelMonitor());

    assertThat(validationResult.success()).isFalse();
    assertThat(validationResult.message()).isEqualTo("Authentication failed");
  }

  @Test
  void test_connection_issue() {
    mockServer.addResponse("/api/authentication/validate?format=json", new MockResponse.Builder().code(500).body("Foo").build());

    var validationResult = underTest.validate(new SonarLintCancelMonitor());

    assertThat(validationResult.success()).isFalse();
    assertThat(validationResult.message()).isEqualTo("HTTP Connection failed (500): Foo");
  }
}
