/*
ACR-3101ce9d49c8457d9e6f9c7adc75b87f
ACR-4f1232a3903046fc8a9aed1c25fda9eb
ACR-fdb4ae5ca9e44f1cbfebb7a97ae9b829
ACR-fa348457daae464e910ffc9a9f31fe32
ACR-92f5b3e1c58c4cc4a84958032293c3fb
ACR-56e156739cb649f3a366032465070761
ACR-eb8178ea3171419685e7e44549b27e79
ACR-b0159d1297134971afc2ae8cd66fb7b3
ACR-d3860db6ede94a1b969823cb71f0d4db
ACR-2fd3782d7b8641abbc9a11720b833452
ACR-81e7b66e8de7415f837de17c7e5a6432
ACR-f83002aeef154c1a863d7e75fc60a985
ACR-ba01b043213846bd96f59c3c9dfe1d71
ACR-e00219c52ef947e7950b1df574917086
ACR-3c79cca5465f4bcfaa6a5951132a1d8c
ACR-e8a5cdab1a7f47a78a2db6c0d2a6adea
ACR-dfb0ce170a8c426290829e6843a9392f
 */
package org.sonarsource.sonarlint.core.commons.util.git;

import java.time.Instant;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class BlameParserTests {

  @Test
  void shouldNotPopulateGitBlameResultForEmptyBlameOutput() {
    var gitBlameReader = new GitBlameReader();

    gitBlameReader.readLine("");

    assertThat(gitBlameReader.getResult().lineCommitDates())
      .isEmpty();
  }

  @Test
  void shouldSplitBlameOutputCorrectlyWhenLinesContainSplitPattern() {
    var blameOutput = """
      5746f09bf53067450843eaddff52ea7b0f16cde3 1 1 2
      author Some One
      author-mail <some.one@sonarsource.com>
      author-time 1553598120
      author-tz +0100
      committer Some One
      committer-mail <some.one@sonarsource.com>
      committer-time 1554191055
      committer-tz +0200
      summary Initial revision
      previous 35c9ca0b1f41231508e706707d76ca0485b8a3ad file.txt
      filename file.txt
              First line with filename in it
      5746f09bf53067450843eaddff52ea7b0f16cde3 2 2
      author Some One
      author-mail <some.one@sonarsource.com>
      author-time 1553598120
      author-tz +0100
      committer Some One
      committer-mail <some.one@sonarsource.com>
      committer-time 1554191057
      committer-tz +0200
      summary Initial revision
      previous 35c9ca0b1f41231508e706707d76ca0485b8a3ad file.txt
      filename file.txt
              Second line also with filename in it
      """;
    var gitBlameReader = new GitBlameReader();
    var blameLines = blameOutput.split("\\n");

    for (String blameLine : blameLines) {
      gitBlameReader.readLine(blameLine);
    }

    assertThat(gitBlameReader.getResult().lineCommitDates())
      .containsExactly(Instant.ofEpochSecond(1554191055), Instant.ofEpochSecond(1554191057));
  }
}
