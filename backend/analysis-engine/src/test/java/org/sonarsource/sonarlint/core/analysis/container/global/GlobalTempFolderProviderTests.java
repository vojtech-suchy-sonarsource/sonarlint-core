/*
ACR-cd99963a90524374be54b92522d08a0b
ACR-927f4c3e9fe34fd9a2d96ff7283f9634
ACR-7ecb2ccdecbc467ba530d25d305c7520
ACR-0b480a45914b4a1d820a1af327e455b0
ACR-c7e9b56353164eb09c3b7390411ab64a
ACR-31342fe151ae4c0085cb79f38ec13362
ACR-e1a68d2470be4383bdf7af327f4086ce
ACR-acfe45f1dbdd4530b8de0b2b120d349d
ACR-b0680c0b4f6045e7824f8e2a058d4aaf
ACR-bb785c085459434d8563035ee1b2156a
ACR-0e381b124c9f401099e50c62c4d96924
ACR-8cd2a9c3c0fe4863b87eeb53d89e240b
ACR-de2732e50f304e4089309cf6b270e78f
ACR-e00ca452701d4135b73619202d9477e2
ACR-9f6a87367e1a44d3a6537a23b72b647b
ACR-7a907038d8bb40b784c98b9fad652370
ACR-f9a2767c554c40cb83100fc565ce1ce2
 */
package org.sonarsource.sonarlint.core.analysis.container.global;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.sonar.api.utils.TempFolder;
import org.sonarsource.sonarlint.core.analysis.api.AnalysisSchedulerConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalTempFolderProviderTests {

  @TempDir
  private Path workingDir;

  private final GlobalTempFolderProvider tempFolderProvider = new GlobalTempFolderProvider();

  @Test
  void createTempFolderProps() throws Exception {

    TempFolder tempFolder = tempFolderProvider.provide(AnalysisSchedulerConfiguration.builder().setWorkDir(workingDir).build());
    tempFolder.newDir();
    tempFolder.newFile();
    assertThat(getCreatedTempDir(workingDir)).exists();
    assertThat(getCreatedTempDir(workingDir).list()).hasSize(2);

    FileUtils.deleteQuietly(workingDir.toFile());
  }

  @Test
  @Disabled("SLCORE-821")
  void cleanUpOld() throws IOException {
    var creationTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(100);

    for (var i = 0; i < 3; i++) {
      var tmp = new File(workingDir.toFile(), ".sonarlinttmp_" + i);
      tmp.mkdirs();
      setFileCreationDate(tmp, creationTime);
    }

    tempFolderProvider.provide(AnalysisSchedulerConfiguration.builder().setWorkDir(workingDir).build());
    //ACR-31dff8f1c43d48b8bef406a6a221b28a
    assertThat(getCreatedTempDir(workingDir)).exists();

    FileUtils.deleteQuietly(workingDir.toFile());
  }

  private File getCreatedTempDir(Path workingDir) {
    assertThat(workingDir).isDirectory();
    assertThat(workingDir.toFile().listFiles()).hasSize(1);
    return workingDir.toFile().listFiles()[0];
  }

  private void setFileCreationDate(File f, long time) throws IOException {
    var attributes = Files.getFileAttributeView(f.toPath(), BasicFileAttributeView.class);
    var creationTime = FileTime.fromMillis(time);
    attributes.setTimes(creationTime, creationTime, creationTime);
  }
}
