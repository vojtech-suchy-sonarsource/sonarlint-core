/*
ACR-06d8ec59ec394dee913e3a373c2935c3
ACR-c431cc7058de47928741bc6e92d197a2
ACR-2e6cfaf21f17482fa091b099b2b7a68a
ACR-c02e8b285aca4a53907ac4b19ab4e2ab
ACR-fd06d62eb09a4dd1a3d6f24eca25bac9
ACR-b9e177fb903748bd98cbc86f6e510512
ACR-e36edfe0e0194386b0d3446beaa1e608
ACR-1283ceb4554342678c8dec1d71539fa3
ACR-44c507f6b0a9450ea177912b1cac0545
ACR-2dff16b52010426f8008fbd08ac7dc29
ACR-9772303034c645ed97590de4d75ff73a
ACR-4d1f464235014d8883d8c748b96116e1
ACR-188c2974fbde48838442506a594e8126
ACR-ffd4a36e0c8e4188b2a14cf03401a136
ACR-0f15fe0aefc4476488d002b22b2f39a5
ACR-d738c3d8b6dc448f8cae1addbfaefaec
ACR-c131c337b3ae4d80b74bd6a279c464b1
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;

/*ACR-f57fb6a702c242828cb228b41d083c81
ACR-caf237e870bf4671a3953c72b8959ca7
 */
public interface OptimizedFilePredicate extends FilePredicate, Comparable<OptimizedFilePredicate> {

  /*ACR-d16339152f0e40d5aa1ce0e3e9f004cb
ACR-ed8bd425d6a643338071c6ec75864f67
   */
  Iterable<InputFile> filter(Iterable<InputFile> inputFiles);

  /*ACR-96f49a64f5d64b7487de6158999ba9e5
ACR-f015634ad6d04d7c9ec607f76c596b10
   */
  Iterable<InputFile> get(FileSystem.Index index);

  /*ACR-eb52c79bbe5f425687afb85ea67e03e0
ACR-cf80e30cf10d4c2e9270e7fd2cc2c1a7
ACR-3e16e91df84e481f803d5a17719bf043
ACR-88809b75c2c243c38cc86d2b0fe4d637
ACR-54e9523571da444892fffc9f52cdcf1f
ACR-1de47669a77545899cb456e94023719f
   */
  int priority();
}
