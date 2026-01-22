/*
ACR-31c5545cb66d4f279c939c63be9adca9
ACR-d584b3aa6bdc48aca6decf218b968403
ACR-3d11f5b36d5c40bfb3f7849520bcdd29
ACR-7c6add46a8354111807a00e6001cac08
ACR-c6a8137478d943fbb4cfcbec1c1b9c05
ACR-60a4a925fdf04492b7e1b95f095155c5
ACR-be5dceb4014947419d19e3c14c892fa8
ACR-7c51bffd01934f4f96405bd8a17a1f82
ACR-537dd06ce4804612b497703056d6cccb
ACR-6f0f4239281e4edaa0b373a32d4290b1
ACR-6036ec8f337648a59ac7d82714394f93
ACR-13e3df46598946368da9afe098a8f49c
ACR-58cb633e13d2482abda48f388d0428d1
ACR-5d02fdadf56c4cff9048332b7694a692
ACR-8b7b66c507024539b919066db49ceeb9
ACR-e225e1365c55412882dc30f7848fd1b3
ACR-52772352bab94fb9b9a1d56fd490da47
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


