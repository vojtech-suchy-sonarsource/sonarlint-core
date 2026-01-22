/*
ACR-ffdeb6ffbdbe43ceaa47b9a75e8d2eee
ACR-b0404242dec3437482979d1e00c0dee0
ACR-b843078026ca42028b3dcebc51e41805
ACR-5d5e3cbcda88447b9f965917aa7d6128
ACR-0930d7bcd24f458981ac135bc173bd06
ACR-519b91b4564a4b5ba03fe27ee831fb5e
ACR-0b2a183d0b0d4c6185a4e6f51887c770
ACR-b15ef49520db4a76abbc73cb51fa9ed4
ACR-d23aeca1ada94ab1aeb937896e926337
ACR-124385840fd34b92821e5e2db3258e82
ACR-ec244ddf12a7416dadda7349d3e13563
ACR-9f44ab645cd34737986e0e1188b6bf1b
ACR-e69ed15298714026baeaf84a53d20b40
ACR-450f1c5f090243ebaf41f1822ac2a314
ACR-42e4212c3a4945e192cc507a2f12f006
ACR-e1908226204c4e70a91fde9735512a1c
ACR-af3bf6a684804f85b21e028a2055776a
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.tracking;

public class ListAllParams {
  private final String configurationScopeId;
  private final boolean shouldRefresh;

  public ListAllParams(String configurationScopeId) {
    this(configurationScopeId, false);
  }

  public ListAllParams(String configurationScopeId, boolean shouldRefresh) {
    this.configurationScopeId = configurationScopeId;
    this.shouldRefresh = shouldRefresh;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public boolean shouldRefresh() {
    return shouldRefresh;
  }
}
