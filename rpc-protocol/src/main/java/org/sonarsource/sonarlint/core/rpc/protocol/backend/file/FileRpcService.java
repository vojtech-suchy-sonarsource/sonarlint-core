/*
ACR-4c199ca2342d4293a7a6ecfb72049266
ACR-ce9e00f8e7ad4d3b8453b6c8ffba1eb2
ACR-d5396076b4a5478cbfb2213649a6fba8
ACR-320b029745294157958e64fa91d058af
ACR-0a1c92c5068f4b829ea4cabb67c895d7
ACR-8d9fee9d555647298150c4a0e821ba9b
ACR-354610928d094223a68ef44bf8a3dae6
ACR-e81f189b02f04e3cb556f26fa060b6ab
ACR-854f4d7cb9334889aa1abee7b6e959b9
ACR-97af607573cf42d9a5b22a682ffcb994
ACR-1533f3cc1d5e43889dc320b1b0746137
ACR-1352919b2b934016925abacf53ad07b7
ACR-eae11bccc9a34d7c847848269c1d6d16
ACR-69832334478d4465ba5b6bd4bb66302a
ACR-6d6f990b799d45788adf4fa7c8369dd7
ACR-23a4e75aa1ae4fd7baf2db9ca692f54a
ACR-1831148660124231992e55a95f664cad
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.file;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment;

@JsonSegment("file")
public interface FileRpcService {

  /*ACR-0bc740c6e65c441da179ee267a5b1005
ACR-cab4b124b5574b16b322c3b195d4ecd2
ACR-22f87266fb204df9b27676b2ba3838bb
   */
  @JsonRequest
  CompletableFuture<GetFilesStatusResponse> getFilesStatus(GetFilesStatusParams params);

  @JsonNotification
  void didUpdateFileSystem(DidUpdateFileSystemParams params);

  /*ACR-e6f537043e56444c89d35b89b5a5fa48
ACR-79c482006da24c778df06fcf1a90f8a0
   */
  @JsonNotification
  void didOpenFile(DidOpenFileParams params);

  /*ACR-3b8294c54fa54e4492e285515950f0f9
ACR-03348d2bb17a44b19115d7ca5f5c0dc3
   */
  @JsonNotification
  void didCloseFile(DidCloseFileParams params);
}
