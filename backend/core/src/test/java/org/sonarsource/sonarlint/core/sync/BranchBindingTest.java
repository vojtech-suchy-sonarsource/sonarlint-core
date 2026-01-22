/*
ACR-da680ef34d1a4d1a9b36b962bc1192cb
ACR-b843ee39d35d49bd99a2f85c1cf222be
ACR-30b3406ef1a343a88362eeb744ddc867
ACR-cb460229660e42ddbb3fb04d3114c7d1
ACR-ff0490bcb8f44e1b8a728eda82a55a16
ACR-4018d6f4825b49938a58943f3c11aba2
ACR-ea460354e29b444da1f7c78397c4ac58
ACR-78f1b50e0a754b28930456622e9bd10e
ACR-733ea1ede2f64484915ae309e7845957
ACR-41621b9e46b94d3994696c8e32f8d52c
ACR-cbf987aed62c4e30a4315356bae00625
ACR-d6c299c131c840508d0d48f0273f3f70
ACR-78eb20463cc141ffa0270d82c0c91117
ACR-6523c944b42a486bbed94276c475a2d6
ACR-3289ff1699f94fa3b2bdfa60fbc5f664
ACR-9015d91ddaed4fea991b87e5827cde35
ACR-20f052905fb047c291e96a1ad6cd2672
 */
package org.sonarsource.sonarlint.core.sync;

import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.commons.Binding;

import static org.assertj.core.api.Assertions.assertThat;

class BranchBindingTest {

  @Test
  void testEquals() {
    var branchBinding = new BranchBinding(new Binding("connection", "projectKey"), "branch");

    assertThat(branchBinding.equals(branchBinding)).isTrue();
    assertThat(branchBinding.equals(null)).isFalse();
    assertThat(branchBinding.equals(new Object())).isFalse();
    assertThat(branchBinding.equals(new BranchBinding(new Binding("connection2", "projectKey"), "branch"))).isFalse();
    assertThat(branchBinding.equals(new BranchBinding(new Binding("connection", "projectKey2"), "branch"))).isFalse();
    assertThat(branchBinding.equals(new BranchBinding(new Binding("connection", "projectKey"), "branch2"))).isFalse();
    assertThat(branchBinding.equals(new BranchBinding(new Binding("connection", "projectKey"), "branch"))).isTrue();
  }
}
