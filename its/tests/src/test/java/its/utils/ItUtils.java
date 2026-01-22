/*
ACR-d6e36a25221746a28928f4efc1397c8d
ACR-db979e44686c401d8d5a1d55754b900a
ACR-dd1a95acedee48c28fe19c4099f0b4e1
ACR-ef925c5ca9484ace9d75ad74f347048e
ACR-94ed66fcc1b0466cbfccbed5e74ad70c
ACR-cac7786679a14e03b7b24bfae2849516
ACR-f9d13824d66a473ea49d5f6cdd05ac64
ACR-b7c8a67ff2524a03b65ad477a46aca95
ACR-c5f9a2a7b50c4e41873b54b16acbc72c
ACR-27ea17fafac444b3b43de56727440d96
ACR-0bb2ce1156364f088f82b26aaedb7768
ACR-b2baf8a4f43e431e96a871882b87d046
ACR-80232ffd3b7e43148dcacb3a4853a4ff
ACR-e6e043cb30e2477fa6749dbef24636b8
ACR-693548de3ba449b9b52e4f3eeba0d8ff
ACR-60fe683e336545ad8fadcdcd54f20acd
ACR-b2b3b35179794ad8b764c6371eb48d78
 */
package its.utils;

public class ItUtils {

  public static final String LATEST_RELEASE = "LATEST_RELEASE";
  public static final String SONAR_VERSION = getSonarVersion();

  private ItUtils() {
    //ACR-7dec4731c8d0408480ba84b387191605
  }

  private static String getSonarVersion() {
    var versionProperty = System.getProperty("sonar.runtimeVersion");
    return versionProperty != null ? versionProperty : LATEST_RELEASE;
  }

}
