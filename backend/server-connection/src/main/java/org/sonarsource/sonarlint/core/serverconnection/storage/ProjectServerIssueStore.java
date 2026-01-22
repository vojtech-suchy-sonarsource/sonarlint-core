/*
ACR-ba22f3ff1f7f46d6a29efcc8959492df
ACR-20f675b64dbd43a29167f2a26f0b43c1
ACR-f6335951a40449ccb0e227a23ef34d91
ACR-d7559654e947459e83651d413ac00739
ACR-a31eb3708d4b4f16a4210d6de4142852
ACR-22d8327fc931462887c5b39e42201d06
ACR-bc508355e2e94ff7b2c6af633d4a8ecb
ACR-ce6976d24ff240c98a8becc9e947cc08
ACR-6f8517a7ccb54197ae9730fbed538580
ACR-8b710a1654224362821e60d428de3be3
ACR-a8549e6fdc8b4e5893cbf6e1617fbfd9
ACR-7517a209dee84dd3ba8fd7c3c0b750b3
ACR-c1d083569a184f969127ab09adfcb0a7
ACR-93fd2c65d69140a392550512b99f2fb7
ACR-8b45fdbf15f048bdb07bdf5602ef0da5
ACR-49f03f0aa1fc499780c0ecf402eb05ca
ACR-42a8f124cd3d4fddaafec8fa655a4919
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import org.sonarsource.sonarlint.core.commons.HotspotReviewStatus;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.serverapi.hotspot.ServerHotspot;
import org.sonarsource.sonarlint.core.serverconnection.issues.ServerDependencyRisk;
import org.sonarsource.sonarlint.core.serverconnection.issues.ServerFinding;
import org.sonarsource.sonarlint.core.serverconnection.issues.ServerIssue;
import org.sonarsource.sonarlint.core.serverconnection.issues.ServerTaintIssue;

public interface ProjectServerIssueStore {
  boolean wasEverUpdated();

  /*ACR-c9563c00cbd4449da0086bb050f37c45
ACR-6fba57420d38422fb8b49c6c32c16c11
   */
  void replaceAllIssuesOfBranch(String branchName, List<ServerIssue<?>> issues, Set<SonarLanguage> enabledLanguages);

  void replaceAllHotspotsOfBranch(String branchName, Collection<ServerHotspot> serverHotspots, Set<SonarLanguage> enabledLanguages);

  void replaceAllHotspotsOfFile(String branchName, Path serverFilePath, Collection<ServerHotspot> serverHotspots);

  /*ACR-6ed3b438550e46b4b046d13f05900ee2
ACR-876b3a74f7da4daf813bfc39cbd27af2
ACR-f106f491fdf94972b4c0fcf34e83ff76
   */
  boolean changeHotspotStatus(String hotspotKey, HotspotReviewStatus newStatus);

  /*ACR-1df7e12f0ff64fceb0df484d4b795e78
ACR-5b135b15182a41a28dc2c154c0e15b3a
   */
  void replaceAllIssuesOfFile(String branchName, Path serverFilePath, List<ServerIssue<?>> issues);

  /*ACR-01cb327530e444e699827c001c9b8dee
ACR-0a97e4cab2e84060895a36318dff03ff
ACR-c4449b8601434489816aa3085467b59e
ACR-6ed51e814b2146b481a0529a3263c7f9
ACR-e5fc380027df4a98a318415656f0f0d4
   */
  void mergeIssues(String branchName, List<ServerIssue<?>> issuesToMerge, Set<String> closedIssueKeysToDelete, Instant syncTimestamp, Set<SonarLanguage> enabledLanguages);

  /*ACR-154130d0a524488a9d3c048049d143e4
ACR-ae9a29143c074c8f9f1a687af5a03cc1
ACR-8cdb1530c5214d91a653889ac42459a9
ACR-bf82a2096e7b483b8c783f04ef45d686
ACR-9c07b349b3c54e55ad0745fb6892e401
   */
  void mergeTaintIssues(String branchName, List<ServerTaintIssue> issuesToMerge, Set<String> closedIssueKeysToDelete, Instant syncTimestamp, Set<SonarLanguage> enabledLanguages);

  /*ACR-b2321d1e950d43e7bcd8d269d8baa296
ACR-246ab61b84644d8f8d25cb455c52baa4
ACR-d1f122186a3944beab8151cf4e4520d3
ACR-dbc054a4fae14eda8d792b1017dd4124
ACR-edbe84a794f94a1d9c9f94bc35e57209
   */
  void mergeHotspots(String branchName, List<ServerHotspot> hotspotsToMerge, Set<String> closedHotspotKeysToDelete, Instant syncTimestamp, Set<SonarLanguage> enabledLanguages);

  /*ACR-b176f9e7911f436a88a6fa0a257c8bba
ACR-ed53f406ec3146a69b2649874d6626c9
ACR-a2a567dadcb44ee5aca8c982dc64f8b6
   */
  Optional<Instant> getLastIssueSyncTimestamp(String branchName);

  /*ACR-b83016f46b8d48fbb133efbe7835e8ec
ACR-93f0d05df05b43a5b1a27218b0d36b14
ACR-6711764d3294402a8c52ee993c51a6a7
ACR-8c1734f525c44d04a7d872d05cd5d460
   */
  Set<SonarLanguage> getLastIssueEnabledLanguages(String branchName);

  /*ACR-eb038204d3b3482f831a263aa1d3b5f3
ACR-671a62deb1d24acc805210ed19750230
ACR-3f6f50486fca45769ea7858f533d17fc
   */
  Set<SonarLanguage> getLastTaintEnabledLanguages(String branchName);

  /*ACR-f6ff1afb810f4c10a0689e2342aeb71c
ACR-e77f738527294621acc246a82175db11
ACR-2a1aad6e86e74d389f9fb1eadac9b621
   */
  Set<SonarLanguage> getLastHotspotEnabledLanguages(String branchName);

  /*ACR-f06af31fe4184c3e9fbbc4a457311b1d
ACR-8cab27cd3101418fa0d55dc4999b863f
ACR-7ea008e463424080b6c82e2f605ea1b7
   */
  Optional<Instant> getLastTaintSyncTimestamp(String branchName);

  /*ACR-92528c070f9a447c9551141025f4c1d9
ACR-11869426673d4ad3b451af4023671c4e
ACR-2d7021ab923a4e87b2ed1eccf7d506d4
   */
  Optional<Instant> getLastHotspotSyncTimestamp(String branchName);

  /*ACR-c7a86499dec1439290f153487f09e853
ACR-bc911404b9b44850941a00f5a40d94a3
ACR-7ba8f873d335472995b4a272d6015c1a
ACR-cf704ff44a3549439b4ec13f1bfc367d
ACR-63519955011b4b6e944c5b222312b576
ACR-b2e93f3dfb624511bbcdfeca5b0e34f6
   */
  List<ServerIssue<?>> load(String branchName, Path sqFilePath);

  /*ACR-c75196a6be2d4abbb1f99d2e9e788bca
ACR-79d87a4099b94c659378025a20f36563
   */
  void replaceAllTaintsOfBranch(String branchName, List<ServerTaintIssue> taintIssues, Set<SonarLanguage> enabledLanguages);

  /*ACR-be3427e0b4a2470eb8f09b7c500638df
ACR-13a3fe717ea14be7a5c923ffc0f0e62b
ACR-0772985aa7914cf5a3dba451981692d2
ACR-82c29c8b388649aea6a284883b60caff
ACR-e952e9c17806493eb1e7e66d71480d5b
   */
  Collection<ServerHotspot> loadHotspots(String branchName, Path serverFilePath);

  /*ACR-c0804acb5b384b4297273d9224e37f37
ACR-1d9b591627564cb09a266e85cc119774
ACR-3a384c1e92154a8caedb13fa4add5e19
ACR-0055072f65424783ab09e0e1a445a043
ACR-05d579c1ff164f098aed7db6edbf0b03
ACR-1c00c771f3124eb7a16d50b572f761b6
   */
  List<ServerTaintIssue> loadTaint(String branchName);

  /*ACR-033d19e0d59a459196d096febbdb11d0
ACR-7343357fdf0c42378cb89c93024b3e4d
ACR-526c22fef9a14b5285f02582bb09c04d
ACR-f35f914474fc431482ddb89f73cd6cfa
   */
  boolean updateIssue(String issueKey, Consumer<ServerIssue<?>> issueUpdater);

  /*ACR-8b44fb3433924eeeaac235c94e04caf3
ACR-876f6292039143d1a5b41a02bad9132c
ACR-47ddba6f6bfd48f1ab9eaeb6d2c9d2bd
ACR-98837f7d62454ab9a26047524cf4a760
   */
  ServerIssue<?> getIssue(String issueKey);

  /*ACR-42508f01a76d4384b2de8ad4fe02c238
ACR-aab9a59206284d369f66a3a3a193a687
ACR-a3a78315c0a8418f8c5241b0eb6d3e24
ACR-b8f1f429341d4fb5ab701616746e1fa7
   */
  ServerHotspot getHotspot(String hotspotKey);

  /*ACR-8c81bd3f67cd42acb282479dcaed1583
ACR-2fa83cfb69d74833abb330df928a11d9
   */
  Optional<ServerFinding> updateIssueResolutionStatus(String issueKey, boolean isTaintIssue, boolean isResolved);

  /*ACR-24b2f350edba4a84882c5f217ba52b73
ACR-e8db4d1818cb44ad943e39e4b45d50d2
   */
  Optional<ServerTaintIssue> updateTaintIssueBySonarServerKey(String sonarServerKey, Consumer<ServerTaintIssue> taintIssueUpdater);

  void insert(String branchName, ServerTaintIssue taintIssue);

  void insert(String branchName, ServerHotspot hotspot);

  /*ACR-214c54c192ae46d3a695e499f3379162
ACR-479c1ae73df24ac496117747cf9059c0
   */
  Optional<UUID> deleteTaintIssueBySonarServerKey(String sonarServerKeyToDelete);

  void deleteHotspot(String hotspotKey);

  void updateHotspot(String hotspotKey, Consumer<ServerHotspot> hotspotUpdater);

  boolean containsIssue(String issueKey);

  /*ACR-1430aad50fb64b4c9d0c358943ccf3a2
ACR-28db58a520a4439c8b41770fe78ab0a5
   */
  void replaceAllDependencyRisksOfBranch(String branchName, List<ServerDependencyRisk> serverDependencyRisks);

  /*ACR-023e143bbade458f8d198dfdc46c98da
ACR-d3bd574f902e4fe7ab63bac00988a4d1
   */
  List<ServerDependencyRisk> loadDependencyRisks(String branchName);

  void updateDependencyRiskStatus(UUID key, ServerDependencyRisk.Status newStatus, List<ServerDependencyRisk.Transition> transitions);

  void removeFindingsForConnection(String connectionId);
}
