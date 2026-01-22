/*
ACR-67e604b86a2242dfb18c2aa941c5b3a5
ACR-7369745da80d4854a5534af533c977e6
ACR-0c1cab11a73b48d389c8ab993604ab29
ACR-a3698004f855447abc65a1790a3aa764
ACR-69636556a58b4e2e936548f8b90ba41d
ACR-5adf87928d6a424ab5366b477829fc8f
ACR-f4de71926c6b49f68091cf4d147d9b27
ACR-b9c5d253062c48958dce16dcac56119c
ACR-35dbb51be5994d5ba140120379296893
ACR-e903253731a247338cb469c4c86690f3
ACR-0b8a279519864294b01ef42dfcfc65ab
ACR-87a801c3f2b547108da83d89244438b3
ACR-ac1ec00916c746888efde59962be8075
ACR-8be8869e0b564bc2881f0b29b51f7ebf
ACR-c5b6b98dec34438b800ff50e7a47aefe
ACR-2a33f623ed014f118b1c697d9d110e72
ACR-53b3c7d360604f158fe3242ed544f545
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
