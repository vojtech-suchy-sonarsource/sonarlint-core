/*
ACR-d5e8ea43f7cc4498aad0725da6588937
ACR-ace0d96524d4425e8079d1a82d76369d
ACR-3af4cb721f624752b3b140c95a61356a
ACR-bb77ad2633fe4a9baabd79408729ceb3
ACR-1dcfbaff9e1d4982a445a9bfc256cca9
ACR-fe55cff73e8a4d27abbc5966024a6230
ACR-da6e0f9628084af58b7c05d4c156d543
ACR-a42bf6af359e4eabadd5421a7323a0fb
ACR-cc1d630c168e4010911b81fb07118f59
ACR-104614ee160048d183f7c55d412d2954
ACR-f02ad3e1aee94195b0ba8e55604828ac
ACR-7587146b4f9a46afa661b38a2631be56
ACR-ac91388f6bcb4fab8a6c1ce719a28b02
ACR-af478af32ce94fe9b1781ddc01af8588
ACR-f1b6870d66d04231b5996ef6baaac220
ACR-75e0cd9fee3246efbdc3c492bc32092d
ACR-6a478b491ccf41acaf874291f87e9eaa
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.projects;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SonarProjectDtoTest {

  @Test
  void should_have_object_methods() {
    var one = new SonarProjectDto("mySearchTerm", "project");
    var other = new SonarProjectDto("mySearchTerm", "project");

    assertThat(one)
      .isEqualTo(other)
      .hasSameHashCodeAs(other)
      .hasToString("SonarProjectDto{key='mySearchTerm', name='project'}");
  }
}
