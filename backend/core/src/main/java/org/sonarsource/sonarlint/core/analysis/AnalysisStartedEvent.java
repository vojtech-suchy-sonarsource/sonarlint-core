/*
ACR-98f4859ba6984404b774728a7a9d54e7
ACR-f70ab284be3a4b15b2ad9bccef044053
ACR-b4e00b3ddfe04364ba28d5d705ec32ec
ACR-316b0be7781c4ae08b0f7d42f4815aaa
ACR-8185446cda854b38877f4f6810e1712e
ACR-b281f5775f56458cae98112aa28ef6a3
ACR-549759fa8e5f4bc1baf7e3fb3f3328a7
ACR-eaa5e6ed24344fa89b8b012230276220
ACR-b4c78c385bff49f7841c4283f68cfb34
ACR-21513c3711cb40daaa5a6cc5545003b2
ACR-ff70f4c15ed24b49b998d3303fc2aed2
ACR-69cd8fab728446af81b8a84b1b9008e6
ACR-7c0176137fdb4de7bed56b04b5b34d5b
ACR-0ebda0a5b67d454db5fb1fce79b59c84
ACR-098f35de3e0442b696fbf45c1f9d15cb
ACR-adb4ab5914734d179921b5b982eded0f
ACR-dfe190f0a0734f1bb07cad7d7f03f48f
 */
package org.sonarsource.sonarlint.core.analysis;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.util.stream.StreamSupport;
import org.sonarsource.sonarlint.core.analysis.api.ClientInputFile;

import static java.util.stream.Collectors.toSet;

public class AnalysisStartedEvent {
  private final String configurationScopeId;
  private final UUID analysisId;
  private final List<ClientInputFile> files;

  public AnalysisStartedEvent(String configurationScopeId, UUID analysisId, Iterable<ClientInputFile> files) {
    this.configurationScopeId = configurationScopeId;
    this.analysisId = analysisId;
    this.files = StreamSupport.stream(files.spliterator(), false).toList();
  }

  public UUID getAnalysisId() {
    return analysisId;
  }

  public String getConfigurationScopeId() {
    return configurationScopeId;
  }

  public Set<Path> getFileRelativePaths() {
    return files.stream().map(ClientInputFile::relativePath).map(Path::of).collect(toSet());
  }

  public Set<URI> getFileUris() {
    return files.stream().map(ClientInputFile::uri).collect(toSet());
  }

  public UnaryOperator<String> getFileContentProvider() {
    return path -> files.stream()
      .filter(ClientInputFile::isDirty)
      .filter(clientInputFile -> clientInputFile.relativePath().equals(path))
      .findFirst()
      .map(AnalysisStartedEvent::getClientInputFileContent)
      .orElse(null);
  }

  private static String getClientInputFileContent(ClientInputFile clientInputFile) {
    try {
      return clientInputFile.contents();
    } catch (IOException e) {
      return "";
    }
  }
}
