/*
ACR-ce0bb25bdefe405988a4f70659f8d321
ACR-a9eed3b07bb14bda86e5038086e4a787
ACR-3d1af15bf9d846159d04fb826a77ba0d
ACR-f8088e286fca411c85af695220d1441c
ACR-231d008a4b73434eab4e722a17a7a50f
ACR-3692cbc6df834b7289b8d10d507cfeb7
ACR-5b373ce79ca04e40ad27267b966a05d2
ACR-99a9c01e60534fae8fa127e1791a7563
ACR-4ca34d5cfd3c412aab01ff870439305b
ACR-aca4d04ba6b24418b2e4f1595f54409d
ACR-18a6865e2b6d4a1dac233b57e24f8483
ACR-8dd264591b894796880469738465a96b
ACR-cf8d266e95f1429bb6a2573ed4b0f749
ACR-c2562fed575642b794a82f12f32d1557
ACR-9149d8361a94443bb4a724c7c5ef0944
ACR-c9ea413133a64fc2a1f66f81bfa7f680
ACR-f79779b19d49439a95e2d58f4fc2dd72
 */
package org.sonarsource.sonarlint.core.newcode;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.commons.Binding;
import org.sonarsource.sonarlint.core.commons.NewCodeDefinition;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.newcode.GetNewCodeDefinitionResponse;
import org.sonarsource.sonarlint.core.serverconnection.SonarProjectStorage;
import org.sonarsource.sonarlint.core.serverconnection.storage.NewCodeDefinitionStorage;
import org.sonarsource.sonarlint.core.storage.StorageService;
import org.sonarsource.sonarlint.core.telemetry.TelemetryService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NewCodeServiceTests {

  private ConfigurationRepository mockConfigRepository;
  private StorageService mockStorageService;

  private NewCodeService underTest;

  @BeforeEach
  void setup() {
    mockConfigRepository = mock(ConfigurationRepository.class);
    mockStorageService = mock(StorageService.class);
    underTest = new NewCodeService(mockConfigRepository, mockStorageService, mock(TelemetryService.class));
  }

  @Test
  void getNewCodeDefinition_noBinding() {
    var ncd = underTest.getNewCodeDefinition("scope");
    assertThat(ncd).extracting(GetNewCodeDefinitionResponse::getDescription, GetNewCodeDefinitionResponse::isSupported)
      .containsExactly("From last 30 days", true);
  }

  @Test
  void getNewCodeDefinition_noNcdSynchronized() {
    String scopeId = "scope";
    var effectiveBinding = mock(Binding.class);
    when(mockConfigRepository.getEffectiveBinding(scopeId))
      .thenReturn(Optional.of(effectiveBinding));
    var storage = mock(SonarProjectStorage.class);
    when(mockStorageService.binding(effectiveBinding))
      .thenReturn(storage);
    var newCodeDefStorage = mock(NewCodeDefinitionStorage.class);
    when(storage.newCodeDefinition()).thenReturn(newCodeDefStorage);
    var ncd = underTest.getNewCodeDefinition("scope");
    assertThat(ncd).extracting(GetNewCodeDefinitionResponse::getDescription, GetNewCodeDefinitionResponse::isSupported)
      .containsExactly("No new code definition found", false);
  }

  @Test
  void getNewCodeDefinition_readFromStorage() {
    String scopeId = "scope";
    var effectiveBinding = mock(Binding.class);
    when(mockConfigRepository.getEffectiveBinding(scopeId))
      .thenReturn(Optional.of(effectiveBinding));
    var storage = mock(SonarProjectStorage.class);
    when(mockStorageService.binding(effectiveBinding))
      .thenReturn(storage);
    var newCodeDefStorage = mock(NewCodeDefinitionStorage.class);
    when(storage.newCodeDefinition()).thenReturn(newCodeDefStorage);
    var newCodeDefinition = NewCodeDefinition.withNumberOfDaysWithDate(42, 1234567890123L);
    when(newCodeDefStorage.read()).thenReturn(Optional.of(newCodeDefinition));
    var ncd = underTest.getNewCodeDefinition("scope");
    assertThat(ncd).extracting(GetNewCodeDefinitionResponse::getDescription, GetNewCodeDefinitionResponse::isSupported)
      .containsExactly("From last 42 days", true);
  }
}
