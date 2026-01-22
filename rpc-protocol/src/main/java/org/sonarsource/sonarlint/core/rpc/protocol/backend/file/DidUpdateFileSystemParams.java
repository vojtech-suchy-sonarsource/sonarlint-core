/*
ACR-f9fe10316fe9464680a2c8ce81b23f36
ACR-787ea4d7590a461180ac9a6faec89de0
ACR-4373689f109042baa4e984a5be675466
ACR-e27c774a16c84f7ca44edba014d721b6
ACR-a74fc8aa9a594c43b61cc6f8340de0c1
ACR-0f3fb27fd9e344f19dac7eedd0314848
ACR-fe22a4ca228b4b8996e21a006ad1657c
ACR-1bba7e050bc6411e9fa40f4724471150
ACR-407293fabf0a409dbfcf1d9f56dec46a
ACR-5df41a9e1aa94d7bb78e6d507f1c9d5b
ACR-636b8c4ea6c94b75975f1f66d5daebad
ACR-8ceca46e573149b8bd6f9894e4b1ed77
ACR-1aaf69fb7e8c4b058071d673b407f857
ACR-50ced5e2da1d481285f344e5a1e0b165
ACR-d51057ce9ef540c8a7e0cb35e75d31e2
ACR-e3569d5e04984f30b15633383b4dfb46
ACR-b0784b9d9fc1428c850d0977320cde1c
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
