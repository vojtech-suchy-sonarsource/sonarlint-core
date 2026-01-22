/*
ACR-1da7a6a3cb4c4d1fa370cff3447f8cfe
ACR-681fa4a57ba04539861728e3e0e8d1a0
ACR-e9e89335a64448eb8231c24b77bd062a
ACR-ef982b474ca646d9ad1797d5697f51c3
ACR-b106b5b282614a7e93bbfb99444f92b3
ACR-061ca4e889c84f0faa2333b13e895b28
ACR-6f52115b7fac4162bb8ba96c66bab6d9
ACR-e23548f18da841e3b827a8ab740b123f
ACR-d1ec4f5735144ff595cbb0bc6a7b02d6
ACR-16b415a8a91a480a94ce8efe9fb6bbf3
ACR-fa9c02cd7d944400bc41eb4d6b0994f1
ACR-709fa601f44e47c7a19f5c1905020a87
ACR-0b13202eb6a74881b1bb9281b5a11967
ACR-e00ca9c00a6a42838f70945350aacf31
ACR-1ed497021d8a465ab4d2d5d9d743a0ba
ACR-3f8786d7d0764d018a30af8e83f39a3d
ACR-9afa5837c08c473f8f23373e0ff31fbc
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
