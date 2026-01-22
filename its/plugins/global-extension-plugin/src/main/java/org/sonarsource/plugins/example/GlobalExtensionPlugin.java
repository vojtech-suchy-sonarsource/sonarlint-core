/*
ACR-3c582c9cb2c4447cbc4df801c4985c53
ACR-5d6d76d8c5fd4735a55cc3942a27016f
ACR-53e5860a08e94582a352549b47c510fe
ACR-d8c1d61caf064606ad03e5ad2d35b15a
ACR-985efe3601af4602825bd70281f4d3e6
ACR-9278860add9e4de594848964e5190db3
ACR-26a7e21220af482b9ada5e1859b6dfc4
ACR-01da00f9e8db43ffa362eb7402e88aea
ACR-59a539202608425a91df1995789aeb3e
ACR-69df96cfb76b4893a3744cc41b623a28
ACR-09fe9dd9267d44afb1fe65d23815b9e8
ACR-b61c3bea04024d83b2f069c9b76fe6a2
ACR-3469cea4ef964466b2189e7bdf6b0d84
ACR-946804abef1c45dbb87008e25dc1b6b1
ACR-8512a147679b4b2a904f65e85f8bc532
ACR-e12d2ea1ef96451daad20d8f2b467814
ACR-5c0edbd3ef1243a296ddffa44da59667
 */
package org.sonarsource.plugins.example;

import org.sonar.api.Plugin;

public class GlobalExtensionPlugin implements Plugin {

  @Override
  public void define(Context context) {
    context.addExtensions(GlobalLanguage.class,
      GlobalRulesDefinition.class,
      GlobalSonarWayProfile.class,
      GlobalSensor.class,
      GlobalExtension.class);
  }
}
