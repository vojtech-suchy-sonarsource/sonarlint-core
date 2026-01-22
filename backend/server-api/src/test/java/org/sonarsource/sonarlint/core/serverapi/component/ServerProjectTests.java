/*
ACR-8b2089c3e5354a92b59ff203d530110d
ACR-eded7813a4f34670b846d37d4d31a707
ACR-502d7edbafdb42a4b9d3680ce4e6637b
ACR-aac6a164585c41008bea01891b98481e
ACR-43aad4eb491b479db1d65ff8190c7556
ACR-ccb08475e45643ce9227aac79a5a82da
ACR-787a33d176a1470ca75522fa3ad597e3
ACR-e17d8a5f4e7c4a1f9c7bff762845bb23
ACR-a702279602364b56b1f5fa9e89b86d4f
ACR-4d12c9c475c747a99dd3dad1f70d3c3c
ACR-6f973869826041498d5587ca618f5c17
ACR-37b851933776480781b417f7822c2ab2
ACR-8ed4e55c4cea4d1fb46aa2633912d229
ACR-0f68b8f751e449ecb57ada32ac7d5108
ACR-1c23f1541f2f4166bb4e13cf4f17e6dd
ACR-35f82771c4cb44ea94a93aa7671617ce
ACR-83c69fbee2804c048ab49a7e409c5620
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
