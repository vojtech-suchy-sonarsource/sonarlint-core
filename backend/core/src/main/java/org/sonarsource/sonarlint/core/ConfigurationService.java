/*
ACR-169a72044a4549a9abcead3e12422435
ACR-974c50d76c5b4e07b2d44bedc16d8dc3
ACR-244620155cde41cfb5dd538dfc4da2a0
ACR-7afd93fc51454bb492ac04356c0cc313
ACR-fc703ea6dcde492d838d80b9e9016719
ACR-7d07c8e5846e44a784b6407100aa1153
ACR-811914d8c0614e41a5fdeaf96e3aaa56
ACR-7d86ed5295b24ed0bf1cdab2a55f328c
ACR-8c7ae3d8b45441caa5548ea049b64404
ACR-234013be928c49ec9f99e6ddc8016380
ACR-d112612352914759a296aab2d033f234
ACR-464204ae0e3240d090e9fcbb00093580
ACR-72ede6022cc54482b341bc373fa43951
ACR-2fba68b2d08942059bba2ef63c915d69
ACR-f1783e89dec4459cbebc9f32bad6713a
ACR-ac5ed71487e44b9ca6328c5c5e72aacd
ACR-eae8da86e0e24424ac693d28cd3cc4fb
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
