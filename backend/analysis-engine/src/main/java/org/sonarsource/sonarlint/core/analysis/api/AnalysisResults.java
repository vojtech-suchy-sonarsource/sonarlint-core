/*
ACR-5de210100c1041ca9d00f78625a2e63e
ACR-187672630aba481d98531cd261516073
ACR-621cf30485024055a85f334503f1aedd
ACR-06bfed9ec48c4d78883c100d4b962b45
ACR-6baf4c9a0dd846669eff150cf250d3f5
ACR-6c7c382c426344d2b401f2d6440bd658
ACR-5a54c514980844e08a15fcf4bbbae717
ACR-1ff4fa984d6c43c7b2a5fb80bebf8334
ACR-fa73dbaa340c4a79b4a6768abca74d3d
ACR-b11c901126764551b0f1179e1902e645
ACR-cc92a4009cb44e5eb68fec23383a80b9
ACR-7b3a4c1ebefa4755b6e33517bcf4a32f
ACR-d9e980c74fd1466a8dfae3d4de4c6afc
ACR-d624dde7487043ab9f888057008dffb6
ACR-6e54ff6f54ee4743954e8a784275cc54
ACR-bf0702cc5fb646af8b19eef305661236
ACR-0edf835c2d5e43f5a3ea8d3f9c493c39
 */
package org.sonarsource.sonarlint.core.analysis.api;

import java.time.Duration;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;

public class AnalysisResults {
  private final Set<ClientInputFile> failedAnalysisFiles = new LinkedHashSet<>();
  private final Map<ClientInputFile, SonarLanguage> languagePerFile = new LinkedHashMap<>();
  private Duration duration = Duration.ZERO;

  public void addFailedAnalysisFile(ClientInputFile inputFile) {
    failedAnalysisFiles.add(inputFile);
  }

  /*ACR-d17ddf45209e42e193e38e80f266970f
ACR-07029e5ad3bf4fcb855fd1d7ec5048a7
ACR-167c850f501f400cb2dde2529a30a5a4
   */
  public Map<ClientInputFile, SonarLanguage> languagePerFile() {
    return languagePerFile;
  }

  public void setLanguageForFile(ClientInputFile file, @Nullable SonarLanguage language) {
    this.languagePerFile.put(file, language);
  }

  /*ACR-ae67fc4784ad4990ac7a7c79007ec884
ACR-ccf141f948a349e28317b12b33d4eb5b
ACR-8b013af408ae499ba6125025805b2393
   */
  public Collection<ClientInputFile> failedAnalysisFiles() {
    return failedAnalysisFiles;
  }

  public Duration getDuration() {
    return duration;
  }

  public void setDuration(Duration duration) {
    this.duration = duration;
  }
}
