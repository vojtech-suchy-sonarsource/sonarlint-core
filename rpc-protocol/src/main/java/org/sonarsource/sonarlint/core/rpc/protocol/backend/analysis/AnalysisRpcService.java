/*
ACR-4ba5a00a49964b70b719f0abe856e413
ACR-ca260c6b66254f97898714f8565669b5
ACR-1db48b42605242d1a0af0caf7a514124
ACR-a80eeae343ff46209858802e82413785
ACR-b9c7c39532e04438b7560cfe82022ccf
ACR-312959ba21c0433ab529b72443f4ae77
ACR-974c589c2eb34e9db4a0dc960f019330
ACR-05493c0bc6204619abab8c2786901492
ACR-a7c79521a36843f9a91edf6fee879f11
ACR-926832ae929a42bea1d35374c93cbf9e
ACR-50c205d7c38c46408f98ffd2fd1e50d6
ACR-2b7b992f6bfd4a738b442c7cd67b55c7
ACR-5d7012f1ee6a48178313bed9902357c3
ACR-5bee6cc36a114d288a55112e8461ac09
ACR-ddbd06c6c64d491c9359ef9b2f13f0b0
ACR-2cda1fd65f0b49648b85abeb45ec23e6
ACR-bf609ac8da224c18aac164769964685c
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.analysis;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.client.hotspot.RaiseHotspotsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaiseIssuesParams;

@JsonSegment("analysis")
public interface AnalysisRpcService {
  /*ACR-4cec1eb235e248e7b59c5d86d8997cc5
ACR-970b70605c7a4957a161596eef855d93
ACR-00bada0141ca4f95bc4d3fa3f860d03f
ACR-93c74ae294c641818c0277e89d29a511
ACR-fdecea974d1a40f39c9573080563318c
   */
  @JsonRequest
  CompletableFuture<GetSupportedFilePatternsResponse> getSupportedFilePatterns(GetSupportedFilePatternsParams params);

  /*ACR-3ac303892bcd45dd8f97b42a7a596f8d
ACR-f93bf21f6492498d86b0b78a821c123e
ACR-0e2818494b234a3ead9dccb4f626fa24
   */
  @JsonRequest
  CompletableFuture<GetForcedNodeJsResponse> didChangeClientNodeJsPath(DidChangeClientNodeJsPathParams params);

  /*ACR-ea9eefb921f2457592e8f0888063f05c
ACR-6bf62f30f7e2483686767392d8f67a26
   */
  @JsonRequest
  CompletableFuture<GetAutoDetectedNodeJsResponse> getAutoDetectedNodeJs();

  /*ACR-066a2083b3dc46a193afffd37637bf4c
ACR-1cb6ea9cefe34e10b4ecd59bf822c818
ACR-8bae7ce778124f73b490165c69ee2ffe
ACR-118991dad7ae43f3a6569536681fe78e
   */
  @JsonRequest
  CompletableFuture<AnalyzeFilesResponse> analyzeFilesAndTrack(AnalyzeFilesAndTrackParams params);

  /*ACR-42d389c339f842269e01e6bded49a2ad
ACR-9556e9758019467287ff9c57836d60f4
ACR-1cab7b1a54554ecfaee537abab4ee10b
ACR-80b18cb2be814f2d9afbab00e74a974f
   */
  @JsonNotification
  void didSetUserAnalysisProperties(DidChangeAnalysisPropertiesParams params);

  /*ACR-92cd6337dc2349edb5b23c9363462ff5
ACR-ad8db849b2ef476ca6e7cba84906da33
ACR-c97440157f5841688faa7e15d6710047
ACR-c0672d98c5f643aebf951099c31e2cb4
   */
  @JsonNotification
  void didChangePathToCompileCommands(DidChangePathToCompileCommandsParams params);

  /*ACR-893e33fb17294ce6bbf63d049127e228
ACR-e03c68ddfc0d464facf6cc71fb288a16
ACR-cbef4b61056b401abb02a5e90184ec2c
ACR-c02eb68b46564e41b32efb25aa329efb
ACR-bf4cf160a0fb4a80a00ff4d7c9524aa1
ACR-c1e2c6cd0b784e348bda94f2bf3ab983
ACR-c23de62c04d0483193bd16f79c711348
ACR-81107d8de87f4e1abb27a8ab0efe12e6
ACR-5df991ea30784a1c979342fba421c38c
   */
  @JsonNotification
  void didChangeAutomaticAnalysisSetting(DidChangeAutomaticAnalysisSettingParams params);

  /*ACR-704a2b45e63a47089d4ddee946807e5e
ACR-f77e89a52b40485fa3ea5d942cc8d887
ACR-03d834bee8f746878e421bb0f423452c
ACR-903c5b090f24403987b2c66eda9fad23
ACR-0c9f582df8cd49438fda3233b1fbff11
ACR-3e98a9c41baa42c99bfa85cfd059e34b
   */
  @JsonRequest
  CompletableFuture<ForceAnalyzeResponse> analyzeFullProject(AnalyzeFullProjectParams params);

  /*ACR-3f582ed6accb419f8456cad29d855d6b
ACR-11636fe5b8264c889a5f8cb4765682cd
ACR-38e10eaa2b8448eaa059a867823e2330
ACR-293204530a4a44fd8b5d2245303d673d
ACR-4182ce4c5187437f89e306446a8dd742
ACR-85b309a707864a2a8a5b9885887cdbec
   */
  @JsonRequest
  CompletableFuture<ForceAnalyzeResponse> analyzeFileList(AnalyzeFileListParams params);

  /*ACR-4bf6b62f136b4a628d40fb75f8febd01
ACR-44b3bff1536e451bae2676cc797db2b1
ACR-a7faae9e9ea44231ba553a5cd166e50c
ACR-c619bdf4a7ec4b9cbcf7e9c99f8d4ffa
ACR-47bd96205e874028ad28d6221d407346
ACR-dcf0eabc79784724943fd9f939c8df62
   */
  @JsonRequest
  CompletableFuture<ForceAnalyzeResponse> analyzeOpenFiles(AnalyzeOpenFilesParams params);

  /*ACR-b6670dc1493d4fb9b1a7dbe8a9d0d0db
ACR-520618a021734309b6b1cb3f2e12edfd
ACR-eb4d463b7e8241be90488ecf515386e9
ACR-75534920e98740efa61f3af0a8234663
ACR-3484b84a272d47de88b45af0b2e20feb
ACR-9183f2fab47a42ba97fb8f1213e9ad7a
   */
  @JsonRequest
  CompletableFuture<ForceAnalyzeResponse> analyzeVCSChangedFiles(AnalyzeVCSChangedFilesParams params);


  /*ACR-3ff2c227261143558a81d68b90be4672
ACR-3232bc78a93d4e40848f185ed030c577
ACR-ab98e5989b7346bc995434df1cb18299
ACR-56067fada38040e086dca93dde886b92
ACR-f4ea5d4001c94b809b5304187429e899
ACR-c89bfd8776c44e3a9856c91a482931ff
ACR-ef03f6c1b7fb4c12b44a77574232a0e6
   */
  @JsonRequest
  CompletableFuture<ShouldUseEnterpriseCSharpAnalyzerResponse> shouldUseEnterpriseCSharpAnalyzer(ShouldUseEnterpriseCSharpAnalyzerParams params);
}
