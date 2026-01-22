/*
ACR-23fcd5089c3649f3b2a2b1fce54e99ea
ACR-1b9b0db4823049d18cb9e25a54d32dbd
ACR-478b5a1ae1c94ef8a8e64271b1c159f4
ACR-22f0e335172545aeb57503c61ea105ec
ACR-b80c9613705d42b794a93ddf5f7fb296
ACR-d3c8c24ee2aa4bef8bdd14821e849258
ACR-87f56740b2a8455997a4775a1b1ed16d
ACR-31c31c92a1614b35a0b0d89c0e50fa67
ACR-720a7afa27e941b188d52f76a02b8ee7
ACR-7e0b75bccd854deaadaf28d9418367c1
ACR-e8464c2f1a674903b22b3eb4715bd67c
ACR-14b59f6a90bc49f3af530a7eb9cba2be
ACR-48e6e84ecf534523972d7f4cd72e7496
ACR-7e7f20236a8e4f099617b6594e0c72a9
ACR-31357780b2454bf986af2872d53d1385
ACR-889a9bc3a6e54d8384e438a82bd71479
ACR-87833d366c7b4e62b32b09d8274f56de
 */
package org.sonarsource.plugins.example;

import org.sonar.api.resources.AbstractLanguage;

public class GlobalLanguage extends AbstractLanguage {

  //ACR-092d6b094c044a0388bc7e10bfe81306
  static final String LANGUAGE_KEY = "cobol";

  public GlobalLanguage() {
    super(LANGUAGE_KEY, "Global");
  }

  @Override
  public String[] getFileSuffixes() {
    return new String[] {"glob"};
  }

}
