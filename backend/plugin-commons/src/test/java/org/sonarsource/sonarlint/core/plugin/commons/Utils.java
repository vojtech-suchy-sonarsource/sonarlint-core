/*
ACR-3cbfd9eab220486e9ff2f42c1faec8eb
ACR-b6d5fcba5bb24469b5a15b5d2ee700ef
ACR-9b340cc76253421eb9b1e6b7fc107f5c
ACR-eaa1d54ab66949c9a67f530adba3c099
ACR-f41543e8887f42d385d7c167ab15c73a
ACR-8aa5247b54b54051b74a256cb9838432
ACR-9a7474534cf844008ef20691bb26ab27
ACR-ce044648910440bf9473d934060c7348
ACR-ab27a92b3533485bbfaa526d124e9cce
ACR-11ae630d7ce940d6b500fe42ccb96646
ACR-57d60bef2d2f4f4ab6ac1a95626c7bba
ACR-c7433f9bcc6841e3ac78c495eb46e5a4
ACR-f1ca30bd317841f1be4b5c5eed6eeae4
ACR-a804af0329134d7c852b86518d2a7870
ACR-bdcf6792baa74692a3babb1e92837d36
ACR-3befcd78371e4111aefc89e4c2e25d96
ACR-805ab8f0488b48bda151ae0073979a8a
 */
package org.sonarsource.sonarlint.core.plugin.commons;

import static org.apache.commons.lang3.RandomStringUtils.insecure;

public class Utils {
  private Utils() {
    //ACR-00b5fbbf74c648ee9aa4da4743b7c7c4
  }

  public static String randomAlphanumeric(int count) {
    return insecure().nextAlphanumeric(count);
  }
}
