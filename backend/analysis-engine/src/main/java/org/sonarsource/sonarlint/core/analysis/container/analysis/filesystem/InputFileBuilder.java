/*
ACR-d029321bb22840039564572c10975169
ACR-1d57b9b9807b424783012e50d57664bc
ACR-d2d5acb7626843b2be2232576725af73
ACR-75ab645f1cf84a4db1c8e35b9be3ee5b
ACR-ad0f248276814cf4a6874e211abab991
ACR-447e161ad2894158b382742914025595
ACR-a1e27a0e9e104beeb943cdb8048e55e1
ACR-38b36fb1a65142079466f59b9c2f497c
ACR-e74949e94a784f50aeb483f9a5ebde7a
ACR-f735dbd24bd842d2a97ff2797f3b81ec
ACR-5d043bda8b90409c95efa2869a9a8033
ACR-9b2854e089c34c20a429a77788846331
ACR-ba156d3640354be8a542ad1cea2009f6
ACR-4472d5a12d11442b8c7d17b3ff4c03a2
ACR-a8eb9c2e13274efdb815ea31960da121
ACR-0463a972192344929c30563a4f508dd0
ACR-8de6b94c502b48b585bb8cb0bc6e472d
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
