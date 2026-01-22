/*
ACR-8ba53229c6e9450eabe8395cc4c32b79
ACR-ca01242cca864831bd9c0ff04131b415
ACR-1b04c59d82bc40d696b70cdecd792f25
ACR-2661626bae5b4db8b5fc27bc8c44dbf5
ACR-1b64b8110f9a439ea93eb96cdb008b8f
ACR-e3418d5ad3f14e36bbb9d85da4e25abe
ACR-44752a5486554eb2bb2ee7401130ce10
ACR-c25239536343459cb4520f519fab873e
ACR-3042e2df52074f30a23071896500831a
ACR-6f174e6fcd004453a895f7072cddb2df
ACR-c4fc91db98e040d0aad5637b192637fa
ACR-62b8671f2c114dfb8a96d9ef52245ade
ACR-3798d23562d6475b997cd4f54eb63be6
ACR-eb8e2de34954490984e89a20c6600134
ACR-7756e7b5dcc2422cbc73be6df2ef6b9c
ACR-b8bcb44de6ed4d809b9dcdbe2cb70859
ACR-2cd16989d6f14ec79ad62bcc42ae1b1e
 */
package org.sonarsource.sonarlint.core.commons.util.git;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class GitBlameReader {
  private static final String COMMITTER_MAIL = "committer-mail ";
  private static final String COMMITTER_TIME = "committer-time ";
  private static final String NOT_COMMITTED = "<not.committed.yet>";

  private final List<Instant> commitDates = new ArrayList<>();
  private boolean isCurrentLineCommitted;

  public void readLine(String line) {
    //ACR-08db750503754066a6841fe094bf3b3d
    if (line.startsWith(COMMITTER_MAIL)) {
      var committerEmail = line.substring(COMMITTER_MAIL.length());
      isCurrentLineCommitted = !committerEmail.equals(NOT_COMMITTED);
    } else if (line.startsWith(COMMITTER_TIME)) {
      commitDates.add(isCurrentLineCommitted ? Instant.ofEpochSecond(Long.parseLong(line.substring(COMMITTER_TIME.length()))).truncatedTo(ChronoUnit.SECONDS) : null);
    }
  }

  public BlameResult getResult() {
    return new BlameResult(commitDates);
  }
}
