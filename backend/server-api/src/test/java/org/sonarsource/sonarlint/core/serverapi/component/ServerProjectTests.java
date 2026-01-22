/*
ACR-c23db68207a34e85b4ada9882d0b9d69
ACR-617b41eab61448a59633178eebb0a90a
ACR-b40c0799f2f340ca979e589486291c7f
ACR-858f43d4643f4e6c8a4f186ddac48612
ACR-1cc20eec69cc46d5a928e2a1e31502a6
ACR-cf5b825db746426db89891408e6b56da
ACR-ba60dc36eea34ee2b8ff803b0beb24d0
ACR-d2a35e6de2264f09b025d79f314ee53c
ACR-79a9280be55a47be842693a30602f2c3
ACR-038bb3e5b58f4854a6042ee6a53aab84
ACR-7dc1c381aed44eeaabef7359bc978736
ACR-0f7ed6955c2b41a381019e052d1bd33f
ACR-e13f020f055247ba83cb147b1b668dd1
ACR-dfdff38941cf453083d1d90d986213cd
ACR-41f5feae92254b4993151a851b1b0ce3
ACR-7aa2672b44474040821a45719b1fb4b4
ACR-2fae5ac4bbf5426990af134b584eaae5
 */
package org.sonarsource.sonarlint.core.serverapi.component;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ServerProjectTests {
  @Test
  void testGetters() {
    ServerProject project = new ServerProject("key", "name", false);

    assertThat(project.key()).isEqualTo("key");
    assertThat(project.name()).isEqualTo("name");
  }

}
