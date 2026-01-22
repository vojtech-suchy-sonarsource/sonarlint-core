/*
ACR-18645c8b22674844853870c73a351ee0
ACR-e544a91933fc4cdbb17d7f59a110ebc9
ACR-f9f5c9cbe6a74921beb0112455eac256
ACR-89cf57ad57494b89b32f836d85ed0ced
ACR-76f5a4ea74c84489a698de4b1cc4639d
ACR-de9a465ce3be412e947480ade8839294
ACR-c300d3c8546f4aa199194fcd9b5a2aed
ACR-3ea1565931784cc8bd8dab5d271d7b38
ACR-743f74bab93242a8b6c717c68a0e6336
ACR-d9587c6e70f24876b9999d2914e8a596
ACR-512518493671430491848817e9e02131
ACR-bfa0b02378154da792889f79c67708a3
ACR-16b850ca42c042dab6966ced4e4caf43
ACR-2f738e8d1445433da1ea9110fc237eef
ACR-6a1dd2d2690947afa75e36aa5ce68432
ACR-ee49fa5d5a7b4babb20268dd14591abb
ACR-5f03bdd44879419dacd9479c960b8672
 */
package org.sonarsource.sonarlint.core.analysis.container.module;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.sonar.api.batch.fs.InputFile.Type;
import org.sonarsource.sonarlint.core.analysis.api.ClientInputFile;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.FileMetadata;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.LanguageDetection;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.SonarLintInputFile;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

public class ModuleInputFileBuilder {
  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private final LanguageDetection langDetection;
  private final FileMetadata fileMetadata;

  public ModuleInputFileBuilder(LanguageDetection langDetection, FileMetadata fileMetadata) {
    this.langDetection = langDetection;
    this.fileMetadata = fileMetadata;
  }

  public SonarLintInputFile create(ClientInputFile inputFile) {
    var defaultInputFile = new SonarLintInputFile(inputFile, f -> {
      LOG.debug("Initializing metadata of file {}", f.uri());
      var charset = f.charset();
      InputStream stream;
      try {
        stream = f.inputStream();
      } catch (IOException e) {
        throw new IllegalStateException("Failed to open a stream on file: " + f.uri(), e);
      }
      return fileMetadata.readMetadata(stream, charset != null ? charset : Charset.defaultCharset(), f.uri(), null);
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
