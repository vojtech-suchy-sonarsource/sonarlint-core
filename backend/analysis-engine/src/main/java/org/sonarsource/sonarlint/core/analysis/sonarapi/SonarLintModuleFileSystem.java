/*
ACR-a944ff4870894cd9b2eaba368a642daf
ACR-84eb8864d2814d7b9d807246faf744ea
ACR-1c8f523b5d0c41dbb5e79765b442e36c
ACR-635c8b8ab8f940aab7de815e00198fbe
ACR-da033a902d1f4075b89ec3adf30c521b
ACR-109bbd91464c495b98bb7a2b4357184d
ACR-ff013816d74f404b98d9e3d60012c45a
ACR-3ff9542799f244c896d2c4229be87024
ACR-6710ca1067cf4c468f14571f4fc89bff
ACR-d8b58b3d394d4596bbc0d9e08421b68a
ACR-2beea9bbe5f340f79dd55c20a2867f85
ACR-20aa9944f4a94b468f2e7e9c09a4b65b
ACR-44e2a92294004881b754702652ea21e1
ACR-6b5e0b8b568e41ccb447a94b856c2b31
ACR-4caead315b4e4f5c932f45ced3c8ef17
ACR-8d83a2272d2c400982ecf119af22b9bf
ACR-83af0c59fc364cf0a394420c02966bdd
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi;

import java.util.stream.Stream;
import org.sonar.api.batch.fs.InputFile;
import org.sonarsource.sonarlint.core.analysis.api.ClientModuleFileSystem;
import org.sonarsource.sonarlint.core.analysis.container.module.ModuleInputFileBuilder;
import org.sonarsource.sonarlint.plugin.api.module.file.ModuleFileSystem;

public class SonarLintModuleFileSystem implements ModuleFileSystem {

  private final ClientModuleFileSystem clientFileSystem;
  private final ModuleInputFileBuilder inputFileBuilder;

  public SonarLintModuleFileSystem(ClientModuleFileSystem clientFileSystem, ModuleInputFileBuilder inputFileBuilder) {
    this.clientFileSystem = clientFileSystem;
    this.inputFileBuilder = inputFileBuilder;
  }

  @Override
  public Stream<InputFile> files(String suffix, InputFile.Type type) {
    return clientFileSystem.files(suffix, type)
      .map(inputFileBuilder::create);
  }

  @Override
  public Stream<InputFile> files() {
    return clientFileSystem.files()
      .map(inputFileBuilder::create);
  }
}
