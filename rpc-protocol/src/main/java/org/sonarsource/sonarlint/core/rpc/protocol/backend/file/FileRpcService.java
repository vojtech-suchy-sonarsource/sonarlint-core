/*
ACR-88afb410fe6e4050b1cddb47db38cdcb
ACR-6912678b6fcd4474848cc12b2245f6ec
ACR-6dae87c034ec44b78cd02808b8d3c0e4
ACR-2c98f612edff4e83a7e19ae28214cd5f
ACR-9f19f356978b4f3dacd9062532e88cbe
ACR-49d5449a219a497abb1c3cbad6338513
ACR-8adca8567aa84cd281ea36af9d718464
ACR-1929f96b7a0e4173a9c1ebd8adc26927
ACR-c537c32afac94e08b2dd11e8073cbaed
ACR-b57e63ed5cc34498a3a9cb205af26b60
ACR-676a020c339f4079b8e48d287069913b
ACR-4cc18ef824dd4db09e8299b91d8122fe
ACR-7a6d6550ba5a4c48bed3195495eb7507
ACR-d11ed75eec4c41a59e6c52a97729cd2a
ACR-371caee535bb48d38dea646aa6bdace6
ACR-ae8b6654540c4382a8846ac44ad47e3d
ACR-8379b947845e451fa6d03fefaef59496
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.file;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment;

@JsonSegment("file")
public interface FileRpcService {

  /*ACR-dde00e94b82d48edb4ea54cbb140620d
ACR-39ca7c1d0114486b961f07756ccd227e
ACR-05b353ec44134326ae79c29bd39ab218
   */
  @JsonRequest
  CompletableFuture<GetFilesStatusResponse> getFilesStatus(GetFilesStatusParams params);

  @JsonNotification
  void didUpdateFileSystem(DidUpdateFileSystemParams params);

  /*ACR-721960ddb0cc431da0fb6636ff806476
ACR-f5135615732740398e26aac6d74cc8de
   */
  @JsonNotification
  void didOpenFile(DidOpenFileParams params);

  /*ACR-13445e302741455a9477a6384a767483
ACR-9de781490bfb498b907abb856f9d703e
   */
  @JsonNotification
  void didCloseFile(DidCloseFileParams params);
}
