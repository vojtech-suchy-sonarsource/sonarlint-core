/*
ACR-908c2d56ceea47a1b3db868e3ccacaaf
ACR-0603aa53c2e64bed80f19132526c0cba
ACR-6fb268ac12a242caac42091b312ca9c7
ACR-f778c01db6094f07ab5e47d274009b0c
ACR-1d133fe6235245768f93c2622b6f3690
ACR-516b731e31aa4dc6b35ef20366f41511
ACR-7d4523e95d2b4118aee64cdf97386db9
ACR-8f33ead2cfc845b5a83cdd6bd970b7dd
ACR-3f79c274e84b456caf9ad728655000cc
ACR-1a29f7f0e8cd436895558b23465185fe
ACR-9227e9f57d274ad992f802649bba4c74
ACR-ae22ef0c1720468c946c1054d86c1330
ACR-2ab7f6b942684442aa520e295ce30b07
ACR-7f88cd95946147639a091f6babb8e158
ACR-de759388687f46689e6903a8d8d45319
ACR-11a4c50d5afb43ffab11b65e470146eb
ACR-90edd79385574d028224029f7776951e
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
