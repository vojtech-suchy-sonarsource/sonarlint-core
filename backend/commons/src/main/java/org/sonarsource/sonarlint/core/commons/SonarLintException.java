/*
ACR-d48753cc0e1a4db0a3d26761d529d500
ACR-cf3da600fdf34d46b7fe581dce947443
ACR-b3de08264a91468e91f073d78086af80
ACR-b6a1e5eb29c54638bcfcaead622903a5
ACR-6fb34fbc5dd14406bea96d5f01592aa1
ACR-92d3efff98ee46e3a049089f5b6bacc3
ACR-dd0a623fc9d44678bf108358beb46541
ACR-d0b049b660a344fe93dec629e329db13
ACR-798258d9298646deaa95da69ccf820bb
ACR-502741607f524cbea939d360d08c9dea
ACR-1ede595db31948ebaaf68b55400a24d3
ACR-df9b5ddac7624920854990a6e257be9b
ACR-0b919e2b2fec47cebcc0dbb16938b033
ACR-6fbe4dab96444df8a3286f0baa4395a2
ACR-8cb23589dabf43baa7ff6a4ec34bc510
ACR-be22e00398ec479198694aaee926b57c
ACR-37c15dab5e3c4105a8426c190394e8e7
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
