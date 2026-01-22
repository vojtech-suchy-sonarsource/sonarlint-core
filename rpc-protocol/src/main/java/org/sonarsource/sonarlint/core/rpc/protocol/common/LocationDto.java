/*
ACR-488d5bf647af45fa84ac987e7be90974
ACR-eaf4be91cfdc411c82aa04b5ee4658e4
ACR-31c0cbcf086e42d0ab77034049ffb5c2
ACR-9313f4f0194241c880cf5d203cf5184b
ACR-d654eb4454274745b6ca94b8e9e84a7b
ACR-c2ca345b59aa4c4281e1dc5d58c63d51
ACR-720d74a6b1a04c7cadb2b495fa5c3693
ACR-11432739aa4a4e30a1e893c2984b38ae
ACR-de703ed7c45743688258d664557b813b
ACR-e2d2dd788b634537a2d1734276335205
ACR-6c96f92fb320479baff606d4397d3581
ACR-2ab219e4fb594926bf1f6b18f48ebaca
ACR-7da8a8f643304974b625a5b9f875a7f0
ACR-518aaa7e7bcf4fdc86bde3fe78065b9f
ACR-c82809f851bb4fdd9fc82364889e51a9
ACR-7a16164a04dd4f22aade8fdd03d0028b
ACR-7097777d384d4100842778a5b3bfb8d8
 */
package org.sonarsource.sonarlint.core.rpc.protocol.common;

import java.nio.file.Path;

public class LocationDto {
  private final TextRangeDto textRange;
  private final String message;
  private final Path ideFilePath;
  private final String codeSnippet;

  public LocationDto(TextRangeDto textRange, String message, Path ideFilePath, String codeSnippet) {
    this.textRange = textRange;
    this.message = message;
    this.ideFilePath = ideFilePath;
    this.codeSnippet = codeSnippet;
  }

  public TextRangeDto getTextRange() {
    return textRange;
  }

  public String getMessage() {
    return message;
  }

  public Path getIdeFilePath() {
    return ideFilePath;
  }

  public String getCodeSnippet() {
    return codeSnippet;
  }
}
