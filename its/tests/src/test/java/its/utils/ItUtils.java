/*
ACR-17e9f4c2cb21457198ca2d13c2b4d228
ACR-f3ae1efa489f4e18ae2e37d6da4951e4
ACR-27870be5e29e40fb9761e60b207a36a2
ACR-e1ceae997a5b4aab82a4c66c7feae2b8
ACR-6de993751daf49829e40019c4b01b892
ACR-d20ce69b4fdd4245accfb36ec0aaedf0
ACR-0f74f32b29ea48c99f6ee4b12e3ff621
ACR-498586cac51f4646b5b693299e8fdd44
ACR-5041a1a8f93c4d26bf289ad0c67a2bb2
ACR-0a68f9445602462488eb0d0063a154f0
ACR-53ebec9d02584f1db9335e1a034eb182
ACR-99db5630e43d4fb894701d27e406b243
ACR-ac5618aa436a4b7fa3eabb3607067b78
ACR-c1fb3788c087431eba9d5f4a471fdbf7
ACR-609b0264bad7495faeacd75cf62b62ed
ACR-fc72a07642a84a3cbc821a6f1e727971
ACR-c3ae9b2d81e243c38c7f4d7a718b356a
 */
package its.utils;

public class ItUtils {

  public static final String LATEST_RELEASE = "LATEST_RELEASE";
  public static final String SONAR_VERSION = getSonarVersion();

  private ItUtils() {
    //ACR-f4dadb28a16f444da2112ec576607e1f
  }

  private static String getSonarVersion() {
    var versionProperty = System.getProperty("sonar.runtimeVersion");
    return versionProperty != null ? versionProperty : LATEST_RELEASE;
  }

}
