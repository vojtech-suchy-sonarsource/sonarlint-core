/*
ACR-b39e327f9033402c87697812593df6c1
ACR-f2a019b932d243deaead00b4f9969fb0
ACR-d396ef6a31fc43abafc3feed96f3588c
ACR-f05d0f8e5086451a8a78910ab4472c98
ACR-5736612c5d554e5683b1e305f02bc629
ACR-2ee5904748b3423382d2e97cb8863bf5
ACR-7afc3b1294684275af76ef45b10ee587
ACR-ff9a9788ded942118df4c3101efbcae6
ACR-71b832ac13b143e5a71381948ff435f8
ACR-a6a84bc6faf44603b4dacda663e5ed17
ACR-e98774997fa7456eb27a3e85eba68e09
ACR-8d57260ca61943efbfe0511a376cef45
ACR-ad41df5c869f4b67b90023a3056ccb82
ACR-1540694ffa0f457eb8d0df3827a0d925
ACR-e2a6067fba2642029f875f8db188e394
ACR-626ec953fed944dfae0f15e046b42ada
ACR-438df4cf379c4970bafe4fef464d20fb
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
