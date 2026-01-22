/*
ACR-76c72b8ffd3144368869876ef61e467d
ACR-dbc19ba29505466bb28d3bccf3d1aaeb
ACR-ebb4eba94edd42cf9fb4a891f84942b7
ACR-a83e88e54ccd4c57a0eb10a38821413e
ACR-910115c63d01401894ad18e50805aa69
ACR-2f27d938834e4961a3a71aa4ed044295
ACR-cd8387e35107424481a4a9436263700c
ACR-2766ac55d26841268ad3d4da28b875da
ACR-94260d3c22e04842a62451cf27db56dc
ACR-e9140fa9bc16488e81c3cd7bab15eb5c
ACR-614a3331c169498e8e1b4440bf658792
ACR-8c68e937444a42018f567e4fe0706041
ACR-7580bb5eec3e4ef084694e503af342a8
ACR-064e048fd079446eba0a61cac6de4bda
ACR-d38a00daaab64247abf1198214c967e4
ACR-14ec72438fa44439969226376ae1758b
ACR-31e43368492247fcbe56171cc07b7d55
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.sonarsource.sonarlint.core.commons.NewCodeDefinition;
import org.sonarsource.sonarlint.core.commons.NewCodeMode;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.serverconnection.FileUtils;
import org.sonarsource.sonarlint.core.serverconnection.proto.Sonarlint;

import static org.sonarsource.sonarlint.core.serverconnection.storage.ProtobufFileUtil.writeToFile;

public class NewCodeDefinitionStorage {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  public static final String NEW_CODE_DEFINITION_PB = "new_code_definition.pb";

  private final Path storageFilePath;
  private final RWLock rwLock = new RWLock();

  public NewCodeDefinitionStorage(Path rootPath) {
    this.storageFilePath = rootPath.resolve(NEW_CODE_DEFINITION_PB);
  }

  public void store(NewCodeDefinition newCodeDefinition) {
    FileUtils.mkdirs(storageFilePath.getParent());
    var newCodeDefinitionToStore = adapt(newCodeDefinition);
    LOG.debug("Storing new code definition in {}", storageFilePath);
    rwLock.write(() -> writeToFile(newCodeDefinitionToStore, storageFilePath));
  }

  public Optional<NewCodeDefinition> read() {
    return rwLock.read(() -> Files.exists(storageFilePath) ?
      Optional.of(adapt(ProtobufFileUtil.readFile(storageFilePath, Sonarlint.NewCodeDefinition.parser())))
      : Optional.empty());
  }

  static Sonarlint.NewCodeDefinition adapt(NewCodeDefinition newCodeDefinition) {
    var builder = Sonarlint.NewCodeDefinition.newBuilder()
      .setMode(Sonarlint.NewCodeDefinitionMode.valueOf(newCodeDefinition.getMode().name()));
    if (newCodeDefinition.getMode() == NewCodeMode.NUMBER_OF_DAYS) {
      var newCodeNumberOfDays = (NewCodeDefinition.NewCodeNumberOfDaysWithDate) newCodeDefinition;
      builder.setDays(newCodeNumberOfDays.getDays());
    }
    if (newCodeDefinition.getMode() != NewCodeMode.REFERENCE_BRANCH) {
      var newCodeDefinitionWithDate = (NewCodeDefinition.NewCodeDefinitionWithDate) newCodeDefinition;
      builder.setThresholdDate(newCodeDefinitionWithDate.getThresholdDate().toEpochMilli());
    } else {
      var newCodeReferenceBranch = (NewCodeDefinition.NewCodeReferenceBranch) newCodeDefinition;
      builder.setReferenceBranch(newCodeReferenceBranch.getBranchName());
    }
    if (newCodeDefinition.getMode() == NewCodeMode.PREVIOUS_VERSION) {
      var newCodePreviousVersion = (NewCodeDefinition.NewCodePreviousVersion) newCodeDefinition;
      var version = newCodePreviousVersion.getVersion();
      if (version != null) {
        builder.setVersion(version);
      }
    }
    return builder.build();
  }

  static NewCodeDefinition adapt(Sonarlint.NewCodeDefinition newCodeDefinition) {
    var thresholdDate = newCodeDefinition.getThresholdDate();
    var mode = newCodeDefinition.getMode();
    switch (mode) {
      case NUMBER_OF_DAYS:
        return NewCodeDefinition.withNumberOfDaysWithDate(newCodeDefinition.getDays(), thresholdDate);
      case PREVIOUS_VERSION:
        var version = newCodeDefinition.hasVersion() ? newCodeDefinition.getVersion() : null;
        return NewCodeDefinition.withPreviousVersion(thresholdDate, version);
      case REFERENCE_BRANCH:
        return NewCodeDefinition.withReferenceBranch(newCodeDefinition.getReferenceBranch());
      case SPECIFIC_ANALYSIS:
        return NewCodeDefinition.withSpecificAnalysis(thresholdDate);
      default:
        throw new IllegalArgumentException("Unsupported mode: " + mode);
    }
  }
}
