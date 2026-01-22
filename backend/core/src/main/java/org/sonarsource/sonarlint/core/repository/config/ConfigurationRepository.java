/*
ACR-38f54027bd1a4b4eba0811c9ef17102f
ACR-ff871a4fe7a14f84a9c7462329f59e8f
ACR-3006392a7a23419091a21a401ad78b40
ACR-f99b5695d1534b75ac63bf0e36086fa4
ACR-f4b701a193f546ae959f08a2ea73454a
ACR-9cf5f18dd9c44229affbf392673aa9c7
ACR-fba3d6c415e84685929a706a9dcb3696
ACR-b3d35258568f4529ba3bb73fc00a8dde
ACR-26d96b360847438d9dee8f7682ea50f1
ACR-d98315c8974e4832b2410eb456088c37
ACR-ee7a47797bee462da3b26978e2205531
ACR-e3823bf520cd4bef81263f4c913f5d0e
ACR-28b7861bbc374fd480b12bde516d6d95
ACR-915b4a075de54625aa5fea33a241493f
ACR-079e4e93df8446e9b5e14b3d47b74ada
ACR-9cc1727d71154fedbf40ec9c84e58c04
ACR-6c21c22467444f7193b92b522eefddc7
 */
package org.sonarsource.sonarlint.core.repository.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.annotation.CheckForNull;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseError;
import org.sonarsource.sonarlint.core.commons.Binding;
import org.sonarsource.sonarlint.core.commons.BoundScope;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcErrorCode;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

public class ConfigurationRepository {

  private final Map<String, ConfigurationScope> configScopeById = new ConcurrentHashMap<>();
  private final Map<String, BindingConfiguration> bindingByConfigScopeId = new ConcurrentHashMap<>();

  public ConfigurationScope addOrReplace(ConfigurationScope configScope, BindingConfiguration bindingConfig) {
    var id = configScope.id();
    var previous = configScopeById.put(id, configScope);
    bindingByConfigScopeId.put(id, bindingConfig);
    return previous;
  }

  @CheckForNull
  public ConfigurationScopeWithBinding remove(String idToRemove) {
    var removedScope = configScopeById.remove(idToRemove);
    var removeBindingConfiguration = bindingByConfigScopeId.remove(idToRemove);
    return removedScope == null ? null : new ConfigurationScopeWithBinding(removedScope, removeBindingConfiguration);
  }

  public Map<String, BindingConfiguration> removeBindingForConnection(String connectionId) {
    var removedBindingByConfigScope = new HashMap<String, BindingConfiguration>();
    var configScopeIdsToUnbind = bindingByConfigScopeId.entrySet().stream().filter(e -> connectionId.equals(e.getValue().connectionId())).map(Map.Entry::getKey).collect(toSet());
    configScopeIdsToUnbind.forEach(configScopeId -> {
      var removedBindingConfiguration = bindingByConfigScopeId.get(configScopeId);
      if (removedBindingConfiguration != null) {
        var noBinding = BindingConfiguration.noBinding(removedBindingConfiguration.bindingSuggestionDisabled());
        updateBinding(configScopeId, noBinding);
        removedBindingByConfigScope.put(configScopeId, removedBindingConfiguration);
      }
    });
    return removedBindingByConfigScope;
  }

  public void updateBinding(String configScopeId, BindingConfiguration bindingConfig) {
    bindingByConfigScopeId.put(configScopeId, bindingConfig);
  }

  public Set<String> getConfigScopeIds() {
    return Set.copyOf(configScopeById.keySet());
  }

  @CheckForNull
  public BindingConfiguration getBindingConfiguration(String configScopeId) {
    return bindingByConfigScopeId.get(configScopeId);
  }

  public Optional<Binding> getEffectiveBinding(String configScopeId) {
    var configScopeIdToSearchIn = requireNonNull(configScopeId, "Configuration Scope ID is mandatory");
    while (true) {
      var binding = getConfiguredBinding(configScopeIdToSearchIn);
      if (binding.isPresent()) {
        return binding;
      }
      var parentId = getParentId(configScopeIdToSearchIn);
      if (parentId.isEmpty()) {
        return Optional.empty();
      }
      configScopeIdToSearchIn = parentId.get();
    }
  }

