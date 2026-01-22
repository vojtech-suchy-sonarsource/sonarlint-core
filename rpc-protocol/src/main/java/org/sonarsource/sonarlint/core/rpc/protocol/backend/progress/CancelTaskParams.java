/*
ACR-5b8a3ee4fc4149a8ab5930ce9f24cea6
ACR-4770129a2ab447898b7467eb68d3a89c
ACR-32ce6998a002440284d6887eaad53421
ACR-3d5361cd38af4a4db10492bec132567e
ACR-982d55dcc8a443a1b9e86e48ea6be961
ACR-f158160b6d624c189941f272a685f2a4
ACR-da346a94f081482fa6305cb118f41efa
ACR-d34097fa3ed24cb2bb3b7b46573e5ff7
ACR-02430f1a3c8b4b62a314ebc6debe1e4b
ACR-396c71fa5a604da18dc53a9e75250868
ACR-5d39adceda3c4d5781c2d7bd75808810
ACR-f6e21efd78c44454aeedb4137c0cd0d7
ACR-303aba13017a431b86325b4ffc8dbe7a
ACR-1aee07a8f3434a60ab820e7afb22a322
ACR-73f8a91c85c54623a1d3262c4fa03778
ACR-ed99511b6dc8477283054d5f3c2473ec
ACR-e6fc5c8a87324beeb8bb4182c7054def
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.progress;

public class CancelTaskParams {
  private final String taskId;

  public CancelTaskParams(String taskId) {
    this.taskId = taskId;
  }

  public String getTaskId() {
    return taskId;
  }
}
