/*
ACR-745f77cacf3c41bd87b9d9f46e1cb31b
ACR-ff6a19ca4a3c4c6ab24245c84d9c9d2f
ACR-57a45a1113be4c20918d6fc986244dd5
ACR-7f7e67ac89c54f38854f74579dff898f
ACR-e04aaac3be054631bedc778518271fa2
ACR-f764c2e0904e4f13988dc69c73026040
ACR-252b3b50ae09470c8af41219cde75ce8
ACR-bcd9fcfa81be4a1ea47a18d07332989f
ACR-e0a00c42604e4bfbb7dde39a980e2b2d
ACR-645842abe8414cc8b160eede73226054
ACR-1b9f535cf80b4662bddf851f7a1a6bfc
ACR-275855202ad443c0859f83eaabb8a36a
ACR-58c2521d737044beac124baa2bd8f194
ACR-422727af96ae429bb6cd92b5020166a3
ACR-2c5d8310fa5746dc8e2b60d29991b499
ACR-907cecf7ba8e49f5be9d6ff49335ef71
ACR-bc8da8a873164654a8aeb35beac3e93c
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
