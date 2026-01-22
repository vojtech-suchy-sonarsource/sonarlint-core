/*
ACR-1e775e88082d46048163f05ea522d3a0
ACR-c086b0ebccce42eea19d2c42b0f6e08a
ACR-cae76afa39e146dc9ff46aa6f5ee0276
ACR-8a81c9489e434922b49e1278b83a29a9
ACR-6a2e7ccbce394306a1596f844820168c
ACR-bc0e29b53d1c421e824da0a04af932a5
ACR-eb4dba6cc2b84c5c92e25d55f5062dbf
ACR-ddfaec569236499bb15b91858d376cf1
ACR-13f4a162fdbd453c8fb643243c6bf2b3
ACR-e9cf605238c84ef5a4ec1d8dcd6958ad
ACR-b572bebbcf0549f3beacf74094b81aca
ACR-616b39bdb8b5489480c3aaf2720326a0
ACR-bc86a505257840d0b718880d1494f075
ACR-a5d0cd4cbf2b4832bf986cc4d75f8d34
ACR-0304851ff9434b8f8cbc14a3bec6a862
ACR-da78cd887b234b9984ec8297c7bdb832
ACR-d83995d52d1c4dbdbbcca84c5e653f7f
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
    //ACR-a37ca3a91fd14c0cb20e3b7f94f642f5
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
