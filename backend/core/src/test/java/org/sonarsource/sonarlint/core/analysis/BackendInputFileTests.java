/*
ACR-92cbd39f67934cf0a6a0975564943405
ACR-54a13e4c92644a9dab27567f9a18d2bc
ACR-5c673d966b0b41b7b1c5652561ce38b5
ACR-3841cf9836bd4abf980efc5a4581457c
ACR-be48e1847f644496bd2fc85f83e36e0a
ACR-4842c5d9750c47c09a7f65a991ea7c51
ACR-5eadc143e78c46cca0a8e9f470c9daf9
ACR-1ad78423a53d44d9b5a7febee6e61358
ACR-adc48e22d88440f1a8ff17fc2bcc2a96
ACR-30d3ee1cbde841f792f991a294282481
ACR-40d1f8f7e6af4c8488ce1e0c5891b029
ACR-60cd0a91af5146eaac634fc6b10a790a
ACR-607c0d4ca85f48ee8e9535a44e23e412
ACR-c46de710d9764667a8e7886157127f08
ACR-66fc4a5ea1294cf4a06bb212ffb74649
ACR-33b447fae2af4cf99fc4e96f04521d14
ACR-d33a9fe188fb443c9a060a3877968c27
 */
package org.sonarsource.sonarlint.core.analysis;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.fs.ClientFile;

import static org.assertj.core.api.Assertions.assertThat;

class BackendInputFileTests {

  @Test
  void ascii_path_should_be_the_same() {
    var path = Path.of("/test/file.php");
    var pathAsString = path.toString().replace(File.separatorChar, '/');
    var clientFile = new ClientFile(URI.create("file://" + pathAsString), "configScopeId", path, false, null, null, null, true);

    var inputFile = new BackendInputFile(clientFile);

    assertThat(inputFile.getPath().replace(File.separatorChar, '/')).endsWith(pathAsString);
  }

  @Test
  void non_ascii_path_should_be_the_same() {
    var path = Path.of("/中文字符/file.php");
    var pathAsString = path.toString().replace(File.separatorChar, '/');
    var clientFile = new ClientFile(URI.create("file://" + pathAsString), "configScopeId", path, false, null, null, null, true);

    var inputFile = new BackendInputFile(clientFile);

    assertThat(inputFile.getPath().replace(File.separatorChar, '/')).endsWith(pathAsString);
  }

}
