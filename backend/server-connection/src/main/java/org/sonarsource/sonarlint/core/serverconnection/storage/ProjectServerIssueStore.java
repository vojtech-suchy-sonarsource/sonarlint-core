/*
ACR-ea398c6b10944bae9a84804d3308663b
ACR-5629967478e149a495ddae4f346dfcdc
ACR-8ca62ad4269145de8df8fcde2a1c6690
ACR-74ceb0e56b12470292c1191892c979f2
ACR-2eb0ab148fe34e9e9bb31eb9695f140b
ACR-46faa1b4dc354a4499eead6fe159eac4
ACR-7b491714e0c34a7ab469d622f2feaed5
ACR-4a850ce53e7c416a8a97e3b0cdf0d827
ACR-837a8a9d6acf46db8a2b48d0788de098
ACR-1834cd2c78154874aac53f6b432d7277
ACR-cd055ebe148c493b954b5d5edef9da08
ACR-64ad03d22ab24307b0f395e606b73941
ACR-4ac7cde80b9448b3bb0fb089eac39710
ACR-4a89e26dbe2841b8bc7f88c8a38ca29e
ACR-9f00eefc35d6465db37c157d5e760dd2
ACR-7a340085337e444198ba692654973dab
ACR-19fd296a444c4db3bc7db4c67104e31f
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

  /*ACR-81bbecb990274c5e85ecd2e5c7edcab5
ACR-d571e71764574bb598f08a49f8439624
   */
  void replaceAllIssuesOfBranch(String branchName, List<ServerIssue<?>> issues, Set<SonarLanguage> enabledLanguages);

  void replaceAllHotspotsOfBranch(String branchName, Collection<ServerHotspot> serverHotspots, Set<SonarLanguage> enabledLanguages);

  void replaceAllHotspotsOfFile(String branchName, Path serverFilePath, Collection<ServerHotspot> serverHotspots);

  /*ACR-320065e5adf54920bf763515b3ce698f
ACR-b8f45bb065504ee699aefd8e30f57dfe
ACR-028ca19985d24359b33d48865e713ce9
   */
  boolean changeHotspotStatus(String hotspotKey, HotspotReviewStatus newStatus);

  /*ACR-b3434e9b7bb04128991e3afcbe78d29d
ACR-3089be4d22f24aedadaece06c6d6c08a
   */
  void replaceAllIssuesOfFile(String branchName, Path serverFilePath, List<ServerIssue<?>> issues);

  /*ACR-e701fcfe826c420390b64065c97f27ff
ACR-9794dea360a8453ca8784ea7142666a4
ACR-7aace3495b43416c826d916d008c25e9
ACR-0518d9eaac864fe5822e74969953151d
ACR-937f2e38b73f45928e7e06b7cbdc983a
   */
  void mergeIssues(String branchName, List<ServerIssue<?>> issuesToMerge, Set<String> closedIssueKeysToDelete, Instant syncTimestamp, Set<SonarLanguage> enabledLanguages);

  /*ACR-f8ac8dab30884f74871f074373735f6f
ACR-424da9c285af41f8968c7fd8f76dbfe6
ACR-8e9753296ebe4fb19910a121b377c60a
ACR-577c7f79770f4d6eb067f7c3992d565b
ACR-41f9c50ad43144b7b3a2af420b697147
   */
  void mergeTaintIssues(String branchName, List<ServerTaintIssue> issuesToMerge, Set<String> closedIssueKeysToDelete, Instant syncTimestamp, Set<SonarLanguage> enabledLanguages);

  /*ACR-49adbcfabde54d4e825cb396493f3a32
ACR-ce2f57ff5e98417681e5c1ed3f9a05a3
ACR-6553e1b14e734a509c24c7ab2b2bfdb6
ACR-ce04510e762a43a9ab6dc59a08950812
ACR-175bf8f35eef45b8bba6f8b0ffd0892d
   */
  void mergeHotspots(String branchName, List<ServerHotspot> hotspotsToMerge, Set<String> closedHotspotKeysToDelete, Instant syncTimestamp, Set<SonarLanguage> enabledLanguages);

  /*ACR-0359ef66cdc7437280b93378a7f137ab
ACR-03f7965c7a4d4b149b5d4b5ae5a7ae51
ACR-a2e2611583f344d6a313883df869d56b
   */
  Optional<Instant> getLastIssueSyncTimestamp(String branchName);

  /*ACR-375baf7cde8f4f1b9d30f9e7f7665bae
ACR-8582fb78bd6448099c39860bbaa686da
ACR-5166119da00844bbbcd055170f9c4969
ACR-b621467643b54db1a822ab5dc334c491
   */
  Set<SonarLanguage> getLastIssueEnabledLanguages(String branchName);

  /*ACR-d7a9bb87932047f2a10e03ce64bab838
ACR-a078020f50264606b505483a77da2f5d
ACR-acaac875e8794b6d9343cb71e448fa42
   */
  Set<SonarLanguage> getLastTaintEnabledLanguages(String branchName);

  /*ACR-c471ea665de74741b761130750d60003
ACR-3b82bb4d2b0e42d89308092dfcbf52b8
ACR-d12b66d05a5742afb202dfd6e1f87db1
   */
  Set<SonarLanguage> getLastHotspotEnabledLanguages(String branchName);

  /*ACR-6cd532c89d92415db083b937858820d7
ACR-aecd33db8d5349279c19faa467bec854
ACR-2bf0a3e4429f40b3b17fac6a75d105f2
   */
  Optional<Instant> getLastTaintSyncTimestamp(String branchName);

  /*ACR-4ab0d36eccc44304a696b8a3984880d7
ACR-555c06fa4eb74e879b46aa63d75fc72c
ACR-e700d673e8a046ada01deca061f191ab
   */
  Optional<Instant> getLastHotspotSyncTimestamp(String branchName);

  /*ACR-821e7b4da83d41c48dbebb71421772ac
ACR-3e1814d74b47429a9cf8c2b8a91e2742
ACR-ad2e0baf57214e00a43f3a1f70dfa4d6
ACR-f59b9e515801456cb8d1189d28916422
ACR-ce32595b669748dea1d587226676afad
ACR-d9a3950023b84d0ea8aa61103f3c9146
   */
  List<ServerIssue<?>> load(String branchName, Path sqFilePath);

  /*ACR-e829ee003616444192bfd6dfab6c7e8b
ACR-ea52523714094fcf88667f4c4d8b5e09
   */
  void replaceAllTaintsOfBranch(String branchName, List<ServerTaintIssue> taintIssues, Set<SonarLanguage> enabledLanguages);

  /*ACR-95ed6e7a274c4a788a5ac1eaef3bcd04
ACR-e91c13c81d264565b6bb194909b9b28b
ACR-3d11cbf560484575a8662a2a38451d8e
ACR-683d2ba5c9b84844b39bea95990eb0ea
ACR-51ccc08d12da44f38445afe4bbe90eb1
   */
  Collection<ServerHotspot> loadHotspots(String branchName, Path serverFilePath);

  /*ACR-7aa41865145c4593b272e2de90ce2532
ACR-b600b2091de446ffabd7a39f0dbef1f8
ACR-6dc3d73193334527b3a06ddb16322a45
ACR-7d6bb661ef5045e2af072075ed02ef3c
ACR-495059913c2c463a95883239e0ec5ccd
ACR-50db291110d449fd9d60cb7a7955502f
   */
  List<ServerTaintIssue> loadTaint(String branchName);

  /*ACR-165e0e7a6491469483a74fd811a26527
ACR-249544b12ef344a0b88c8aa5ebdcfee0
ACR-c7e583ae8986433da74f027b098fe8f0
ACR-9c59458ce71247dbb590c1b8988e2995
   */
  boolean updateIssue(String issueKey, Consumer<ServerIssue<?>> issueUpdater);

  /*ACR-d71cfdf7c6e14d57b57dd5400d836df3
ACR-3ea47e62f95042089a4a199dacf8d331
ACR-2cce6a062dfd49a7b0b9b979c5cf531d
ACR-05788933a9274819a0b7ea80f50fd5c6
   */
  ServerIssue<?> getIssue(String issueKey);

  /*ACR-f72f3f04c26b40d999ec77d3272ae099
ACR-5c3159b670b443a2a73b3d1764f692c2
ACR-261c195a86ca48c2b6e901605a2e0429
ACR-c66e81962b9e4d20926209769cfcd100
   */
  ServerHotspot getHotspot(String hotspotKey);

  /*ACR-825eb147a4da4e02a08b07174614fce5
ACR-44797b72b8f24b74a598d00eefbff07d
   */
  Optional<ServerFinding> updateIssueResolutionStatus(String issueKey, boolean isTaintIssue, boolean isResolved);

  /*ACR-aa5611b3d4074855bdd919efa5601268
ACR-6b9f4072656e4ee2bf610df6184a4ed4
   */
  Optional<ServerTaintIssue> updateTaintIssueBySonarServerKey(String sonarServerKey, Consumer<ServerTaintIssue> taintIssueUpdater);

  void insert(String branchName, ServerTaintIssue taintIssue);

  void insert(String branchName, ServerHotspot hotspot);

  /*ACR-e3a65caec8ca44d49c0d743237855c70
ACR-2424d2fd323b4ea483ae63b2e95ce0d5
   */
  Optional<UUID> deleteTaintIssueBySonarServerKey(String sonarServerKeyToDelete);

  void deleteHotspot(String hotspotKey);

  void updateHotspot(String hotspotKey, Consumer<ServerHotspot> hotspotUpdater);

  boolean containsIssue(String issueKey);

  /*ACR-9cf1dc03a39b44b990a1dc86d81504b7
ACR-51eb5dbd241d4bd6b241b59ee34681aa
   */
  void replaceAllDependencyRisksOfBranch(String branchName, List<ServerDependencyRisk> serverDependencyRisks);

  /*ACR-a6bf6999fec4427f831648584d7bbe7b
ACR-50877f23b0e547d5aab53e4a1cea95fb
   */
  List<ServerDependencyRisk> loadDependencyRisks(String branchName);

  void updateDependencyRiskStatus(UUID key, ServerDependencyRisk.Status newStatus, List<ServerDependencyRisk.Transition> transitions);

  void removeFindingsForConnection(String connectionId);
}
