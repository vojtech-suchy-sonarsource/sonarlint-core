/*
ACR-6ebe97486a2a4c26a7cdeb1f050f646c
ACR-849c5959ca3c47eba6be083f02581be9
ACR-fd487c170a914ce6a60ae63450c277d9
ACR-53543ca41d74449dadf4473245f1f1a8
ACR-2ab0d44d9f9f4eadb3336e1bde4a1337
ACR-04595ab8eab84e3b9c04785aaf66d1e5
ACR-af9b7653f56f42bf9e7de4004d2be51e
ACR-0d158ade4d6e4bf69a0a2e55a94b9fa3
ACR-f89964b79c3545f69a1a82807e2d0e2f
ACR-60950742c98c4e03a8647e63462f21cd
ACR-4e23e783dade4006b0cac58af86ce85a
ACR-5689a5fd98ec44839fd638b62f7f2e30
ACR-3e6f684c47684f8d89c3dda1e7b553e8
ACR-52f3b846a6cc434cb322ff8338ca4217
ACR-64837fbf6a8d400c845375c558fd2b63
ACR-b7c7b12e1a0b4ee6997cd22d9ae5860a
ACR-70895a7384f242c9ae0f3d1e223ee4e9
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.commons.NewCodeDefinition;
import org.sonarsource.sonarlint.core.serverconnection.proto.Sonarlint;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.sonarsource.sonarlint.core.serverconnection.storage.NewCodeDefinitionStorage.adapt;

class NewCodeDefinitionStorageTests {

  @Test
  void shouldAdaptToProtobuf() {
    var days = adapt(NewCodeDefinition.withNumberOfDaysWithDate(30, 1000));
    assertThat(days.getDays()).isEqualTo(30);
    assertThat(days.getThresholdDate()).isEqualTo(1000);

    var previousWithVersion = adapt(NewCodeDefinition.withPreviousVersion(1000, "1.0-SNAPSHOT"));
    assertThat(previousWithVersion.getVersion()).isEqualTo("1.0-SNAPSHOT");
    assertThat(previousWithVersion.getThresholdDate()).isEqualTo(1000);

    var previousWithoutVersion = adapt(NewCodeDefinition.withPreviousVersion(1000, null));
    assertThat(previousWithoutVersion.getVersion()).isEmpty();
    assertThat(previousWithoutVersion.getThresholdDate()).isEqualTo(1000);

    var branch = adapt(NewCodeDefinition.withReferenceBranch("master"));
    assertThat(branch.getReferenceBranch()).isEqualTo("master");
  }

  @Test
  void shouldAdaptFromProtobuf() {
    var daysProto = Sonarlint.NewCodeDefinition.newBuilder()
      .setMode(Sonarlint.NewCodeDefinitionMode.NUMBER_OF_DAYS)
      .setDays(30)
      .setThresholdDate(1000)
      .build();
    var days = adapt(daysProto);
    assertThat(days.isSupported()).isTrue();
    assertThat(days).isInstanceOf(NewCodeDefinition.NewCodeNumberOfDaysWithDate.class);
    assertThat(days.getThresholdDate().toEpochMilli()).isEqualTo(1000);
    assertThat(((NewCodeDefinition.NewCodeNumberOfDaysWithDate) days).getDays()).isEqualTo(30);

    var previousWithVersionProto = Sonarlint.NewCodeDefinition.newBuilder()
      .setMode(Sonarlint.NewCodeDefinitionMode.PREVIOUS_VERSION)
      .setVersion("1.0-SNAPSHOT")
      .setThresholdDate(1000)
      .build();
    var previousWithVersion = adapt(previousWithVersionProto);
    assertThat(previousWithVersion.isSupported()).isTrue();
    assertThat(previousWithVersion).isInstanceOf(NewCodeDefinition.NewCodePreviousVersion.class);
    assertThat(previousWithVersion.getThresholdDate().toEpochMilli()).isEqualTo(1000);
    assertThat(((NewCodeDefinition.NewCodePreviousVersion) previousWithVersion).getVersion()).isEqualTo("1.0-SNAPSHOT");

    var previousWithoutVersionProto = Sonarlint.NewCodeDefinition.newBuilder()
      .setMode(Sonarlint.NewCodeDefinitionMode.PREVIOUS_VERSION)
      .setThresholdDate(1000)
      .build();
    var previousWithoutVersion = adapt(previousWithoutVersionProto);
    assertThat(previousWithoutVersion.isSupported()).isTrue();
    assertThat(previousWithoutVersion).isInstanceOf(NewCodeDefinition.NewCodePreviousVersion.class);
    assertThat(previousWithoutVersion.getThresholdDate().toEpochMilli()).isEqualTo(1000);
    assertThat(((NewCodeDefinition.NewCodePreviousVersion) previousWithoutVersion).getVersion()).isNull();

    var branchProto = Sonarlint.NewCodeDefinition.newBuilder()
      .setMode(Sonarlint.NewCodeDefinitionMode.REFERENCE_BRANCH)
      .setReferenceBranch("master")
      .build();
    var branch = adapt(branchProto);
    assertThat(branch.isSupported()).isFalse();
    assertThat(branch).isInstanceOf(NewCodeDefinition.NewCodeReferenceBranch.class);
    assertThat(((NewCodeDefinition.NewCodeReferenceBranch) branch).getBranchName()).isEqualTo("master");

    var analysisProto = Sonarlint.NewCodeDefinition.newBuilder()
      .setMode(Sonarlint.NewCodeDefinitionMode.SPECIFIC_ANALYSIS)
      .setThresholdDate(1000)
      .build();
    var analysis = adapt(analysisProto);
    assertThat(analysis.isSupported()).isTrue();
    assertThat(analysis).isInstanceOf(NewCodeDefinition.NewCodeSpecificAnalysis.class);
    assertThat(analysis.getThresholdDate().toEpochMilli()).isEqualTo(1000);

    var unknownProto = Sonarlint.NewCodeDefinition.newBuilder()
      .setMode(Sonarlint.NewCodeDefinitionMode.UNKNOWN)
      .build();
    assertThatThrownBy(() -> adapt(unknownProto)).hasMessage("Unsupported mode: UNKNOWN");
  }

}
