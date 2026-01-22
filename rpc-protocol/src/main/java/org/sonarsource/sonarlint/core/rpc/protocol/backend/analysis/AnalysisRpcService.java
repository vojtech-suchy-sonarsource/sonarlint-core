/*
ACR-bb187f57bad44821b3a9f142615b7ac5
ACR-bed9c85f41ff439f9ec8d13689e0a97b
ACR-ebaa826840bb4e23b8ecf997d0cbca95
ACR-05e022b1c7e7487fb7479ecde4174859
ACR-28dcf87722fa42ceb16ea5c9b5399c6d
ACR-67577de97d28478ab85dc61f6e6aedab
ACR-7bc3c86f1b5745dcb8ee9a5ab6454d3d
ACR-c533ddc8c5704030a7255709daf2108d
ACR-5b18f12111b9438aa1fbda1226aca63a
ACR-5ccb9b80894b418ba0746128214dd71c
ACR-9322e597395f417b8769c677970f7d14
ACR-a79961dafb534fd59af361f04975e914
ACR-7b44dbf4aa2041a49e9d7641a073ab90
ACR-913f368c67ec431ca7cf8942d6b2bf8f
ACR-cb58cb6f42314c55b1fa68eee7813c8d
ACR-e38bf49c6e1246d380d9e19fd9e18137
ACR-2b6d8b8bfaf546a5bee2ac69cbe0efae
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
  /*ACR-57c3b1d74eea4db5924742ae273ad6d4
ACR-684a5c8dd5d74b0aac9af63515757690
ACR-b73ec480a4094125886fe84da742bacb
ACR-4be063d96ee84ebaa1d8bbc417233e3a
ACR-330694e587a94d98b82aec654ff49f7a
   */
  @JsonRequest
  CompletableFuture<GetSupportedFilePatternsResponse> getSupportedFilePatterns(GetSupportedFilePatternsParams params);

  /*ACR-938ac39831d94282b6650175c3ca6c51
ACR-f3afa3fb5ca64c11a51fd6779b7ce882
ACR-9ebceaa527054f9b82cc422389f044be
   */
  @JsonRequest
  CompletableFuture<GetForcedNodeJsResponse> didChangeClientNodeJsPath(DidChangeClientNodeJsPathParams params);

  /*ACR-c5aba91ceb5b48a6ab985f11fb1658e6
ACR-8511ace834bd4034a8a3c5a230c1f6e4
   */
  @JsonRequest
  CompletableFuture<GetAutoDetectedNodeJsResponse> getAutoDetectedNodeJs();

  /*ACR-071d9b84584c4e1fa7339e96ef3dfa3a
ACR-8fabc4720ae54314a5816f5a02cc1334
ACR-2b17b1f1752d4f9fb000765bcab59a99
ACR-600ac3ae5da04f1593abf8e988ac9d25
   */
  @JsonRequest
  CompletableFuture<AnalyzeFilesResponse> analyzeFilesAndTrack(AnalyzeFilesAndTrackParams params);

  /*ACR-5fffe23b9de24a64a32613433ab77192
ACR-df830d3cf80842938bcae2c38390c7e1
ACR-1cd87baed85b440eb45a456b1c4a51c8
ACR-ca114efacfeb4ed9a311d902bb83bc8a
   */
  @JsonNotification
  void didSetUserAnalysisProperties(DidChangeAnalysisPropertiesParams params);

  /*ACR-879cf65964bb4165ad1b6acc72b617f7
ACR-cd1883a07dee4cce9c66a627d287e605
ACR-a088c6224c5647bc816bbc92d820ee44
ACR-943c566430d64ed096dfb21791b4c054
   */
  @JsonNotification
  void didChangePathToCompileCommands(DidChangePathToCompileCommandsParams params);

  /*ACR-ff3394dc146048289ad6d58164e7a80f
ACR-b8e3509dea65471ea6a2ed842087463a
ACR-2268855411b9448ca7c92230aaa0777f
ACR-13d6e4e812604fac9be133c8c07345ed
ACR-72c66940d692402abd7ff6974a9a0c5b
ACR-3e97d7ce900f4e5b8c7edb4dc1b47ae9
ACR-fe02ad9eec8b419f8f1aff684f22d112
ACR-5aea7a7324294c42bb1d0267847bf3ba
ACR-f9269ad9eb43498eaa867d96e2aa9877
   */
  @JsonNotification
  void didChangeAutomaticAnalysisSetting(DidChangeAutomaticAnalysisSettingParams params);

  /*ACR-3fbdf846567d4430a3de78727ff7cbdd
ACR-e3a6f28dd9e244db8fc07219bef10543
ACR-078dcd40ab2b4cd8a718e676becc234d
ACR-499aed212be749c68c87771420191c59
ACR-96c602218f614a3cbb9d369ceb6e6d77
ACR-3c660ff166034814a812989baeb701dc
   */
  @JsonRequest
  CompletableFuture<ForceAnalyzeResponse> analyzeFullProject(AnalyzeFullProjectParams params);

  /*ACR-92b297bda2d24045a81b1c2c92673195
ACR-318b7dbe59614c0e92979c1a04a63660
ACR-45c737ceff314b7e93246b618032900a
ACR-f466c94043c84cd4972bdec1cd304dd5
ACR-cc68ce3701a0436381d5197da206ad32
ACR-10597eb8d5d841a78e01d32a4664130c
   */
  @JsonRequest
  CompletableFuture<ForceAnalyzeResponse> analyzeFileList(AnalyzeFileListParams params);

  /*ACR-6c8bedf3a7b14f66b55a2db6e96b37fe
ACR-c7427dcfadf24360b4fa97626e1cea22
ACR-e930228191324aa7a6c34705c643f9dd
ACR-51b1081548314e97a01665bf9946df5c
ACR-e48a5d3a75c640e4a96b264304a63e4d
ACR-4ae56bcba63a4ba4aed609bb946184de
   */
  @JsonRequest
  CompletableFuture<ForceAnalyzeResponse> analyzeOpenFiles(AnalyzeOpenFilesParams params);

  /*ACR-62f034db9d7b474b857a6376567976e7
ACR-f50cc75292d945e1b5304012b1d5a954
ACR-05463612d81e4f17bf848e0111d6576b
ACR-755911948c8e48d68c71b39cfd04e6f7
ACR-08491a2009524011a07b2bc5f6345711
ACR-d75dd7fa929f4affb067a386f3aa0712
   */
  @JsonRequest
  CompletableFuture<ForceAnalyzeResponse> analyzeVCSChangedFiles(AnalyzeVCSChangedFilesParams params);


  /*ACR-effcaf7e8ca54af0b3f32b5a23d9c45f
ACR-c0a2a16779fb40f98a2da036b5a1e840
ACR-a7496597e00c4077acbe10cb500c20ea
ACR-80276fb564094214ba55df2823890a94
ACR-2204138c892043cbbbc80c3359406b34
ACR-77f6929fede94b778ed37d4db5a0cac3
ACR-7a17bb24cc084e22a7155671b3ea19cd
   */
  @JsonRequest
  CompletableFuture<ShouldUseEnterpriseCSharpAnalyzerResponse> shouldUseEnterpriseCSharpAnalyzer(ShouldUseEnterpriseCSharpAnalyzerParams params);
}
