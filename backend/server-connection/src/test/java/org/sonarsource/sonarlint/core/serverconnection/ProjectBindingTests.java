/*
ACR-1c9c3d36d285492abf42845c1ec2ea68
ACR-695e84f3569e4e75a01f3d04d6f746b5
ACR-0d75b19c1bf247099a19f858daa99682
ACR-3e69a662fa914e26b138191b293bde61
ACR-f89ab7af6c1a404f9980c13ab2afc66f
ACR-b31910ae32cb456d8f2f7777d0362258
ACR-510e86114b7344f7a2118f18c58ff713
ACR-85feaf1157fd43768f9028d9ba9c443e
ACR-1a8ba2a182f24afba82800faf9cd472b
ACR-18752abc64d64b91a4eff5a5396c5f70
ACR-2725c2c3c5f343609b7c2304d9744fbd
ACR-3f3bffe33e7a44f5be276f26e9698a5c
ACR-15115004a77946f5ba2f38fd7af68fd5
ACR-796ca545e44b43788100a79a789072b6
ACR-f6d557b69c754623a4a0a4110ef36dac
ACR-91060798d26b4760b8df08716a6c7d7d
ACR-1d5bf461f1dc46a1806a572c01ea1f61
 */
package org.sonarsource.sonarlint.core.serverconnection;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectBindingTests {
  @Test
  void should_assign_all_parameters_in_constructor() {
    var projectBinding = new ProjectBinding("key", "sqPrefix", "localPrefix");
    assertThat(projectBinding.projectKey()).isEqualTo("key");
    assertThat(projectBinding.serverPathPrefix()).isEqualTo("sqPrefix");
    assertThat(projectBinding.idePathPrefix()).isEqualTo("localPrefix");
  }

  @Test
  void equals_and_hashCode_should_use_all_fields() {
    var projectBinding1 = new ProjectBinding("key", "sqPrefix", "localPrefix");
    var projectBinding2 = new ProjectBinding("key1", "sqPrefix", "localPrefix");
    var projectBinding3 = new ProjectBinding("key", "sqPrefix1", "localPrefix");
    var projectBinding4 = new ProjectBinding("key", "sqPrefix", "localPrefix1");
    var projectBinding5 = new ProjectBinding("key", "sqPrefix", "localPrefix");

    assertThat(projectBinding1.equals(projectBinding2)).isFalse();
    assertThat(projectBinding1.equals(projectBinding3)).isFalse();
    assertThat(projectBinding1.equals(projectBinding4)).isFalse();
    assertThat(projectBinding1.equals(projectBinding5)).isTrue();

    assertThat(projectBinding1.hashCode()).isNotEqualTo(projectBinding2.hashCode());
    assertThat(projectBinding1.hashCode()).isNotEqualTo(projectBinding3.hashCode());
    assertThat(projectBinding1.hashCode()).isNotEqualTo(projectBinding4.hashCode());
    assertThat(projectBinding1.hashCode()).isEqualTo(projectBinding5.hashCode());
  }

  @Test
  void serverPathToIdePath_no_match_from_server_path() {
    var projectBinding = new ProjectBinding("key", "sqPrefix", "localPrefix");
    assertThat(projectBinding.serverPathToIdePath("notSqPrefix/some/path")).isEmpty();
  }

  @Test
  void serverPathToIdePath_general_case() {
    var projectBinding = new ProjectBinding("key", "sq/path/prefix", "local/prefix");
    assertThat(projectBinding.serverPathToIdePath("sq/path/prefix/some/path")).hasValue("local/prefix/some/path");
  }

  @Test
  void serverPathToIdePath_empty_local_path() {
    var projectBinding = new ProjectBinding("key", "sq/path/prefix", "");
    assertThat(projectBinding.serverPathToIdePath("sq/path/prefix/some/path")).hasValue("some/path");
  }

  @Test
  void serverPathToIdePath_empty_sq_path() {
    var projectBinding = new ProjectBinding("key", "", "local/prefix");
    assertThat(projectBinding.serverPathToIdePath("some/path")).hasValue("local/prefix/some/path");
  }
}