  public Binding getEffectiveBindingOrThrow(String configScopeId) {
    return getEffectiveBinding(configScopeId).orElseThrow(() -> {
      var error = new ResponseError(SonarLintRpcErrorCode.CONFIG_SCOPE_NOT_BOUND, "No binding for config scope '" + configScopeId + "'", configScopeId);
      return new ResponseErrorException(error);
    });
  }

  public Optional<Binding> getConfiguredBinding(String configScopeId) {
    var bindingConfiguration = bindingByConfigScopeId.get(configScopeId);
    if (bindingConfiguration != null && bindingConfiguration.isBound()) {
      return Optional.of(new Binding(requireNonNull(bindingConfiguration.connectionId()),
        requireNonNull(bindingConfiguration.sonarProjectKey())));
    }
    return Optional.empty();
  }

  private Optional<String> getParentId(String configScopeId) {
    var configurationScope = configScopeById.get(configScopeId);
    if (configurationScope != null) {
      return Optional.ofNullable(configurationScope.parentId());
    }
    return Optional.empty();
  }

  public Set<String> getLeafConfigScopeIds() {
    var leafConfigScopeIds = new HashSet<>(configScopeById.keySet());
    configScopeById.values().forEach(scope -> {
      var parentId = scope.parentId();
      if (parentId != null) {
        leafConfigScopeIds.remove(parentId);
      }
    });
    return leafConfigScopeIds;
  }

  public boolean isLeafConfigScope(String configScopeId) {
    return getLeafConfigScopeIds().contains(configScopeId);
  }

  @CheckForNull
  public ConfigurationScope getConfigurationScope(String configScopeId) {
    return configScopeById.get(configScopeId);
  }

  public Collection<BoundScope> getAllBoundScopes() {
    return configScopeById.keySet()
      .stream()
      .map(scopeId -> {
        var effectiveBinding = getEffectiveBinding(scopeId);
        return effectiveBinding.map(binding -> new BoundScope(scopeId, binding)).orElse(null);
      })
      .filter(Objects::nonNull)
      .toList();
  }

  public Collection<ConfigurationScope> getAllBindableUnboundScopes() {
    return configScopeById.entrySet()
      .stream()
      .filter(e -> e.getValue().bindable())
      .filter(e -> getEffectiveBinding(e.getKey()).isEmpty())
      .map(Map.Entry::getValue)
      .toList();
  }

  @CheckForNull
  public BoundScope getBoundScope(String configScopeId) {
    var effectiveBinding = getEffectiveBinding(configScopeId);
    return effectiveBinding.map(binding -> new BoundScope(configScopeId, binding)).orElse(null);
  }

  public Collection<BoundScope> getBoundScopesToConnectionAndSonarProject(String connectionId, String projectKey) {
    return getBoundScopesToConnection(connectionId)
      .stream()
      .filter(b -> projectKey.equals(b.getSonarProjectKey()))
      .toList();
  }

  public Collection<BoundScope> getBoundScopesToConnection(String connectionId) {
    return getAllBoundScopes()
      .stream()
      .filter(b -> connectionId.equals(b.getConnectionId()))
      .toList();
  }

  /*ACR-b5343875dbbd4bc79ed5d412f8df3b8c
ACR-5d589a48697c457bb8b5d4e2145b91c5
   */
  public Set<String> getSonarProjectsUsedForConnection(String connectionId) {
    return getAllBoundScopes()
      .stream()
      .filter(b -> connectionId.equals(b.getConnectionId()))
      .map(BoundScope::getSonarProjectKey)
      .collect(toSet());
  }

  public Map<String, Map<String, Collection<BoundScope>>> getBoundScopeByConnectionAndSonarProject() {
    return getAllBoundScopes()
      .stream()
      .collect(groupingBy(BoundScope::getConnectionId, groupingBy(BoundScope::getSonarProjectKey, Collectors.toCollection(ArrayList::new))));
  }

  public List<String> getChildrenWithInheritedBinding(String parentId) {
    return configScopeById.values().stream()
      .filter(scope -> parentId.equals(scope.parentId()) && (!bindingByConfigScopeId.containsKey(scope.id()) || !bindingByConfigScopeId.get(scope.id()).isBound()))
      .map(ConfigurationScope::id)
      .toList();
  }
}
