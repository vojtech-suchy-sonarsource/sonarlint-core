/*
ACR-33347615269248429710ace852689844
ACR-230ed4b7c70d480b9504b5fdd5937729
ACR-e3f0583fa1744b41b8c16aef7c6c966c
ACR-3d8d5febb17a4cd68ebd32bd7cdbec50
ACR-eb697fbc751c486d9663ba3caf011934
ACR-7ae0b54ef25b48da81da43cb8c18ff45
ACR-3cf3cf0879ac430ba15d7a7e1fd535cb
ACR-d40ad32435f44fd69dce9970e0740475
ACR-ad8d3983a69c431c8ddf1d77e72a9832
ACR-faff2110c3b8412ab8a833a07c863a57
ACR-1b25ee3574bd45abaaba6468de06f548
ACR-ae4f3698d54e49e5b51beec2e365a470
ACR-5ad245c062814ee392e6fa327cec77bf
ACR-0cb2788fab1e4ec993ef75a678172d90
ACR-3d5a4ec3eed64c87afd32d4430acf7a5
ACR-fb51db9bd03e419da600629c7b78351b
ACR-ae052583b34349fea6eaabc0a999dc72
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
