/*
ACR-cd51b1054dfa4784aa7b3b8f18a03eea
ACR-f7eb3bddaaa14011a63eca3b4cfcee45
ACR-103fdf93695e4e7fbf065a099253b9d3
ACR-e10750c78aa94d1391685363195e4beb
ACR-de5d12c9731c4eac8d597b8cffa077d0
ACR-c0722fd1b7e3490dab11b4c5eae4b40c
ACR-d392362a01f14a62961fd5ee4eeab5c0
ACR-7a06f23d8a42458d8444c6a8630f1bb2
ACR-2cdb064a86184eaf9c46e3cad8360abf
ACR-5ce8befd362148688fed13676b1611c2
ACR-74683dd68fa14da99c041749c86fb8da
ACR-85b746e0cc18482e889f1f5425ca3334
ACR-d7e4370fa530497da45363b0f51a2260
ACR-04f253a60c3c490a817c3877d0511920
ACR-665cf34a4f7e4ab8a88d81f43318e1d4
ACR-9d763b6744014cf1ad8ba034762dd1c1
ACR-632d91a65d084a40883fd3f7abb1dcf5
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import org.sonarsource.sonarlint.core.log.LogService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.log.LogRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.log.SetLogLevelParams;

public class LogServiceDelegate extends AbstractRpcServiceDelegate implements LogRpcService {
  public LogServiceDelegate(SonarLintRpcServerImpl sonarLintRpcServer) {
    super(sonarLintRpcServer);
  }

  @Override
  public void setLogLevel(SetLogLevelParams params) {
    notify(() -> getBean(LogService.class).setLogLevel(params.getNewLevel()));
  }
}
