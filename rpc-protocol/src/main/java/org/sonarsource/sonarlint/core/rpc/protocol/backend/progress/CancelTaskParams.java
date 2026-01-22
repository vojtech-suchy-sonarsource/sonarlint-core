/*
ACR-c0abea80688d414c9b669e38e10c2bfa
ACR-10270bec0a48429087f2ed072cb63a5d
ACR-03f100f855d247d9ae28c7de882bb79c
ACR-4d5d7acb98e94303ab5c16b269aaaba4
ACR-4890f0b6915845f1a911ec9514fbe4db
ACR-a054bea17d1c4942afb49d70a72ca20b
ACR-3d59288327794eb4ac9c5bd80ab13c06
ACR-f995b65d346340069ad89393b58d6fad
ACR-c77ee06e2968479b8e04cded77b8a25b
ACR-f3876ea7207f4b618ddf1453a2141df3
ACR-9d04f63f42ee438e9b65443270005620
ACR-70bf1095b2d64f80ad7f337c10e4581b
ACR-426dd72fdf3e478eb985489e1bd61688
ACR-708ff7516a11444d869bec6c50dd094a
ACR-3bf0772fa6dc4d5aaa792e78ec59b6a1
ACR-6b9d06e1064047bcb97311a898a29c5a
ACR-e8fb7b03ed8344348583549c72599aab
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
