/*
ACR-f7fa73cf167d4ec49ba2d81eb86ec7a9
ACR-137ad4eb19c5455aaf8201113d4aeb56
ACR-cb796104638f4c7c8e686ec6a15b34f2
ACR-14365df7a0544c6e8b5dd6544d232c73
ACR-82be97f61ed14a13a3fde55836daf3f3
ACR-cfa4c46e48764a3f97010736ef548d69
ACR-097c4418839a4f9e88a48955ce0031c8
ACR-81987fd4c3d34d0bb370dce748ede5b9
ACR-11f54111cdf946b883d0d11e9a526101
ACR-3a64dbba507a4b2da1a9bd4cb264dc4c
ACR-3cd5fe8adf2342668a32ab221852c52c
ACR-9ded09c9d3e345f986a7468d6e6a93ed
ACR-9342640b3b9444999d385f32dfb3b905
ACR-ebc6ddb01af34db7b40be7c89439f278
ACR-edc0191baade4279ba4c7b7dc3456e1b
ACR-4c7664629602417f9370925f6a9bf4f9
ACR-8876979e8b154589ad81d470276db856
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.util.Collections;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem.Index;
import org.sonar.api.batch.fs.InputFile;

class FalsePredicate extends AbstractFilePredicate {

  static final FilePredicate FALSE = new FalsePredicate();

  @Override
  public boolean apply(InputFile inputFile) {
    return false;
  }

  @Override
  public Iterable<InputFile> filter(Iterable<InputFile> target) {
    return Collections.emptyList();
  }

  @Override
  public Iterable<InputFile> get(Index index) {
    return Collections.emptyList();
  }
}
