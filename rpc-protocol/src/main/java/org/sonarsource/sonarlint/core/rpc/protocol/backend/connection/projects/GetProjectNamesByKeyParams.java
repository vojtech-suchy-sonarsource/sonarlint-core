/*
ACR-a094853e22e84d99b75f9b2fc7b2864e
ACR-c02920a1e2a14ff9a3e1b667022b0772
ACR-be9e61aa4f8e4672a8cd109dd97dca2a
ACR-2e348796a8ce4b6881ab8eb3c8703788
ACR-d68e3ebb635d47a4a49921d61e749e29
ACR-09716b19bf694a0c899328e0018e974c
ACR-2c1352eb95e34af6acdfc701a6e12606
ACR-8c37190c864c44a3bf7007ca928324ae
ACR-a9f2d6ca9fe44ab89a8b338c1083bb1b
ACR-21f8398dafa74e118bea25b94f4e1f6a
ACR-796433bb6a8b4f14865b3ee57cb81849
ACR-d24988c46e1345b59a65b74baedf8c59
ACR-0188c1b5af2849dc80f0362019ac7514
ACR-25ff3cb5c7fc4d41a9367357a0261d51
ACR-826e072de358427f94fed7ba6971493e
ACR-3acffdabd3904f5d833001b6b9dbe362
ACR-7b5d86c46ae945acb93de67c4c1a11c3
 */
package org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.projects;

import com.google.gson.annotations.JsonAdapter;
import java.util.List;
import org.sonarsource.sonarlint.core.rpc.protocol.adapter.EitherTransientConnectionAdapterFactory;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.common.TransientSonarCloudConnectionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.common.TransientSonarQubeConnectionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;

public class GetProjectNamesByKeyParams {
  @JsonAdapter(EitherTransientConnectionAdapterFactory.class)
  private final Either<TransientSonarQubeConnectionDto, TransientSonarCloudConnectionDto> transientConnection;

  private final List<String> projectKeys;

  public GetProjectNamesByKeyParams(Either<TransientSonarQubeConnectionDto, TransientSonarCloudConnectionDto> transientConnection, List<String> projectKeys) {
    this.transientConnection = transientConnection;
    this.projectKeys = projectKeys;
  }

  public GetProjectNamesByKeyParams(TransientSonarQubeConnectionDto transientConnection, List<String> projectKeys) {
    this(Either.forLeft(transientConnection), projectKeys);
  }

  public GetProjectNamesByKeyParams(TransientSonarCloudConnectionDto transientConnection, List<String> projectKeys) {
    this(Either.forRight(transientConnection), projectKeys);
  }

  public Either<TransientSonarQubeConnectionDto, TransientSonarCloudConnectionDto> getTransientConnection() {
    return transientConnection;
  }

  public List<String> getProjectKeys() {
    return projectKeys;
  }
}
