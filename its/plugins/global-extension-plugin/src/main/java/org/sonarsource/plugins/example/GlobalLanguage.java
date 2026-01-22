/*
ACR-5c3633135bcc45aea04640c3b0f93567
ACR-5edf3b808a0846709394120aea021083
ACR-52469ccf640940b4a88f3eebd563d4b5
ACR-732c94e24f6d41a9b428b4d43cbe0177
ACR-19e7bb48a8c64ece988e8b2de24fd781
ACR-3c2dfcf678624efaa11497849c357fa9
ACR-80d8b728779544b4ba36fbba38aca307
ACR-c6c6494a53e3415ab2af564d0c1588fa
ACR-6ab2452b1f8d47d7b40105c6b220647c
ACR-1065f9a8501348679a085d93ed79062f
ACR-7dcbe10237bb48f586a7e7e706849437
ACR-6ac85391ddbf46ba833d042ded5f236b
ACR-e0f8ae0b0a324349839f5913b3cde984
ACR-9bb796fa1b504be98996101e745868c2
ACR-3ddf1e12ccd841a6a115527df85914a8
ACR-4301d8bf23cd4fa79ca36f2d1410fad6
ACR-ed3c5cb2e047422f9034655233ab7338
 */
package org.sonarsource.plugins.example;

import org.sonar.api.resources.AbstractLanguage;

public class GlobalLanguage extends AbstractLanguage {

  //ACR-f1774e1d191349f19d9ec86b011c674b
  static final String LANGUAGE_KEY = "cobol";

  public GlobalLanguage() {
    super(LANGUAGE_KEY, "Global");
  }

  @Override
  public String[] getFileSuffixes() {
    return new String[] {"glob"};
  }

}
