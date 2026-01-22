/*
ACR-ba25237152cd4cb29ed75e2ba28c2283
ACR-526a7cc782784259b5b2df2d5509af83
ACR-4a2172c5941341a8a810992af0462d37
ACR-4ef9df876a124ebeb6e0ea270d69c043
ACR-b01204db84d34a578027054f85668ed0
ACR-950bbb485a6144279892f9069f0dff17
ACR-ea00ee9375094e70bb52f8013107f80d
ACR-7e8319cb77344efe8cfa6b95478b9453
ACR-64aa86e2b8934153b0eeedfc0ee411bf
ACR-be41739117fe4acb9ff54aac6b58382b
ACR-a471ddc0a69443cc9556769b758d6092
ACR-75382c61fc134ddab015277f2a54b7fa
ACR-bd0d66ec51564a4bb8b28f301a770623
ACR-b2566fed1ba14572a24ac23e95a166dd
ACR-f990c911bbcd453ab898ac4a4920ff76
ACR-09ae8ef3a1ea44d09f9ebc6ff6453b0e
ACR-172899586bff40bbaf1512abfd62329b
 */
package org.sonarsource.sonarlint.core.fs;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClientFileTests {

  @Test
  void dirty_file_larger_than_threshold_returns_true() throws Exception {
    var uri = URI.create("file:///dirty.js");
    var clientFile = new ClientFile(uri, "scope", Paths.get("dirty.js"), null, StandardCharsets.UTF_8, null, null, true);

    var content = "x".repeat(2048);
    clientFile.setDirty(content);

    assertThat(clientFile.isLargerThan(1024)).isTrue();
    assertThat(clientFile.isLargerThan(4096)).isFalse();
  }

  @Test
  void clean_local_file_uses_files_size_and_non_local_returns_false() throws Exception {
    var tempFile = Files.createTempFile("sl-clientfile-size", ".txt");
    Files.write(tempFile, new byte[4096]);

    var localUri = tempFile.toUri();
    var localClientFile = new ClientFile(localUri, "scope", Paths.get("local.txt"), null, StandardCharsets.UTF_8, tempFile, null, true);

    var missingPath = tempFile.getParent().resolve("missing.txt");
    var nonLocalUri = missingPath.toUri();
    var nonLocalClientFile = new ClientFile(nonLocalUri, "scope", Paths.get("missing.txt"), null, StandardCharsets.UTF_8, null, null, true);

    assertThat(localClientFile.isLargerThan(1024)).isTrue();
    assertThat(localClientFile.isLargerThan(8192)).isFalse();
    assertThat(nonLocalClientFile.isLargerThan(1)).isFalse();
  }

}


