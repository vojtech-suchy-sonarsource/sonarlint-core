/*
ACR-ab7606d0575e41389077b2bd4424387a
ACR-69c4e2c45dd4492a875927747c60f34a
ACR-c192915cb8564c098ffcd931a4d946b0
ACR-7850fc92b2f04e458e552f7a79f95810
ACR-0c2302d903f14a0e933587a471f57dae
ACR-352c3deda03b4dbab2c8576c9258556b
ACR-64f549e495bf432296f0962b214c845c
ACR-c902816820124f43930def0eec0d6755
ACR-1d414328981f4a1090ddcb8d683e5af6
ACR-1446e222aaf941a6ace6b913a6cb3845
ACR-cfb951cdda6743f09118492bf5996cf6
ACR-d0c63079be75455887dc1b57b09662d7
ACR-8f6fbc5bb3534603a5a649ad3ebe3257
ACR-4239b310e58246f1a3aba48ba2c9fae0
ACR-636f5c7c4c9a49119b1ebf0f5e8c2128
ACR-edd963e1fee746d9899ba3c65dbe43c7
ACR-bce53a25999c4416aa50a83e85023b73
 */
package org.sonarsource.sonarlint.core.analysis.api;

import java.util.stream.Stream;
import org.sonar.api.batch.fs.InputFile;

public interface ClientModuleFileSystem {
  Stream<ClientInputFile> files(String suffix, InputFile.Type type);
  Stream<ClientInputFile> files();
}
