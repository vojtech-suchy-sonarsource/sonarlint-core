/*
ACR-5a029ddce8214775a44530ded804f911
ACR-5c161294685b4518967d10e6df102bb5
ACR-9a1be4a629e94b59a40055721b6b1d2c
ACR-c87da0a4215644e89ef6602386e4052c
ACR-81519a19bc4d43c5accac59725a9c54a
ACR-e050544654cc4f76ae5f172c0471f2b5
ACR-735e345a12784021a841d9b5bd9b75a4
ACR-b10bbe015aa4407482e9d34486dfea88
ACR-65743ae1c6d4420d9043d10deefb1828
ACR-c4934b6947974ffbb0a8aab3e102ec99
ACR-79357be7732341a8a3eb08f5b6282246
ACR-183fa8c12adf45efb5d27adeb0af27cb
ACR-402cb3ba41ac4148a5cb4a531ddeafb1
ACR-53d454b7a31449188348d2a16f7e52dd
ACR-104690b9cfa542fa973688c8644d6c39
ACR-58c780f170d0423699e35db29a8985d6
ACR-2333b29567f54bd3b08041b2c8c19f32
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import java.util.concurrent.CompletableFuture;
import org.sonarsource.sonarlint.core.fs.ClientFileSystemService;
import org.sonarsource.sonarlint.core.fs.FileExclusionService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.DidCloseFileParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.DidOpenFileParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.DidUpdateFileSystemParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.FileRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.GetFilesStatusParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.file.GetFilesStatusResponse;

public class FileRpcServiceDelegate extends AbstractRpcServiceDelegate implements FileRpcService {

  protected FileRpcServiceDelegate(SonarLintRpcServerImpl server) {
    super(server);
  }

  @Override
  public CompletableFuture<GetFilesStatusResponse> getFilesStatus(GetFilesStatusParams params) {
    return requestAsync(cancelChecker -> {
      var statuses = getBean(FileExclusionService.class).getFilesStatus(params.getFileUrisByConfigScopeId());
      return new GetFilesStatusResponse(statuses);
    });
  }

  @Override
  public void didUpdateFileSystem(DidUpdateFileSystemParams params) {
    notify(() -> getBean(ClientFileSystemService.class).didUpdateFileSystem(params));
  }

  @Override
  public void didOpenFile(DidOpenFileParams params) {
    notify(() -> getBean(ClientFileSystemService.class).didOpenFile(params.getConfigurationScopeId(), params.getFileUri()));
  }

  @Override
  public void didCloseFile(DidCloseFileParams params) {
    notify(() -> getBean(ClientFileSystemService.class).didCloseFile(params.getConfigurationScopeId(), params.getFileUri()));
  }
}
