/*
ACR-d478938a30ee406a9406fced76e31d64
ACR-d0830c964de64048aec1c74ad0b25757
ACR-b5054cdc79ce489abca1c647d497c36c
ACR-776a054ff2b444c6b0c671b083a8a3e1
ACR-51baaecede5b4136a4dcdcbda2c18fdd
ACR-49fdc240b07b47ec9b3fc87d0470b9f7
ACR-10411039ee1043e98be4e47f2fd22f3f
ACR-933574e19fe94182b3dc54efdbb2ed31
ACR-bc7f56078fa9409b8400152e3c3dfd3e
ACR-620328715cb34ab9bccfc0185fe30dfd
ACR-80131e8a0eea49ac904e5321cb913048
ACR-43b87c96b711486da4e0f273d78f657a
ACR-dde7d6c1ab5947c7896198f8610fc6bf
ACR-5b34803bb54849c190d256ba1a2ddf09
ACR-317acd497a8c4f07b4cae3c02263bce7
ACR-558264c7e343467c8e15c103220dff01
ACR-3c9cc3b164ff40c892fd12888c674c40
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import org.sonarsource.sonarlint.core.commons.progress.TaskManager;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.progress.CancelTaskParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.progress.TaskProgressRpcService;

public class TaskProgressRpcServiceDelegate extends AbstractRpcServiceDelegate implements TaskProgressRpcService {

  public TaskProgressRpcServiceDelegate(SonarLintRpcServerImpl sonarLintRpcServer) {
    super(sonarLintRpcServer);
  }

  @Override
  public void cancelTask(CancelTaskParams params) {
    notify(() -> getBean(TaskManager.class).cancel(params.getTaskId()));
  }

}
