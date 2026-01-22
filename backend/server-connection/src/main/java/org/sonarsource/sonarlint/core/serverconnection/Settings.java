/*
ACR-2684f3b4f087441ca72500fb5a61fa48
ACR-2d307a2d94d84b139d276a2778231c51
ACR-c31ce52efdaf42e2aeafe505f9f0a899
ACR-ae7c8d79b2034d94a2010b7196f33394
ACR-3425fc1ba6f04aeaaefa6736f64fb5cc
ACR-7ac3e8b35fa64fb38725f26178654ad7
ACR-8c006775f1294f7c80b8600f26337d05
ACR-dd953681c008403e8132b9a519037622
ACR-8f5ab6ed5a22422c964706dc62659835
ACR-3096709421004f4798c32aecf4963b8a
ACR-8e9f53a171374712916d37a4de2ef09a
ACR-11937c8108d345a8ae23e704468c552c
ACR-6fe9dbc02a954ec38dfdcb6d15e3bd0e
ACR-f47bce4a127a494f98ee1e21b7d6425d
ACR-29f471c360c142bfa6172ec2e26cbae4
ACR-beae917e093f48af86b15eb4aba1824b
ACR-0cc4ce89cbac4fe79174901eff7facb1
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.util.Map;

public class Settings {
  private final Map<String, String> settings;

  public Settings(Map<String, String> settings) {
    this.settings = settings;
  }

  public Map<String, String> getAll() {
    return settings;
  }
}
