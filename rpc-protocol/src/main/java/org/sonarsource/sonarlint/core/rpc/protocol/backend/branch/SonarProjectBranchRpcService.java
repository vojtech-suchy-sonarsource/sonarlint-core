/*
ACR-b7f38f1d6c2845b2aaa5ba817877c6b3
ACR-38ec20f731e2459998e0c86c8a08b2a9
ACR-70635a43b1044c3eb68214fd1be5cce7
ACR-2f47c53235774497bd35d64e0a757135
ACR-963c52a101464c59a979a345724869cb
ACR-8011006ed2ad46b98ce82025904acf14
ACR-293151109af245c99f09be5617af80d2
ACR-e4a73a5db5ef4daf8b4bedd2031aa8d1
ACR-805bbc5b78ec4dd08543f08b34974e4e
ACR-5c3ecd1ffd0141db9b8dac017b4d4778
ACR-757f3eeee6484d2b9a9ba1e8cee8ef33
ACR-dfab165fc60344139c8d6247fbb52caf
ACR-eb22d4ce7f0541659c8653b353982041
ACR-cb280616273c4168b2f398f3849e3591
ACR-0cc3e689695e4714bee400beb431fd60
ACR-462f70a5fa4f4fe2ae7b4017c8f1c067
ACR-550cce23bae741b28f04e4c3e7135b72
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.branch;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment;

@JsonSegment("branch")
public interface SonarProjectBranchRpcService {

  /*ACR-81ac30e3b91b45648404323ba3973d12
ACR-16a5bb9e59184a36904c23d5c35426a5
   */
  @JsonNotification
  void didVcsRepositoryChange(DidVcsRepositoryChangeParams params);

  /*ACR-16c17cfab96445b587474637d7af0fef
ACR-53fd1b721e2445d5ae87e3bad025a8b4
   */
  @JsonRequest
  CompletableFuture<GetMatchedSonarProjectBranchResponse> getMatchedSonarProjectBranch(GetMatchedSonarProjectBranchParams params);
}
