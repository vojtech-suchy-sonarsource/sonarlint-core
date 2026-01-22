/*
ACR-3c7a1e62a7204addaa70c2b035d19d1c
ACR-ae00a489be404fd2891ab75c15577cf2
ACR-4dbc4ee10f474d23a41174ca614ab974
ACR-3621495497774951b7caad266167df39
ACR-168b955791ff4de79743390746535d9b
ACR-caba99f4305e4180b7a9083b6b69213e
ACR-9d4f194115394b9b98eb31dcb2a5fc14
ACR-5f212f4880aa4ba08caee609f1dc66cd
ACR-ffc6a6bd76194ffcba7964d410e8ed3f
ACR-082450a9893e4868b4c16e0f593d83f0
ACR-576111d9f8284c7c94bc23f3e5dac3e8
ACR-fdb9ac1b6ab14cd0a0257687c046183f
ACR-182a44c1c4074244a118fa8cdb95b551
ACR-c8759d1b3a7047dcbff744b45907b9bf
ACR-060a1fb2edda4dd293ba37f261aba3f2
ACR-7bfcb2b5c492414cb16a436c8aa174bc
ACR-ec93d039d13a4b2b9a64952805e12fbc
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
