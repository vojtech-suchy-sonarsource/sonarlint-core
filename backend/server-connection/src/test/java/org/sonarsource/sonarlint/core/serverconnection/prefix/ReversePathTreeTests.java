/*
ACR-f573b8b56436448ba519a55cccebf8f3
ACR-2a7e0c81198a46babc9967d62c6bf991
ACR-f02f8fb0aa6d4c3aa9206fdbbf07853a
ACR-a11a20d8845c440ca0815aaf0c681455
ACR-8e273607fb9c4758906177e8a62545be
ACR-ba48b80d88814b91977ae170397312cb
ACR-f4dd8c39211e404d83a126008c7b27e5
ACR-9ec42122259c42d8ba769fb063fe6bfb
ACR-fc9c3f28c3314c90bc726ab560f8ade8
ACR-360621d4dfce450b962e7babaa2d6e1e
ACR-1793c5afde4f4a55a1a1f40a9980f25d
ACR-012acbb8cd6e428e85b6979466934d85
ACR-7d0c3895d6b74bdfadde4c8bfdd425cc
ACR-b5abb1648b1847ba82506d511b74fa1c
ACR-30df64194f214c1aabf3bace6c0e944b
ACR-029e8aac515c461fab2b7a263803fb1f
ACR-4d7b0e1d98534313a8e827c2917a3717
 */
package org.sonarsource.sonarlint.core.serverconnection.prefix;

import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReversePathTreeTests {
  private final ReversePathTree tree = new ReversePathTree();

  @Test
  void should_return_matching_prefix() {
    tree.index(Paths.get("A/src/main/java/File.java"));

    var match = tree.findLongestSuffixMatches(Paths.get("B/src/main/java/File.java"));

    assertThat(match.matchLen()).isEqualTo(4);
    assertThat(match.matchPrefixes()).containsExactly(Paths.get("A"));
  }

  @Test
  void should_return_matching_prefixes() {
    tree.index(Paths.get("project1/src/main/java/File.java"));
    tree.index(Paths.get("project2/src/main/java/File.java"));
    tree.index(Paths.get("project2/src/test/java/File.java"));

    var match = tree.findLongestSuffixMatches(Paths.get("src/main/java/File.java"));

    assertThat(match.matchLen()).isEqualTo(4);
    assertThat(match.matchPrefixes()).containsExactlyInAnyOrder(Paths.get("project1"), Paths.get("project2"));
  }

  @Test
  void should_return_empty_prefix_if_full_match() {
    tree.index(Paths.get("project1/src/main/java/File.java"));
    tree.index(Paths.get("project2/src/main/java/File.java"));
    tree.index(Paths.get("project2/src/test/java/File.java"));

    var match = tree.findLongestSuffixMatches(Paths.get("project2/src/main/java/File.java"));

    assertThat(match.matchLen()).isEqualTo(5);
    assertThat(match.matchPrefixes()).containsExactly(Paths.get(""));
  }

  @Test
  void should_return_empty_if_no_match() {
    tree.index(Paths.get("project1/src/main/java/File.java"));
    tree.index(Paths.get("project2/src/main/java/File.java"));
    tree.index(Paths.get("project2/src/test/java/File.java"));

    var match = tree.findLongestSuffixMatches(Paths.get("File2.java"));

    assertThat(match.matchLen()).isEqualTo(0);
    assertThat(match.matchPrefixes()).isEmpty();
  }

  @Test
  void should_return_matches_that_are_part_of_other_matches() {
    tree.index(Paths.get("project1/A/pom.xml"));
    tree.index(Paths.get("project1/pom.xml"));
    tree.index(Paths.get("pom.xml"));
    var match = tree.findLongestSuffixMatches(Paths.get("pom.xml"));
    assertThat(match.matchLen()).isEqualTo(1);
    assertThat(match.matchPrefixes()).containsOnly(Paths.get(""), Paths.get("project1"), Paths.get("project1/A"));
  }

}
