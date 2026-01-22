/*
ACR-7d46519022564f92875a192da6c96f21
ACR-439be0c5f49643db8564e0f4cb3164b4
ACR-f13129238ec04507946f2b72518d565e
ACR-c389b4f36d9149cabab323eaf7a6609c
ACR-f0647781badd4c109f46077869b0e0ed
ACR-72e913bf2f71467ba0762d0c15265d28
ACR-eca880ccc6a5498c88493b86ae010dfa
ACR-7b980a68dc94406681b1c1fd21a74821
ACR-fe2988bd569344898e8f1befc0b4366e
ACR-07e43d068d9c473989f6500f191c0996
ACR-a3c9d68c0c264b11b4d67aa0ea20ec6e
ACR-c66308e58a394db4b3b3c7c8b9900e27
ACR-78e88895c34a44ea83e66103b09f6e34
ACR-f079e29d67b648cf9ec7ceeee56df300
ACR-a244a8b4a2dd4d4cb255013cbbb2f49b
ACR-c6efb9fadabd43989f7b6fd298065ab8
ACR-acddf4715e754854acab3d96c6e9d9b1
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
