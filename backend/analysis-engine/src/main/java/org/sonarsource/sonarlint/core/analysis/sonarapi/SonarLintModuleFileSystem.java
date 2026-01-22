/*
ACR-caadeb59bde644d68d8e2801fe29922a
ACR-a3bc056b5d1f4fa8821732a7766073a6
ACR-142e6728e8484932ab18845271b773b7
ACR-403e481732af4b969b65f342d542b6b3
ACR-71fa6b5c1a7048b1bb610c53ea6aa917
ACR-6855bb5dac314c12818b39a9aa2306eb
ACR-fea27a4769de4ba9a9ed959c4f165732
ACR-d8ee8284cb6f43218fa9d5d311cc7109
ACR-0ac483ddf15343718e11163da55e3998
ACR-6982b5732b114d428cd2940f12e1b45a
ACR-89a566f488a04ec59ca930d36e0c26fb
ACR-02cdbf89ae194370855a26035e82c018
ACR-254f7a775688409b9d49d9c0f4a53a88
ACR-6df86470e0fe475ba4226ce5b54fb06d
ACR-fc911e21b31d434dbd7e956dd7734b4a
ACR-1fc5c287d0124291af51fae88dd2b0cd
ACR-a106478b7b3e43959d7cee5588915bf8
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
