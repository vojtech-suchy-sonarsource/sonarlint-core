/*
ACR-836021a2da764959b170f2abefb8706f
ACR-edba1ac649744b4ab182e47e4bddff67
ACR-9c11b7cddbd04d77b62db94bb5b3debf
ACR-7516f0ccc8b546bf84ce860613530f51
ACR-3e05cdc6374741c8a4ae9dcdc2cdfa91
ACR-d809eab1e59a45e382c0e4a877808459
ACR-d65ce0a021514bf89f26b9310cac56be
ACR-60cc5c7c34b74c1e81a68342781bb96d
ACR-6ef8c0292b78427b97b3c20133589d86
ACR-95847d4fd92c480cb0b760f9f772e014
ACR-081fd23253c1427ab67654716a242fbb
ACR-170ec96659344a12825b9b9ba0f98eaf
ACR-8ff66a0ccf014323895515b86766233d
ACR-b482fed320984fcfaef8e12e45dedd45
ACR-5e989cf24d2c4a5bb0525e056816ef72
ACR-6c85f2f74308438c97931738412f3abf
ACR-d17141492f1348cbaf4cbe3c8dd19ced
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis;

import java.io.File;
import javax.annotation.Nullable;
import org.sonar.api.utils.TempFolder;
import org.springframework.context.annotation.Bean;

public class AnalysisTempFolderProvider {

  private final NoTempFilesDuringAnalysis instance = new NoTempFilesDuringAnalysis();

  @Bean("TempFolder")
  public TempFolder provide() {
    return instance;
  }

  private static class NoTempFilesDuringAnalysis implements TempFolder {

    @Override
    public File newDir() {
      throw throwUOEFolder();
    }

    @Override
    public File newDir(String name) {
      throw throwUOEFolder();
    }

    private static UnsupportedOperationException throwUOEFolder() {
      return new UnsupportedOperationException("Don't create temp folders during analysis");
    }

    @Override
    public File newFile() {
      throw throwUOEFiles();
    }

    private static UnsupportedOperationException throwUOEFiles() {
      return new UnsupportedOperationException("Don't create temp files during analysis");
    }

    @Override
    public File newFile(@Nullable String prefix, @Nullable String suffix) {
      throw throwUOEFiles();
    }

  }
}
