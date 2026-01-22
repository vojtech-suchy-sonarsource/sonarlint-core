/*
ACR-322ef4b80145490495809c9e09d6a537
ACR-bc2b8e0c78ef4f50a8c736988304bcc6
ACR-f7d4c102a68e43299ec9db20174ec800
ACR-9ef22e7686ff4394adae91e04cef8c55
ACR-4eb2fe70e0674c09811db6e65b93e7f6
ACR-e2f5ed28e2a24b3da2a086abf1a05dfa
ACR-de1d3649eec842ffb9aaeeb2bdfe7edb
ACR-833e382bfa974721b509681feaf5abe3
ACR-e3fa3ff3da6b483c91dbce487a6a15fa
ACR-a7a186d9e56241909c84128a178d6896
ACR-2719414f665a4beaad0a453599bea5d1
ACR-0c4bbeb2519d4bd49ff9ee6a12f07823
ACR-321cb008a5cc4f399fe26c42ce257f74
ACR-647f7b5b4fa1421398baeb22c411e8ab
ACR-004b9ca6c53c459e9986f38c4fa3fe7e
ACR-dc6a3860ee204a42aca9645bf02741c4
ACR-b985aae50d154e0aab5103131e87af80
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
