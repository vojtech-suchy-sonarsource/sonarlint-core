/*
ACR-632b8b41799742b596ebdc1022e74194
ACR-f6ec474852ba4db7a56b399360f3b0ec
ACR-6e925af751f64f7ebc936faf6cf77d67
ACR-c6d0c4bfc891402295c3bae1c9c2b507
ACR-431d864447a1451aaca6dcbfe932cc89
ACR-5688187985534275acda1c3d20a5c093
ACR-e695a589785741fb8d908013fe23daf9
ACR-a93ee892b03448108740b56d9735f805
ACR-32c156578fc24ae9ab58cdd0f69bad21
ACR-14215035d5de4d45b9cff1b5b2771c68
ACR-68e00f67e2cd46da9b683a9485b72fa1
ACR-10883970c2034e158addd552c3b57a76
ACR-4ab068ff5596430f990ceee1e2351511
ACR-2c56920fe72e4b31a61eb7da3579d804
ACR-f09fbbb476954ab0936ca786dbeeed13
ACR-8c194a33bf8647ef94455ab851a6cd11
ACR-7579dfb50c214b16a75302fc2dd6d7df
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
