/*
ACR-378458d6f16d41b4882d9d7e8881ecad
ACR-9d1bfd0cf02a446498bb16837b2d1c63
ACR-e969752f77c349ca91186fb3f1063b7a
ACR-dafaad60241345059bb545bc02848cf0
ACR-188189ddd1bd4944994b1f54eaf7dd92
ACR-047ffe45475f4c0da8f70c9734fb4bf4
ACR-92224066bbdd4138968c63be8feba1a2
ACR-34983662aba142dc8dfbc3679a5abfe5
ACR-17b495572ad0483e848e19857898c9d3
ACR-48fc7091c07a4bcd9f05456c529efc7b
ACR-bbf9cd40e4f04318b6bd496823a760de
ACR-af09d339fa2041c5bb92fc9176b5e6dc
ACR-abf5c97eabeb4adebf64f554b09b9720
ACR-bd4fe2f1527340afb4a581bd7ee89b68
ACR-5f6de9b89bad471db5cc9a61dc78ec42
ACR-9f2c068f5dc049f4981121586807085a
ACR-ef2e00c2c4cf4d9aae1218eb3c717574
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
