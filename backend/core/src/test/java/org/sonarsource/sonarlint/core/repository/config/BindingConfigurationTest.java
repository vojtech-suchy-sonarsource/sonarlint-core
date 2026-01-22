/*
ACR-1e7205eaafc14da196558521c603e07f
ACR-95e29005905b4b0d975ad85122daa8ea
ACR-e5170221109942f28269178fadd543c3
ACR-3009178b2b594d968f747ed3018293c6
ACR-7038336e11d84b21ac597d214cdf1947
ACR-8bfb97b7389544228096467df781136d
ACR-41299dd3792b4dc6a9d9dfcc53d9a836
ACR-e312f494b3c64a6bb6bc5c013d7a0367
ACR-322bcc2ac07348e687896bed32cf284d
ACR-9c490ab2206b4bd9bfc57edc9c3bc5e7
ACR-b20e0b6268bc4b348875bfa86103a2f9
ACR-54770117c63a4bb0a6fae67e5f94ec62
ACR-cd9cef8723394f85ba1dd2a0ebc96367
ACR-4425f47398754c10a1f3931d58af6712
ACR-2d7bb5f16c9845feac3d2a8cc6eff33d
ACR-2a563b1002d3402a868bcecd7edfd180
ACR-23c7eae3757e491a82dbc77e51f7f0ab
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
