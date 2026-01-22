/*
ACR-8bc295f8232940d297e7c218e8a91206
ACR-f62de8b7a5f74931b057b9c5e115b4e7
ACR-8f9dbd6adbbc4a908ebf6f43939fb3d4
ACR-4d2fbf2376824f059c7f78ca32944d3a
ACR-afa017503e534272bb6168eff1d82af1
ACR-a4f776e36ba84d3bbbaaf0664dc14757
ACR-4c31e70c703e48c0b03b17fb5db445ca
ACR-0646e2577ec14109991b1eea1f8e104d
ACR-0966a20dfadc47a9ad5aa31d602418e1
ACR-92e43a20f5a74a169931ef13a0a99afe
ACR-293588dd617547cb9ce2faa12c40de4c
ACR-0430391ffc504538a5a3fef768a0b32c
ACR-7448dde70dd34d3db57fe9fa36796897
ACR-ca7b336190ba4e3494ca88b230976f90
ACR-a9c0f767b696496493998b2a7b2dbb8a
ACR-e3671c1f6eb944efaf2c56e49a7ddd41
ACR-94730c178639478299be161d717ee384
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
