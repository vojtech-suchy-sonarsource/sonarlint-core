/*
ACR-1de8e03532f04fada6927057bf9e4095
ACR-697ff55535fe4dbe82cd95bb1023f120
ACR-b38fdfc45363411bb79b592a2cf5bc12
ACR-fbbb48dffc76422081f4e218d924fa37
ACR-fa24c236c2a943c8ac737971a4fc03f1
ACR-054723e7bdf141bfa9b03c90c8645570
ACR-5847e1d059d648d9a4ed373f934dc6d0
ACR-a4345e71fa2d40eb89cd1222607f19b7
ACR-33cf7f86c58d44adad3121809592d0d8
ACR-56e528badc504745bcd01b3e578f7348
ACR-e33937a5a23a4811b2f966849c2d9437
ACR-3c9ed1014a6c4ddb918795842348995c
ACR-328d65d545694aa3a7c6d2b1a21aeb13
ACR-ee0990b25079463aa48a21d803973329
ACR-815f315ebf8648cf8772961b56e9bb01
ACR-51ddd3c91d7a4b0d882bd2c14c714905
ACR-3f4ebe4b46594becb05922b35b49d37f
 */
package org.sonarsource.sonarlint.core.serverapi.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectNotFoundExceptionTests {

  @Test
  void show_organization_key() {
    var ex = new ProjectNotFoundException("module", "organization");
    assertThat(ex.getMessage()).isEqualTo("Project with key 'module' in organization 'organization' not found on SonarQube Cloud (was it deleted?)");
  }

  @Test
  void organization_key_missing() {
    var ex = new ProjectNotFoundException("module", null);
    assertThat(ex.getMessage()).isEqualTo("Project with key 'module' not found on your SonarQube Server instance (was it deleted?)");
  }
}
