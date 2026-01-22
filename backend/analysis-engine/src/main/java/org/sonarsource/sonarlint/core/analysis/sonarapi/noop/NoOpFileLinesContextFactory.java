/*
ACR-44dba2c7b87a484ca2ca86d4629e91d1
ACR-bc724c0eb398415b8fb5d242c3a8e7b8
ACR-c36a1977e0da4563999ddba10645bc8e
ACR-5c810e14eae648eb86b61f2e6e618065
ACR-caa26ca1c07d47c5b19be33ef3a7c974
ACR-198ebaa6780445d5bc857a40e684b234
ACR-5f3014c0ad4b454cbccb3a496d6e6dcc
ACR-7c234b3c94324bfbb6e6d021be06afa4
ACR-ca48b85fa7f449bfb6242e925e6ab55a
ACR-e0bbf7f5d19b41b9b8830d684735f94d
ACR-5b300e89276d4520982be607199af0a3
ACR-22b7e4236cfb4716bb7304b07df7f72d
ACR-dd1c92b6bf9a4ae88748fa2aba2ad153
ACR-c6b97fea5e76427db8b928691a2378ab
ACR-5aa421232bb94cfaad62db5428f58e43
ACR-51c400d609864d4c8b7224a6ddedeaf9
ACR-91116f7d60bf42f09b892ac24e1ea5ae
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi.noop;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.measures.FileLinesContext;
import org.sonar.api.measures.FileLinesContextFactory;

public class NoOpFileLinesContextFactory implements FileLinesContextFactory {

  @Override
  public FileLinesContext createFor(InputFile inputFile) {
    return new NoOpFileLinesContext();
  }

}
