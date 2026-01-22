/*
ACR-e2b0067c5aa4406b818135793438a614
ACR-1265d050e4f74a80b8400e25e58bd38e
ACR-7b050791121c44ccb57d08396546de04
ACR-71a20e45a2534f9cbda63ae6f5b34676
ACR-ec5bf0f65bcd434595eeb9bd40389aae
ACR-f77f47132e134c3fb3d534bcf97515ad
ACR-663f4124f4224d5b964d973706b767b6
ACR-72c6da632e744ef2bf8e9473d0caf208
ACR-5a315efc17194775b46f16112d10e402
ACR-a2e63cd40d1e49a598b74b8f60e31830
ACR-03656ec025864f6fb3c0c5ff563d3c65
ACR-7b5af2f47faf4bac98fd7e696f64193e
ACR-93ed0396a9144beb8aaf54e4a47cb895
ACR-56846c5afc4c4f7f803b9c15cb5fc182
ACR-6d156934336a4558a5360ee168d83d61
ACR-aeaff4bf428b498f8b25dd1c12bef5c9
ACR-fed70abf304c454e92b51d47ece6c72d
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.branch;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment;

@JsonSegment("branch")
public interface SonarProjectBranchRpcService {

  /*ACR-8d48b81faf4e499c8ef4a4461a90e5d2
ACR-1286a5f5646a4fa79fcd8c9c38fde494
   */
  @JsonNotification
  void didVcsRepositoryChange(DidVcsRepositoryChangeParams params);

  /*ACR-7a40f2dfe2b54f8f83cfd63ed3df363c
ACR-77990f540b9e4dd3aa4c56ce4a075058
   */
  @JsonRequest
  CompletableFuture<GetMatchedSonarProjectBranchResponse> getMatchedSonarProjectBranch(GetMatchedSonarProjectBranchParams params);
}
