/*
ACR-0aa06927f3d04d73926597016ce307d7
ACR-fe14bf8d2ea5452eb25d5edf6c8b7926
ACR-1d3ea28800cf468daae9332dba43e42f
ACR-3fdab06c21ad4ad8b850f3718a8277e1
ACR-07657363e0c440bfb37fc8d8b954b6e3
ACR-fe669d5345044be9b1c60090af4643e4
ACR-90be3b1303da4952a441e70c3b95ab5d
ACR-7870de861c8d4062b4c48713450a9d16
ACR-8a4d697e1c984d03bd602544f4a6c0f9
ACR-8fbc16ac7b4940db8c89038684daab51
ACR-6e89794bc80e45e58339d33736a90ed6
ACR-68b8cba50179404f8f156b02dbc61ca8
ACR-ecfeaa63cfd7422e99cf80a30a46233b
ACR-a5851e2561244265a3f21bdc9c466377
ACR-982b95a5cff94a53a4e1040f4f063314
ACR-a5ee79df63244abc9bfac802cdaf7868
ACR-4b9c723f9acb4fbe921939d6cac0d11d
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

  /*ACR-0776a27d2a454b1cbdaac9854162bbda
ACR-80f290796e8c48be8ca0f5bcc4775932
ACR-c9712b1197d4427190f53ab3af05d85a
   */
  public Map<ClientInputFile, SonarLanguage> languagePerFile() {
    return languagePerFile;
  }

  public void setLanguageForFile(ClientInputFile file, @Nullable SonarLanguage language) {
    this.languagePerFile.put(file, language);
  }

  /*ACR-5848a19fe2254a3a8db050e2db90d2b2
ACR-fa8c175703134ef9b0071196d28e3f55
ACR-2e51b44c66ea481f991559f85f611186
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
