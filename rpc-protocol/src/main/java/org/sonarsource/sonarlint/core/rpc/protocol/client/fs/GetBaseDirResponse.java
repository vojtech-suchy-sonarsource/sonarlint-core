/*
ACR-ee14ae74accf4a67865c5679f9f892f2
ACR-d9ae0cecb8c94e148274a477bb50b296
ACR-6d3e2b7fc3444c839aefbedc832d88b0
ACR-4610cb69acaa4531be74375e3f503976
ACR-1e838c5025d04cbdacec9a3f8cacdeb8
ACR-d695b2e99abd41879b172f3d7e669cf3
ACR-5a197a91503a4e128161252eebe6379c
ACR-7883254d8d94409d9e0fc388882ca207
ACR-33db0bbd59d945b49cf84848d462a8ee
ACR-e5a1abd4162d42f6a90abce02bb51dec
ACR-8817490af0df446eb1cf247400ccbad9
ACR-a433027d77ed4cfc84d026a83f040860
ACR-e792a9abe3d94fdfb54906e7060e61ec
ACR-62cce5ca175c42a39057096b544881cb
ACR-d7ebc244572f4840a4098af957e23b1c
ACR-1b86b61712754510a0e0cdaff1acc19f
ACR-fd8cac425b0e42febd30905ef7430a4d
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.fs;

import java.nio.file.Path;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class GetBaseDirResponse {
  private final Path baseDir;

  public GetBaseDirResponse(@Nullable Path baseDir) {
    this.baseDir = baseDir;
  }

  @CheckForNull
  public Path getBaseDir() {
    return baseDir;
  }
}
