/*
ACR-a67cf9beda3b4f6c9ccbef4b442383fe
ACR-d98443c629bc41c2a33bed59353e9aab
ACR-35430144b52943d39d0366dbd00f81c3
ACR-2fab7b3f31bb494aad9d36fbb227a7f1
ACR-4686a4c2124146df8777228c18d4d028
ACR-c83e78cb7c674811a989cf091e3b8182
ACR-c28f47cc7d0643a5af4e4f04e97b2430
ACR-5f4cb77488f24c4f9544e4fa26c56ab2
ACR-02e20fd0e5c14accbaef2579c76c1a82
ACR-544431dbfa88439fbc0997365f9932e2
ACR-9eaadbb1a34f48759a5f3ad03c3c15c0
ACR-960b1771dba94cf4a12710ab73a13a25
ACR-36f5c431c54542e59f61ea4d3558b09d
ACR-cd30aed4984b4af19348c760bdba81d4
ACR-ad593b41c3214cf7bae61910205d3907
ACR-a584b7b38a7642f3bcb8c300c97c4972
ACR-1300c2e728fc48aca16fc226634455fe
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
