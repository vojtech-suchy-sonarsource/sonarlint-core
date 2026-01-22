/*
ACR-7b953a45eeee4c578c37e2dc3e6aade8
ACR-3d81b9e5c5e4457bb79a9f3e5145c44a
ACR-b4026ddbaa334f528ea4be1e8dce81fc
ACR-00e4ea3e68f24974b2b3c1a005a44238
ACR-b13e7cebbabd422abc0499fd859fbcb3
ACR-396401fb04e743a5befe4b6612f0d32a
ACR-3c80f0cf3268431aa764d7115105d27e
ACR-3e6680810b1c40568c0bef796a8cbbce
ACR-a74b8845329a425a9a60ce529964843b
ACR-fb500fe0b45f4a03bb366571b889f312
ACR-c0af73e5098443ca819c265edb1ed2fe
ACR-c0a00f8c01dc4f2db592f5e1b8cfd6c2
ACR-32b98ebf3e6d46868741fb56ce4bd697
ACR-a4a93fc999144af5b4de28cdc9defc18
ACR-540756f160e44588a9716a62ceeaab92
ACR-91811ba6c7b4445199c455990a39cc2c
ACR-c3e65cf50ee44b4b8b491ed505940dac
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingConfigurationDto;

public class ConfigurationScopeDto {

  private final String id;
  private final String parentId;
  private final boolean bindable;
  /*ACR-9c28cf5b45e44765a0b04ff8118e1a31
ACR-40af31a31a08431dafd3649f82b67d85
   */
  private final String name;
  private final BindingConfigurationDto binding;

  public ConfigurationScopeDto(String id, @Nullable String parentId, boolean bindable, String name, @Nullable BindingConfigurationDto binding) {
    this.id = id;
    this.parentId = parentId;
    this.bindable = bindable;
    this.name = name;
    this.binding = binding;
  }

  public String getId() {
    return id;
  }

  @CheckForNull
  public String getParentId() {
    return parentId;
  }

  public boolean isBindable() {
    return bindable;
  }

  public String getName() {
    return name;
  }

  @CheckForNull
  public BindingConfigurationDto getBinding() {
    return binding;
  }
}
