/*
ACR-b4251e415d294668a2409fe951599d80
ACR-b9b1dca6f88e40eaa6085746a4a499c7
ACR-4d7c64b5395f49a494a987a30c06db0f
ACR-2ca54795a36f49ceab5d4cbd8bd88e09
ACR-e2d1b1f2f1a7467c95c733d75e605c1b
ACR-c7f44920829643a6bf04a8280e20bd9b
ACR-1b78f1d086364f0f8d45137ad268c011
ACR-d3398224dcbc42d6a928e38758b7506e
ACR-46417c625ec34818ba3e1faa317d0611
ACR-bd5dfaf11fe845a8a84da359bfc5c107
ACR-38b946967e7945ab98cde89d353e84d2
ACR-327d987c796a4570a49e358db43e9a6d
ACR-849c29a414bd46269cb7c1562a6885e2
ACR-2bb32826a4de456888c920d33919b7ca
ACR-3227b5b93ab842f190db2c61a68a7406
ACR-35e80c1c0ff7460ba23ff3ad8f818829
ACR-e667bc082a704b8282f517fe7109210d
 */
package utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PluginLocator {
  public static final String SONAR_JAVA_PLUGIN_VERSION = "8.22.0.41895";
  public static final String SONAR_JAVA_PLUGIN_JAR = "sonar-java-plugin-" + SONAR_JAVA_PLUGIN_VERSION + ".jar";
  public static final String SONAR_JAVA_PLUGIN_JAR_HASH = "fa425ffda3272aef1abc137941a64772";
  public static final String SONAR_JAVA_SE_PLUGIN_VERSION = "8.19.0.1586";
  public static final String SONAR_JAVA_SE_PLUGIN_JAR = "sonar-java-symbolic-execution-plugin-" + SONAR_JAVA_SE_PLUGIN_VERSION + ".jar";
  public static final String SONAR_JAVA_SE_PLUGIN_JAR_HASH = "unused";

  public static final String SONAR_DBD_PLUGIN_VERSION = "2.5.0.19957";
  public static final String SONAR_DBD_PLUGIN_JAR = "sonar-dbd-plugin-" + SONAR_DBD_PLUGIN_VERSION + ".jar";
  public static final String SONAR_DBD_PLUGIN_JAR_HASH = "unused";
  public static final String SONAR_DBD_JAVA_PLUGIN_VERSION = SONAR_DBD_PLUGIN_VERSION;
  public static final String SONAR_DBD_JAVA_PLUGIN_JAR = "sonar-dbd-java-frontend-plugin-" + SONAR_DBD_JAVA_PLUGIN_VERSION + ".jar";
  public static final String SONAR_DBD_JAVA_PLUGIN_JAR_HASH = "unused";

  public static final String SONAR_JAVASCRIPT_PLUGIN_VERSION = "11.7.1.36988";
  public static final String SONAR_JAVASCRIPT_PLUGIN_JAR = "sonar-javascript-plugin-" + SONAR_JAVASCRIPT_PLUGIN_VERSION + ".jar";
  public static final String SONAR_JAVASCRIPT_PLUGIN_JAR_HASH = "2fab92be44e07f1d367f891a55258736";

  public static final String SONAR_PHP_PLUGIN_VERSION = "3.42.2.15271";
  public static final String SONAR_PHP_PLUGIN_JAR = "sonar-php-plugin-" + SONAR_PHP_PLUGIN_VERSION + ".jar";
  public static final String SONAR_PHP_PLUGIN_JAR_HASH = "88ddaa391f3176891a62375e98b76ae9";

  public static final String SONAR_PYTHON_PLUGIN_VERSION = "5.14.2.29072";
  public static final String SONAR_PYTHON_PLUGIN_JAR = "sonar-python-plugin-" + SONAR_PYTHON_PLUGIN_VERSION + ".jar";
  public static final String SONAR_PYTHON_PLUGIN_JAR_HASH = "e1cff9e38811ab71e6efbff087743367";

  public static final String SONAR_KOTLIN_PLUGIN_VERSION = "3.3.1.7792";
  public static final String SONAR_KOTLIN_PLUGIN_JAR = "sonar-kotlin-plugin-" + SONAR_KOTLIN_PLUGIN_VERSION + ".jar";
  public static final String SONAR_KOTLIN_PLUGIN_JAR_HASH = "XXX";

  public static final String SONAR_XML_PLUGIN_VERSION = "2.14.2.7437";
  public static final String SONAR_XML_PLUGIN_JAR = "sonar-xml-plugin-" + SONAR_XML_PLUGIN_VERSION + ".jar";
  public static final String SONAR_XML_PLUGIN_JAR_HASH = "XXX";

  public static final String SONAR_TEXT_PLUGIN_VERSION = "2.34.0.9939";
  public static final String SONAR_TEXT_PLUGIN_JAR = "sonar-text-plugin-" + SONAR_TEXT_PLUGIN_VERSION + ".jar";
  public static final String SONAR_TEXT_PLUGIN_JAR_HASH = "f679af4c0e2992c3cec281d6a9cd5062";

  public static final String SONAR_CFAMILY_PLUGIN_VERSION = "6.75.1.93101";
  private static final String SONAR_CFAMILY_PLUGIN_JAR = "sonar-cfamily-plugin-" + SONAR_CFAMILY_PLUGIN_VERSION + ".jar";
  public static final String SONAR_CFAMILY_PLUGIN_JAR_HASH = "XXX";

  public static Path getJavaPluginPath() {
    return getValidPluginPath(SONAR_JAVA_PLUGIN_JAR);
  }

  public static Path getJavaSePluginPath() {
    return getPluginPath(SONAR_JAVA_SE_PLUGIN_JAR);
  }

  public static Path getDbdPluginPath() {
    return getPluginPath(SONAR_DBD_PLUGIN_JAR);
  }

  public static Path getDbdJavaPluginPath() {
    return getPluginPath(SONAR_DBD_JAVA_PLUGIN_JAR);
  }

  public static Path getJavaScriptPluginPath() {
    return getValidPluginPath(SONAR_JAVASCRIPT_PLUGIN_JAR);
  }

  public static Path getPhpPluginPath() {
    return getValidPluginPath(SONAR_PHP_PLUGIN_JAR);
  }

  public static Path getPythonPluginPath() {
    return getValidPluginPath(SONAR_PYTHON_PLUGIN_JAR);
  }

  public static Path getCppPluginPath() {
    return getPluginPath(SONAR_CFAMILY_PLUGIN_JAR);
  }

  public static Path getXmlPluginPath() {
    return getValidPluginPath(SONAR_XML_PLUGIN_JAR);
  }

  public static Path getTextPluginPath() {
    return getValidPluginPath(SONAR_TEXT_PLUGIN_JAR);
  }

  public static Path getKotlinPluginPath() {
    return getPluginPath(SONAR_KOTLIN_PLUGIN_JAR);
  }

  private static Path getPluginPath(String file) {
    return Paths.get("target/plugins/").resolve(file);
  }

  private static Path getValidPluginPath(String file) {
    var path = getPluginPath(file);
    if (!Files.isRegularFile(path)) {
      throw new IllegalStateException("Unable to find file " + path);
    }
    return path;
  }

}
