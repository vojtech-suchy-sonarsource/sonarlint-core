/*
ACR-44fa8f633b3642f8bc62bd1f60222b62
ACR-e2c9ae70f4dc484ba5d7ed36a266dc59
ACR-6e83b14aa9a04ec08fa2fcdbe8cdb1df
ACR-665428e0929946e9ab1578b5c776eb9a
ACR-70b4c067abcc4fe1a44dde6eaf530bb9
ACR-87f1ba4915404ed89724b7bf68dc79e4
ACR-571437355a254b9b9f10a64d92f930c9
ACR-c19c907f2cd34c0c9335da525fb745bb
ACR-7c8be38db4f24ad2a4b7ac899264f007
ACR-26e1edd2d3c14add99e770df3d3fd310
ACR-558328a2b68348b5aa1ce70fe684ce79
ACR-c454d4ea414f406b8e9b5bf2c1785f69
ACR-6ae47f0b99ad4c5d80551435685c8f36
ACR-b7bada9645b24e7a8fc142a199e7da64
ACR-4af4b7e0ded64c8ca396617096444aba
ACR-05ed882d13f64139907c8a0f80f40727
ACR-24f55ae0b3604761bbe39596aade8b6f
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.config.scope;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingConfigurationDto;

public class ConfigurationScopeDto {

  private final String id;
  private final String parentId;
  private final boolean bindable;
  /*ACR-5fafea3f024e464e99304b9d5f43fd73
ACR-42eed2b5c4bc4ba7aeda0a65f9d0ceac
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
