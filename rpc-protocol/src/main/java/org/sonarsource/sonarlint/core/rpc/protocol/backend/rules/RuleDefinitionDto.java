/*
ACR-69db9debb1c84fd48b6c78292c64eedd
ACR-ec62f468377547aab9ae502d07b93a93
ACR-9206d65fbc614306b85ef5d598ca9338
ACR-d348336dcc4547c5a5d042ede31034fb
ACR-b7b54b4da3e142b0a5c96be0f772870f
ACR-ec7a16ed02524f4f922eb5995835c2cb
ACR-4d69d7858f60430c99edf3a2377e4970
ACR-5cc39ad8457e4c1488da242e77ecf5c6
ACR-d405d6908f5644d593a33cdd6a5471a8
ACR-5a533b731dfb4ac881498259f1c01d61
ACR-7df2f9c8bbbf4581b30a5020327bb9ba
ACR-542f7d29524341b0bad6d3cd1a75b50d
ACR-c4acf0d9cfa044649d24113c2ba76483
ACR-106696866d5b4e35b9c56bccbfc46cf5
ACR-9a245bb9a0974dfcb4205fcd4e2ef58e
ACR-fc41b6b7f7ac43008e0618edf275be24
ACR-4d82b5988694442c91d58a5181a027ac
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.rules;

import java.util.List;
import java.util.Map;
import org.sonarsource.sonarlint.core.rpc.protocol.common.CleanCodeAttribute;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;

public class RuleDefinitionDto {
  private final String key;
  private final String name;
  private final CleanCodeAttribute cleanCodeAttribute;
  private final List<ImpactDto> softwareImpacts;
  private final Language language;
  private final Map<String, RuleParamDefinitionDto> paramsByKey;
  private final boolean isActiveByDefault;

  public RuleDefinitionDto(String key, String name, CleanCodeAttribute cleanCodeAttribute, List<ImpactDto> softwareImpacts,
    Map<String, RuleParamDefinitionDto> paramsByKey, boolean isActiveByDefault,
    Language language) {
    this.key = key;
    this.name = name;
    this.cleanCodeAttribute = cleanCodeAttribute;
    this.softwareImpacts = softwareImpacts;
    this.language = language;
    this.paramsByKey = paramsByKey;
    this.isActiveByDefault = isActiveByDefault;
  }

  public Map<String, RuleParamDefinitionDto> getParamsByKey() {
    return paramsByKey;
  }

  public boolean isActiveByDefault() {
    return isActiveByDefault;
  }

  public String getKey() {
    return key;
  }

  public String getName() {
    return name;
  }

  public CleanCodeAttribute getCleanCodeAttribute() {
    return cleanCodeAttribute;
  }

  public List<ImpactDto> getSoftwareImpacts() {
    return softwareImpacts;
  }

  public Language getLanguage() {
    return language;
  }
}
