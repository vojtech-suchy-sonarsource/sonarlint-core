/*
ACR-de6e48617ebf4d50af6a0837db1409f5
ACR-4ec2851d65574443b33745c5c7eb87e6
ACR-5088a0e45a74480aaac17c9725944538
ACR-92754c7a515942068617c9a71e49458e
ACR-63827f1fcae94f19836bd33edbbf501a
ACR-cd81fa2989e2447bbb0aed40c5272c01
ACR-a06d3dbc3e774a93b9904f9d3a0b1b1a
ACR-60eb52d4c2f64595b75d7352a2b02186
ACR-fa14f8055c5a4f4f8f44c80fe6ee1bdc
ACR-b2ecc23ac6174d34a25a9e4f32c9dfb6
ACR-1a2e1dbd045949718cad0def7d2ea813
ACR-d9d659359d644b92a41acfacff60d9b0
ACR-65cdcd1f0d674b94a0ca02fab5b77abc
ACR-2337c91dae6245ed934cb681edc9996c
ACR-92e43f8f494e4611b13acbc8c1dd55ec
ACR-6b28158fe0f940ef9dc23fc19a8cc563
ACR-bffded90f8144f4bbdd913d5663623c8
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem.Index;
import org.sonar.api.batch.fs.InputFile;

class TruePredicate extends AbstractFilePredicate {

  static final FilePredicate TRUE = new TruePredicate();

  @Override
  public boolean apply(InputFile inputFile) {
    return true;
  }

  @Override
  public Iterable<InputFile> get(Index index) {
    return index.inputFiles();
  }

  @Override
  public Iterable<InputFile> filter(Iterable<InputFile> target) {
    return target;
  }
}
