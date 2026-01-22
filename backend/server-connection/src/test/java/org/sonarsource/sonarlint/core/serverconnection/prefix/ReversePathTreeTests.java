/*
ACR-074a85506fd64e4abd0a9fe59c74c084
ACR-a23a1c00dd5248f9a8e8880b6c7e13ac
ACR-17714cea07b643bc93b687c7596b4e59
ACR-ef64a875dbb04c95a0d88a42ac78394d
ACR-47ac849d1b704cb28262485a852f21dd
ACR-c9bf93d30dd2415c847a576a64e03049
ACR-1ed2b0d3830545df911ab3f5ef0061c8
ACR-2af38f3d5aa04495aec38c7625bdd848
ACR-8df89cf33af340da84871da150b997da
ACR-e990adbb9fcb4860986c630413830211
ACR-a39a950d70cc475bb90118c40034568e
ACR-8502de394f5f421b882f617af9cad200
ACR-c521aa35048b43a2a510aaf242395dee
ACR-9f420754dc1f4a0fb2f6eca95c197a3b
ACR-830498fefe814e17b769662fedddb4fd
ACR-27736949545247b88578602476aed9d0
ACR-a7d492988c8740caa7d6de6cfd9f4803
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
