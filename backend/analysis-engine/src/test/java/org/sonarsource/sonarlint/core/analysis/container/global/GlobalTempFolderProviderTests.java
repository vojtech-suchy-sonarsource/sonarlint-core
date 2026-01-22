/*
ACR-a5e4cbd8829940ce906dc14e7a4fe79d
ACR-ba759edf5c504311840d2048d07359af
ACR-78689613909c4938b371a123115e378d
ACR-54593bbdd2d34854944e4dd9b05977fa
ACR-322326918de44031b4e6e07505c4ada3
ACR-c29930887aab4acd85825ba196f23ce9
ACR-f9ebe1c295f5425a9296cc3a8b591b4d
ACR-9f2c991434fa4384b589238c2c863bd0
ACR-6ee5a05c884b48688eeccda5979a5490
ACR-40e7bf469d924568864a8e21dbcf2b4f
ACR-38f83efcb91e4decb853f6c07708cd86
ACR-1251bdc772144cf2b652f2ef812ae858
ACR-440616310e8c44bea5271375919d1e98
ACR-ec9f81fedf204520810e29aa9cc763b9
ACR-2da4709e01104e35adc90e2400aed2e3
ACR-3276eef8b9ac45908396c7e9699d9a5f
ACR-087ea5990cf643899aab2bf9b41e12f3
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
    //ACR-fbb7ee826dd04ef0a4b1c961d99d500e
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
