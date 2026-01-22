/*
ACR-77189dcd0f194149a4bdbcfd120a9670
ACR-9b6b90764d8d4aa5baaaf0366f35fd71
ACR-dab7a9be76594365ba8b89916dfdb46f
ACR-021e54deb133467ab4a5bb492b5e67e4
ACR-f3abb0073b9747be9bfa0dcd4ec9c730
ACR-52840bb94cb04ecc8a058d0c0d203195
ACR-5824af15d24e4d9abe5f2aa3577922de
ACR-a306c3d45e7b4eb78bd71485dfb41488
ACR-50f66bb9bb884e309e2df41af7c540e4
ACR-14291fa2b86e49ecb736c0607f88ea37
ACR-ad465ae49da04840b9d03e481bedc3c3
ACR-8d1dd9f59d8340078e15bae73eecd3db
ACR-69c2dead97ef45b6907208c8f0c114c1
ACR-a052be1d52424610b6b9622b5aa58878
ACR-d67cd147564543c99ef669ccd37c1b6b
ACR-66dafb79a0534f45a770ad89a716dfc4
ACR-5dbbe4545c174193bb36feef7b37f3e4
 */
package org.sonarsource.sonarlint.core.repository.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BindingConfigurationTest {

  @Test
  void test_isBound() {
    BindingConfiguration noBinding = BindingConfiguration.noBinding();
    assertThat(noBinding.isBound()).isFalse();

    BindingConfiguration noProjectKey = new BindingConfiguration("connection", null, true);
    assertThat(noProjectKey.isBound()).isFalse();

    BindingConfiguration noConnection = new BindingConfiguration(null, "projectKey", true);
    assertThat(noConnection.isBound()).isFalse();

    BindingConfiguration valid = new BindingConfiguration("connection", "projectKey", true);
    assertThat(valid.isBound()).isTrue();
  }
}
