/*
ACR-8b13010acf7e43b3a03492e7057f0a0e
ACR-2108ee4357ab453c8888041423457846
ACR-300924a9b4c0416d97207aa33aa41547
ACR-661c3978a6634670bcb0d453ab5115a1
ACR-2227aeea6bdc475a94f4e8203f69d15d
ACR-4c7bd1125bb244328796461172e3eef9
ACR-25f5004f814a47f18aa504e42373d7e6
ACR-3bec8bf424d245e6805548b467bb7390
ACR-8399ef82f16a497e858533b22327828e
ACR-f3d9d6b2308a4657b541738bbc805cc1
ACR-2f552d944d84423d943602a415f4a09b
ACR-6a302f210d42404fa822a1e2abcf58ab
ACR-fc4ab74ac17649a286acf8aebdb680d4
ACR-71c8fb913d7444a6b8650a001d6278d7
ACR-69c1cd576793436eb16a686db5668e80
ACR-f0900ab45537494d967ffb7adeeb89da
ACR-a4e50d82ff0347159e470d0b6da51f75
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
