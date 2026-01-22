/*
ACR-9ab74f59e40b401cad09881fcaa88027
ACR-1e62b8cd95b644be9acfec3160e8d751
ACR-83ce1aa81f5c4dd18d41cdd91761896b
ACR-4a3fad23f1814d7cbd889309291f5ad1
ACR-30c396c8865c4e09a90c25857da85763
ACR-d36534b940ce4b478242418e668da4dc
ACR-4dd81e150e6e44788449891961963482
ACR-2b16547f121e4085b95354845e8cf6ce
ACR-5764fbc3601943d686f13bf0420020f1
ACR-7d1ddaf1ed8c42a0a6a56825a255293a
ACR-c16e0b69521844c79806281a1f1a5220
ACR-9a1101e706be4148831c91397607c291
ACR-efc9ea7cae4b4347b956130af5b30bbb
ACR-d2825ef3af3645539c5ab17e59f132c0
ACR-09239edb89144582899979a03f7e82d1
ACR-dcdfbeba1ad84d25a7e9a07aab495661
ACR-516d9e95a3c74390bc221be35436d433
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.sonar.api.batch.fs.InputFile.Type;
import org.sonarsource.sonarlint.core.analysis.api.ClientInputFile;
import org.sonarsource.sonarlint.core.analysis.container.analysis.issue.ignore.scanner.IssueExclusionsLoader;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

public class InputFileBuilder {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private final LanguageDetection langDetection;
  private final FileMetadata fileMetadata;
  private final IssueExclusionsLoader exclusionsScanner;

  public InputFileBuilder(LanguageDetection langDetection, FileMetadata fileMetadata, IssueExclusionsLoader exclusionsScanner) {
    this.langDetection = langDetection;
    this.fileMetadata = fileMetadata;
    this.exclusionsScanner = exclusionsScanner;
  }

  SonarLintInputFile create(ClientInputFile inputFile) {
    var defaultInputFile = new SonarLintInputFile(inputFile, f -> {
      LOG.debug("Initializing metadata of file {}", f.uri());
      var charset = f.charset();
      InputStream stream;
      try {
        stream = f.inputStream();
      } catch (IOException e) {
        throw new IllegalStateException("Failed to open a stream on file: " + f.uri(), e);
      }
      return fileMetadata.readMetadata(stream, charset != null ? charset : Charset.defaultCharset(), f.uri(), exclusionsScanner.createCharHandlerFor(f));
    });
    defaultInputFile.setType(inputFile.isTest() ? Type.TEST : Type.MAIN);
    var fileLanguage = inputFile.language();
    if (fileLanguage != null) {
      LOG.debug("Language of file \"{}\" is set to \"{}\"", inputFile.uri(), fileLanguage);
      defaultInputFile.setLanguage(fileLanguage);
    } else {
      defaultInputFile.setLanguage(langDetection.language(defaultInputFile));
    }

    return defaultInputFile;
  }

}
