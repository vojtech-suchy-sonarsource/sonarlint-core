/*
ACR-181f7b660e8246c3af2cf6543201881f
ACR-ec68fa95d3984a8fabc3478f125798f0
ACR-bd6451551fcc48579cbb543f0e03acf5
ACR-b8a62aff409349058948b7fe35bb8076
ACR-ef9eb923328a4d27bbeb5e5f520754bc
ACR-13a1f34eed72404a9718733328e7705b
ACR-b7cf2de2a1014e6ebb253a8d7451f08b
ACR-3267761ae12a41e6a88b9769231f40c9
ACR-f6b2f48ce2634740aa6275923c947fca
ACR-bf9f9a07a892414db986d7d09fb4ce55
ACR-a2fdff229e1a481b83f485b5da8fb6da
ACR-c0a9448927894a768779c78d3ae77587
ACR-c5bc7083b6be42d1bcf63d2118e56e2c
ACR-f1f1c02eb83c4ca3bfc95af1b6d37be9
ACR-40ff13577afa4c31bda0c92e71e691e6
ACR-892107bf602e4fc6911f72cc3dfc467d
ACR-464144729fcd46718731713c91e85c66
 */
package org.sonarsource.sonarlint.core.serverconnection;

import org.junit.jupiter.api.Test;

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.sonarsource.sonarlint.core.serverconnection.storage.ProjectStoragePaths.encodeForFs;

class ProjectStoragePathsTests {

  @Test
  void encode_paths_for_fs() {
    assertThat(encodeForFs("my/string%to encode**")).isEqualTo("6d792f737472696e6725746f20656e636f64652a2a");
    assertThat(encodeForFs("AU-TpxcA-iU5OvuD2FLz").toLowerCase()).isNotEqualTo(encodeForFs("AU-TpxcA-iU5OvuD2FLZ"));
    assertThat(encodeForFs("too_long_for_most_fs" + repeat("a", 1000))).hasSize(255);
    assertThat(encodeForFs("too_long_for_most_fs" + repeat("a", 1000)))
      .isNotEqualTo(encodeForFs("too_long_for_most_fs" + repeat("a", 1000) + "2"));
  }

}
