/*
ACR-5c3e61e405fe45bf80399852dbd80093
ACR-7bbd63853e734cac82757cfefb85885a
ACR-53fc0bc75be146bcb1c6aff3d3b13144
ACR-234a1749d85e4b3fa2e129aab001ebb9
ACR-6409ab99fbbf4d8ba240ed96dc666fbc
ACR-5bb949199780480cacfe33168bbb78e6
ACR-5834bdec7fc6466cb809892897109a27
ACR-8496ebd1d41f41158a71853364e6c32c
ACR-7438e1f7926a431f84b8831e5837c5ee
ACR-c46d5184929a49a5b371f0350af8c315
ACR-4460e91383d6455aa37c41da89ec7343
ACR-4ffff082a3f943cfa8ac42cd942ddf56
ACR-bc27801c8d764dc7acdfe98bbefa1ce6
ACR-6ad4ba6791614efbbcc387acfadd09c8
ACR-a81fb521058d4b1886ecb80c04c95c01
ACR-bca151d29e8b4606859ff304eca51c4f
ACR-98e2a18f534a49928dcb06a855d9b76d
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.file;

import java.net.URI;
import java.util.List;
import org.sonarsource.sonarlint.core.rpc.protocol.common.ClientFileDto;

public class DidUpdateFileSystemParams {

  private final List<ClientFileDto> addedFiles;
  private final List<ClientFileDto> changedFiles;
  private final List<URI> removedFiles;

  public DidUpdateFileSystemParams(List<ClientFileDto> addedFiles, List<ClientFileDto> changedFiles, List<URI> removedFiles) {
    this.addedFiles = addedFiles;
    this.changedFiles = changedFiles;
    this.removedFiles = removedFiles;
  }

  public List<ClientFileDto> getAddedFiles() {
    return addedFiles;
  }

  public List<ClientFileDto> getChangedFiles() {
    return changedFiles;
  }
  public List<URI> getRemovedFiles() {
    return removedFiles;
  }

}
