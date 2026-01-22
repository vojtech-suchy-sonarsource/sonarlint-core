/*
ACR-91e576dbae51457084dd2e45800f3eb0
ACR-72f187ac61524a36848de1f97d16b273
ACR-653de5705e624a3685b95bca5a259542
ACR-152f8681f2c14d0ba8f761959afbdf21
ACR-ba2790908d9b498d94e95786a3ae54a5
ACR-cfdc6687237842b2a85c4b54640e7386
ACR-1ae6e1a4aa1244b69f46ef970785d661
ACR-8fd145ecc23046da97821fad576fe5b9
ACR-5d8d1bbefc304a269fe3e37ab6880e43
ACR-ec588a4a34cb41848082e3c4948a79cc
ACR-0a87672f6c2143a4b590064764636f23
ACR-b337ed7e2f5942d7846548d87ec3f682
ACR-4cba4292a95b45c089f88b6e8a079e2b
ACR-09f474d6acd24ab9a2686805eaff2373
ACR-9e2c1eaacdd648779cd44faeefc942bb
ACR-b7efd329d6674efca8c6903f3e353394
ACR-bf8f12ddcf2e4a3cbc168453303dd736
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
