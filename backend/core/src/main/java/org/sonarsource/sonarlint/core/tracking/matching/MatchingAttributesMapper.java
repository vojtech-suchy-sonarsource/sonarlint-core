/*
ACR-2f5e89d7ee504e499c954711c3a5d96a
ACR-dfd3a41ca8974be2b2a6d4c62483943f
ACR-f73b4308fc8a42eb9f832bfdc5450b8d
ACR-ecca4e8c7ce044f7955c4d155f21cab5
ACR-534eb841a992468086c47c70dc04270c
ACR-617aaac1b51f40159489677ef9e427d9
ACR-789b11b14abc43bf9982de22d66b69a6
ACR-d96d5ec12858470d86372e8cb9b33a71
ACR-57443ab8b93b482ba652dbcf2b29ceaf
ACR-fc35c3341bc046ef9374d928601ca256
ACR-d70606cbc8c6450ea548b59b80574a67
ACR-80af3f45fcba4da8b1e401dced9f0681
ACR-0c5e96f7bbc54264a411e6318909f69a
ACR-85454f06bd9149ad9d144fc5c5bdeff3
ACR-24d6e04176074471b420ddfd21769ca5
ACR-f36db7d14cad4362b72575eafdb1dd15
ACR-957364caa37845e4bc4e811489d4705c
 */
package org.sonarsource.sonarlint.core.tracking.matching;

import java.util.Optional;

public interface MatchingAttributesMapper<G> {


  String getRuleKey(G issue);

  Optional<Integer> getLine(G issue);

  Optional<String> getTextRangeHash(G issue);

  Optional<String> getLineHash(G issue);

  String getMessage(G issue);

  Optional<String> getServerIssueKey(G issue);
}
