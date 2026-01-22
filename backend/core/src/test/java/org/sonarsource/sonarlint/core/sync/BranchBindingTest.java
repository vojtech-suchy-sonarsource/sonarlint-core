/*
ACR-d2963fb3687b4f359195ec085919b800
ACR-b9b6a8e5da3e4e1ab4c0da41fa2d6748
ACR-6ac1bb80482647d2a6537cff0174e249
ACR-7557d139784d4a6f9d37cdfab767bed7
ACR-0795de9f95ec4a349494abc6c5a5fb31
ACR-8c118c27b0504ae0ad15287349dfa2d7
ACR-83b01245ad6a43379d8ae6e4fbff8bd8
ACR-06fdc538438e40fd920c1eef59d28272
ACR-b4ae32b7988d4ec7b04a54205ff05768
ACR-7f22175a45544b2481927da2b392ef1e
ACR-12ebd27ed862436591debdb2ef0a1310
ACR-d4b52b749166411cbe2242153da90fdc
ACR-6c8148a49326401a8a73e6d0f88ee729
ACR-437e5d9db1064f0abaddd204d47b02d9
ACR-53c44bbe26b64938a5c660ef95e6c1aa
ACR-cf6bff433cdc4985b8a3f299efd34d7e
ACR-f4a30ce2e30a4ed99930235b41bf40e4
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
