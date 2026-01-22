/*
ACR-fe02dc9d0bf843b6aed41141504401c3
ACR-36cb391e22d1455ba06a9e2d6d4dadce
ACR-73502dd8fb034feea93ce2e2e169419c
ACR-44d285ae2d3840c9baaabcd6aa73e6da
ACR-67ec1f250cb64e1db5822824898938f5
ACR-ec8b154275124152bc1ab8592eb5ea69
ACR-c1fb9724186f4af78bec339a03b4f753
ACR-372547e857d94fa9b86a7547e926214c
ACR-c3b637eae3e94ba087fc78df45e0ea4a
ACR-cfc852f31eb44a9b914452e82e3e6096
ACR-d3601ad556e34b909d1566086fff52a9
ACR-34aeefc94b0c458f998a429b8a5f82f6
ACR-33e678d6c7bb4094aca51f012148c9fe
ACR-95fcf798c82e465bbe16d2d0d3855063
ACR-434eaf77dd2044d29f879fa5d64decca
ACR-0e0826a624c547e99932a4c2d701fecf
ACR-e4a386c1e1e6472cb7ab1902a458b405
 */
package org.sonarsource.sonarlint.core.analysis;

import java.util.stream.Stream;
import org.sonar.api.batch.fs.InputFile;
import org.sonarsource.sonarlint.core.analysis.api.ClientInputFile;
import org.sonarsource.sonarlint.core.analysis.api.ClientModuleFileSystem;
import org.sonarsource.sonarlint.core.fs.ClientFileSystemService;


public class BackendModuleFileSystem implements ClientModuleFileSystem {

  private final ClientFileSystemService clientFileSystemService;
  private final String configScopeId;

  public BackendModuleFileSystem(ClientFileSystemService clientFileSystemService, String configScopeId) {
    this.clientFileSystemService = clientFileSystemService;
    this.configScopeId = configScopeId;
  }

  @Override
  public Stream<ClientInputFile> files(String suffix, InputFile.Type type) {
    return files()
      .filter(file -> file.relativePath().endsWith(suffix))
      .filter(file -> file.isTest() == (type == InputFile.Type.TEST));
  }

  @Override
  public Stream<ClientInputFile> files() {
    return this.clientFileSystemService.getFiles(configScopeId).stream().map(BackendInputFile::new);
  }

}
