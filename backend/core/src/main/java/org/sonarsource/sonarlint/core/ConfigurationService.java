/*
ACR-0f5c85af56cc49c8a666ae4b113cc536
ACR-ea7b2876d7d74e03b11b699c0b9cd271
ACR-8e05914ffdfb408091d59be270b8c0af
ACR-388fefb025e14dadbdd7ee013991970c
ACR-028293dafbf4466bacc283690b3bafb6
ACR-d4e0891f85ed44b4a30e2edf8b079551
ACR-5d96a7552a3e4b6cb062de5b9c505b6a
ACR-8f7f625064f04587ab54b449543cbeb3
ACR-897f84b5677c47219bc8fd0a0460b194
ACR-067f0c6237b9493e91dcccfef2a9e4de
ACR-5f794f51839644caa427ee007723a5bd
ACR-305e591c07544aa4bd440b6554c57ca9
ACR-68e81f459f5344aebbd4e71184eb4d22
ACR-2499f8f583294fb2a351b0b437204146
ACR-26d4982346aa4b7fa8e94f9365850fc1
ACR-4c97fa9d779a40d8bc724c73055e3109
ACR-b9f7e7229b3540b0a72ebc78f877f372
 */
package org.sonarsource.sonarlint.core;

import java.util.HashSet;
import java.util.List;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.event.BindingConfigChangedEvent;
import org.sonarsource.sonarlint.core.event.ConfigurationScopeRemovedEvent;
import org.sonarsource.sonarlint.core.event.ConfigurationScopesAddedWithBindingEvent;
import org.sonarsource.sonarlint.core.event.ConnectionConfigurationRemovedEvent;
import org.sonarsource.sonarlint.core.repository.config.BindingConfiguration;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationRepository;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationScope;
import org.sonarsource.sonarlint.core.repository.config.ConfigurationScopeWithBinding;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingConfigurationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope.ConfigurationScopeDto;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

public class ConfigurationService {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final ApplicationEventPublisher applicationEventPublisher;
  private final ConfigurationRepository repository;

  public ConfigurationService(ApplicationEventPublisher applicationEventPublisher, ConfigurationRepository repository) {
    this.applicationEventPublisher = applicationEventPublisher;
    this.repository = repository;
  }

  public void didAddConfigurationScopes(List<ConfigurationScopeDto> addedScopes) {
    var addedIds = new HashSet<ConfigurationScopeWithBinding>();
    for (var addedDto : addedScopes) {
      var configScopeInReferential = adapt(addedDto);
      var bindingDto = addedDto.getBinding();
      var bindingConfigInReferential = adapt(bindingDto);
      var previous = repository.addOrReplace(configScopeInReferential, bindingConfigInReferential);
      if (previous != null) {
        LOG.error("Duplicate configuration scope registered: {}", addedDto.getId());
      } else {
        LOG.debug("Added configuration scope '{}'", configScopeInReferential.id());
        addedIds.add(new ConfigurationScopeWithBinding(configScopeInReferential, bindingConfigInReferential));
      }
    }
    if (!addedIds.isEmpty()) {
      applicationEventPublisher.publishEvent(new ConfigurationScopesAddedWithBindingEvent(addedIds));
    }
  }

  private static BindingConfiguration adapt(@Nullable BindingConfigurationDto dto) {
    if (dto == null) {
      return BindingConfiguration.noBinding();
    }
    return new BindingConfiguration(dto.getConnectionId(), dto.getSonarProjectKey(), dto.isBindingSuggestionDisabled());
  }

  private static ConfigurationScope adapt(ConfigurationScopeDto dto) {
    return new ConfigurationScope(dto.getId(), dto.getParentId(), dto.isBindable(), dto.getName());
  }

  public void didRemoveConfigurationScope(String removedId) {
    var removed = repository.remove(removedId);
    if (removed == null) {
      LOG.debug("Attempt to remove configuration scope '{}' that was not registered", removedId);
    } else {
      LOG.debug("Removed configuration scope '{}'", removedId);
      applicationEventPublisher.publishEvent(new ConfigurationScopeRemovedEvent(removed.scope(), removed.bindingConfiguration()));
    }
  }

  public void didUpdateBinding(String configScopeId, BindingConfigurationDto updatedBinding) {
    LOG.debug("Did update binding for configuration scope '{}', new binding: '{}'", configScopeId, updatedBinding);
    var boundEvent = bind(configScopeId, updatedBinding);
    if (boundEvent != null) {
      applicationEventPublisher.publishEvent(boundEvent);
    }
  }

  @EventListener
  public void connectionRemoved(ConnectionConfigurationRemovedEvent event) {
    var bindingConfigurationByConfigScope = repository.removeBindingForConnection(event.getRemovedConnectionId());
    bindingConfigurationByConfigScope.forEach((configScope, bindingConfiguration) ->
      applicationEventPublisher.publishEvent(new BindingConfigChangedEvent(configScope, bindingConfiguration,
        BindingConfiguration.noBinding(bindingConfiguration.bindingSuggestionDisabled()))));
  }

  @CheckForNull
  private BindingConfigChangedEvent bind(String configurationScopeId, BindingConfigurationDto bindingConfiguration) {
    var previousBindingConfig = repository.getBindingConfiguration(configurationScopeId);
    if (previousBindingConfig == null) {
      LOG.error("Attempt to update binding in configuration scope '{}' that was not registered", configurationScopeId);
      return null;
    }
    var newBindingConfig = adapt(bindingConfiguration);
    repository.updateBinding(configurationScopeId, newBindingConfig);

    return createChangedEventIfNeeded(configurationScopeId, previousBindingConfig, newBindingConfig);
  }

  @CheckForNull
  private static BindingConfigChangedEvent createChangedEventIfNeeded(String configScopeId, BindingConfiguration previousBindingConfig,
    BindingConfiguration newBindingConfig) {
    if (!previousBindingConfig.equals(newBindingConfig)) {
      return new BindingConfigChangedEvent(configScopeId, previousBindingConfig, newBindingConfig);
    }
    return null;
  }

}
