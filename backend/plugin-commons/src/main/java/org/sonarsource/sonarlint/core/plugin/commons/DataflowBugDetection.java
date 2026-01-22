/*
ACR-14b11400ca40477e9e9a1fe9aed0898a
ACR-99c6591545de44378a591db9e9784807
ACR-8787c590f07d449980c18e7b35eac207
ACR-575cd005489b4f549a8bfc5e8b5c501c
ACR-24b5a3ec62d1453caad7f9a7169330e6
ACR-7a09685ab00540eb94812f5b0b5f7c64
ACR-1109e31b2da64ff4b5192dcc2145544f
ACR-79f24128ef19465090c2c8c1f6a68c38
ACR-ce1bebbfb2a14190a1006c1a42021946
ACR-335e5ed5a9fd41118edb7fc7ae96372d
ACR-8cbf41b5d4484ad0a4ede00996dfe5f5
ACR-9fd32274bf3f4b13be77dc70c4be7a01
ACR-c7e5cbc5348b4b71be03cf752742c9fb
ACR-dac32756c67843629876d93dc4d3c88a
ACR-9abd859d8d5b4354836487fe164d7043
ACR-c7183c548dc4407185d338f3ce810e92
ACR-33be5810cc894c859fa620dc4b15e560
 */
package org.sonarsource.sonarlint.core.plugin.commons;

import java.util.Set;

public class DataflowBugDetection {

  private DataflowBugDetection() {
    //ACR-5688f1dc918d40d58d8c9831c12c874c
  }

  public static final Set<String> PLUGIN_ALLOW_LIST = Set.of("dbd", "dbdpythonfrontend", "dbdjavafrontend");

  static Set<String> getPluginAllowList(boolean isDataflowBugDetectionEnabled) {
    return isDataflowBugDetectionEnabled ? PLUGIN_ALLOW_LIST : Set.of();
  }
}
