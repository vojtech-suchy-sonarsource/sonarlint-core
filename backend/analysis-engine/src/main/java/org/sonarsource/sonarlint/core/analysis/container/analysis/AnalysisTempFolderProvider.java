/*
ACR-94599fe503c54fd39805add98e8a4999
ACR-b747f5d257b7490da053c0a71867fe93
ACR-21f634d5fa4f41dc85326b838de07d65
ACR-ce0f3b3835da4e668b22c8e11b387b2f
ACR-1695b026133f40188034cd6f4754cee4
ACR-f87b3f6813d04d70b22fa9d85ccc1d26
ACR-550f04e003114779b3dc55b64949defd
ACR-51a674ac9ee04a4db5ae9d98670d857b
ACR-375e4c35f7e2409ca7c4c0361005f473
ACR-640b7eda9a304890b8bf94f6ec102604
ACR-410dab45dc7145dc89b90ef0761e142c
ACR-2ae1b15b86be4b77a16351a97fb43004
ACR-24dedb0e0fbc476488d66354bfd80800
ACR-5b4c074260a64303adfb9a2d4d1ab05e
ACR-47e7b2ca08624ff1ad4c8f62f28fd80a
ACR-ad02729e915e4ab08f6a621073d8dd85
ACR-3a5caca80d5c415aa141e65c8bb0d082
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
