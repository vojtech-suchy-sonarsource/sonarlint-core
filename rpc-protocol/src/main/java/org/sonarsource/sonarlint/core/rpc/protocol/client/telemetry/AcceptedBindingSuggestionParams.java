/*
ACR-a899339cbf6a4014b93acfd0506aa3a0
ACR-edc8957db9f1419e93012abbdc29a010
ACR-93a6b356f9af4a9e88b52fd52274a48f
ACR-b844ec856eab4990847c04abd2ba1aab
ACR-36b011f20b674f86bbc1531957d11ea7
ACR-3973e493c3474b7a8b5081d704ad60e2
ACR-4c5a2ed1563b4f489caddc3dee1fb52b
ACR-000b00db95384a8b81d31a2516852e2b
ACR-9e2973fd29354a2e900d31810e640771
ACR-f5e07120210444ce8c988816ddc43132
ACR-f811672ae263499f88ee141c34043c95
ACR-4fbef49e4afc42c5921a7e2c67216e6f
ACR-b63947aef83e436baba7fbe7744f1476
ACR-cbdeaf0d5ba3453d85abedd59ee36bc9
ACR-85b5b4709543400a876f2069c209eaed
ACR-92532fba6463446fba75b042f74e9f37
ACR-aaf9f8b0cfe94c1881e88d1a4e3782f6
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.telemetry;

import org.sonarsource.sonarlint.core.rpc.protocol.backend.config.binding.BindingSuggestionOrigin;

public class AcceptedBindingSuggestionParams {

  private final BindingSuggestionOrigin origin;

  public AcceptedBindingSuggestionParams(BindingSuggestionOrigin origin) {
    this.origin = origin;
  }

  public BindingSuggestionOrigin getOrigin() {
    return origin;
  }
}
