/*
ACR-83df5b59db624afbb391e8666ce818c5
ACR-d2ec68abaf11427fa3adacf8e8458286
ACR-935cc4b5074948d18aa3678e28e6b2d6
ACR-0ebefb38eb22474ba1e009a12e299b4b
ACR-6b36444b21d0483a9b16b2502745b052
ACR-207023a854c145fd89894c607816222c
ACR-cc9dcdd7216541ce844751f38e7d9990
ACR-40d5de15c1e94a89bc690c513cabf9d5
ACR-970787f1a2304e999ea961cc87ba7f06
ACR-04876ce2da684002a193c6caf4171123
ACR-b3eae678f4414066a4a167826ef7c19f
ACR-db78a3e3b74944c395360be45bf738cb
ACR-82d15cb55e1840d0a10d230ab56b225f
ACR-f385791acc104bcbba62731537d90187
ACR-403ee76cb76749dc9a7cd78f8d0d6b3c
ACR-3e3304c118324b07a19ccc6ab815f4ba
ACR-1c8b07fcc2cb4356a5169ec71d7643e0
 */
package org.sonarsource.sonarlint.core.plugin.commons;

import java.util.Set;

public class DataflowBugDetection {

  private DataflowBugDetection() {
    //ACR-1e14de0d0c934810badb9d29e5fd3d06
  }

  public static final Set<String> PLUGIN_ALLOW_LIST = Set.of("dbd", "dbdpythonfrontend", "dbdjavafrontend");

  static Set<String> getPluginAllowList(boolean isDataflowBugDetectionEnabled) {
    return isDataflowBugDetectionEnabled ? PLUGIN_ALLOW_LIST : Set.of();
  }
}
