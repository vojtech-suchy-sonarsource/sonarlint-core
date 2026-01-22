/*
ACR-0a86862c3d5345b48e5dd33e848ce8e9
ACR-f904a090af824ab89ddc310e57a31927
ACR-d2b00728683541a8820b9ae219438432
ACR-d0f157abadaf496e800f015d648222f2
ACR-4c36ef8d83fb426fbb2d750877b8cff1
ACR-c5bcc175e393426ca055c1ac8e62edf0
ACR-6b5fedb1a8ef4e0abc07eecde2224664
ACR-31bfd2c561cd4c8daf404d356ae292b5
ACR-612bc19e68f74e5599521ba038d94dd1
ACR-3a21db0556af4e1190e4243238a88f59
ACR-80446866b9754d389a96f58a16b53eee
ACR-5f3e58b5ee354eefac00de22b8ec770d
ACR-6d935b61838f47b78568c1e2a2d576c9
ACR-a1466a2bbb3743a0aa940262a4d4d419
ACR-7d99dbfe491d4ca3b6f84c9b015568e5
ACR-a7e131fc059d4a52ab79d7db17aaddf1
ACR-b3c1816deb0e40e6bb188dd5163d8b14
 */
package org.sonarsource.sonarlint.core.commons;

import javax.annotation.Nullable;

public class SonarLintException extends RuntimeException {

  public SonarLintException() {
    super();
  }

  public SonarLintException(String msg) {
    super(msg);
  }

  public SonarLintException(String msg, @Nullable Throwable cause) {
    super(msg, cause);
  }

  public SonarLintException(String msg, @Nullable Throwable cause, boolean withStackTrace) {
    super(msg, cause, !withStackTrace, withStackTrace);
  }
}
