/*
ACR-9ea555c9cb8d418cbed9e1896b2b5389
ACR-c6fac7a13335471ea02cc161c0d477da
ACR-7fc947880e4f471ca0f3ba21f7dcac88
ACR-4123c14375c54352b1172a8a8f04c9eb
ACR-10cbb0b1e0b642a0ba4c6c43b342921b
ACR-9f82510df9de4b92b09b72a94daab006
ACR-8802d1aeca4745caaff3c154421c60e3
ACR-3db4f3fddd884bf69b60dc065f6ac472
ACR-0fd015285cd54d0cb7bf3122f87153d9
ACR-a3c36921c2b74554805311fa23578984
ACR-e6d188479d544e00918d816f460a9b95
ACR-6580343102ba435895d4b6724242b655
ACR-226e19db54ab4c8b9c6bd1a9ce906270
ACR-0121aab14a85412da5116e1503039f11
ACR-643c77c69d2d44dfa1abff3b7ef43182
ACR-6b9e910cce5346f0ac200a035e117d65
ACR-20282ef81c204987b60c04575f9fc228
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
