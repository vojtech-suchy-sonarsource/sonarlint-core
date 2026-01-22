/*
ACR-4e4f89df2ec34869b8c94af0fd4367a6
ACR-4d7b48f30668497399af949f9153ec51
ACR-1f82859da7ba4400a8f19e2f1fd72113
ACR-07ab678e29874d8e9febc6b2842ee016
ACR-adbad9df9dc54325ad0f5ff30b0883d0
ACR-5711c0c90b10491c8fa4655f7ef48a89
ACR-ca8611174f9245c792de5f43c47fa177
ACR-a6aac02e3d0e4d609fb1bb6a6553eb67
ACR-4861416e7cfb4f8280e8315e7f240e02
ACR-52df179354e84789bdd5e5cf976e6923
ACR-b8f8b98a117a4d178cd14693222ba10d
ACR-26f0a988f1294c0d8a8df0c14fb065c1
ACR-fd780939c7a74000aadc1010cb8d47dd
ACR-4827808c05234e9e9657287ad31519ac
ACR-32694066e43844268fb8a69cb0406328
ACR-0de7f6def7c14cfaba36e128f32c7c27
ACR-e8b70bac3ce64847a6e6a2467c480c76
 */
package org.sonarsource.sonarlint.core.repository.rules;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.rules.RulesExtractionHelper;
import org.sonarsource.sonarlint.core.serverconnection.ConnectionStorage;
import org.sonarsource.sonarlint.core.serverconnection.storage.ServerInfoStorage;
import org.sonarsource.sonarlint.core.storage.StorageService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class RulesRepositoryTest {

  @Test
  void it_should_not_touch_storage_after_rules_are_lazily_loaded_in_connected_mode() {
    var storageService = mock(StorageService.class);
    var rulesRepository = new RulesRepository(mock(RulesExtractionHelper.class), mock(ConfigurationRepository.class), storageService);
    var connectionStorage = mock(ConnectionStorage.class);
    when(storageService.connection("connection")).thenReturn(connectionStorage);
    var serverInfoStorage = mock(ServerInfoStorage.class);
    when(connectionStorage.serverInfo()).thenReturn(serverInfoStorage);
    when(serverInfoStorage.read()).thenReturn(Optional.empty());
    rulesRepository.getRule("connection", "rule");
    reset(storageService);

    rulesRepository.getRule("connection", "rule");

    verifyNoInteractions(storageService);
  }

}
