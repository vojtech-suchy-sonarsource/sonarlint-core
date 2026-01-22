/*
ACR-f48d930769e34dde8dcb5558511ea9c8
ACR-f12b7e92845746f884d47636eab3f9c8
ACR-30dbc66c07fc4898976bc7a0ea15861f
ACR-88127f79b0cd431183908b1fbafcbb39
ACR-1467703874c048be81f0b34c9a924ae8
ACR-c94e9621dca44a71bcd3376d77e0f217
ACR-b554ccba854f4dd2b614b6bf98924eb7
ACR-07137118ece446bcae8c144f0aec35c6
ACR-fb1967c880964ee0841eea37d9c37946
ACR-df66c44a4a074d0c87ea82c06eb89f76
ACR-216838c915da4df3b13d0593da20295a
ACR-cd62cfb8afff4d0a81178678bb329077
ACR-aaf1a78742dd4bdcb68dd98ccf124961
ACR-1ab7cb8571d34901852918230743b71c
ACR-fe53363820bb4f6198afd39bf2e4f223
ACR-61a61fee4e6f46a3b0cbc431e1549368
ACR-ce38bfc044db445682ef0e3f4e8a1cbf
 */
package utils;

import java.nio.file.Path;
import java.util.Set;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;
import org.sonarsource.sonarlint.core.test.utils.plugins.Plugin;

public class TestPlugin {
  public static final Plugin JAVA = new Plugin(Language.JAVA, PluginLocator.getJavaPluginPath(), PluginLocator.SONAR_JAVA_PLUGIN_VERSION, PluginLocator.SONAR_JAVA_PLUGIN_JAR_HASH);
  public static final Plugin JAVA_SE = new Plugin("javasymbolicexecution", Language.JAVA, PluginLocator.getJavaSePluginPath(), PluginLocator.SONAR_JAVA_SE_PLUGIN_VERSION, PluginLocator.SONAR_JAVA_SE_PLUGIN_JAR_HASH);

  public static final Plugin DBD = new Plugin("dbd", Language.JAVA, PluginLocator.getDbdPluginPath(), PluginLocator.SONAR_DBD_PLUGIN_VERSION, PluginLocator.SONAR_DBD_PLUGIN_JAR_HASH);
  public static final Plugin DBD_JAVA = new Plugin("dbdjavafrontend", Language.JAVA, PluginLocator.getDbdJavaPluginPath(), PluginLocator.SONAR_DBD_JAVA_PLUGIN_VERSION, PluginLocator.SONAR_DBD_JAVA_PLUGIN_JAR_HASH);

  public static final Plugin PHP = new Plugin(Language.PHP, PluginLocator.getPhpPluginPath(), PluginLocator.SONAR_PHP_PLUGIN_VERSION, PluginLocator.SONAR_PHP_PLUGIN_JAR_HASH);
  public static final Plugin PYTHON = new Plugin(Language.PYTHON, PluginLocator.getPythonPluginPath(), PluginLocator.SONAR_PYTHON_PLUGIN_VERSION,
    PluginLocator.SONAR_PYTHON_PLUGIN_JAR_HASH);
  public static final Plugin JAVASCRIPT = new Plugin(Set.of(Language.JS, Language.TS), PluginLocator.getJavaScriptPluginPath(), PluginLocator.SONAR_JAVASCRIPT_PLUGIN_VERSION,
    PluginLocator.SONAR_JAVASCRIPT_PLUGIN_JAR_HASH);
  public static final Plugin TEXT = new Plugin(Language.SECRETS, PluginLocator.getTextPluginPath(), PluginLocator.SONAR_TEXT_PLUGIN_VERSION,
    PluginLocator.SONAR_TEXT_PLUGIN_JAR_HASH);
  public static final Plugin XML = new Plugin(Language.XML, PluginLocator.getXmlPluginPath(), PluginLocator.SONAR_XML_PLUGIN_VERSION, PluginLocator.SONAR_XML_PLUGIN_JAR_HASH);
  public static final Plugin CFAMILY = new Plugin(Set.of(Language.C, Language.CPP, Language.OBJC), PluginLocator.getCppPluginPath(), PluginLocator.SONAR_CFAMILY_PLUGIN_VERSION,
    PluginLocator.SONAR_CFAMILY_PLUGIN_JAR_HASH);
  public static final Plugin KOTLIN = new Plugin(Set.of(Language.KOTLIN), PluginLocator.getKotlinPluginPath(), PluginLocator.SONAR_KOTLIN_PLUGIN_JAR,
    PluginLocator.SONAR_KOTLIN_PLUGIN_JAR_HASH);

  private final Set<Language> languages;
  private final Path path;
  private final String version;
  private final String hash;

  TestPlugin(Language language, Path path, String version, String hash) {
    this(Set.of(language), path, version, hash);
  }

  TestPlugin(Set<Language> languages, Path path, String version, String hash) {
    this.languages = languages;
    this.path = path;
    this.version = version;
    this.hash = hash;
  }

  public Set<Language> getLanguages() {
    return languages;
  }

  public String getPluginKey() {
    return SonarLanguage.valueOf(languages.iterator().next().name()).getPluginKey();
  }

  public Path getPath() {
    return path;
  }

  public String getVersion() {
    return version;
  }

  public String getHash() {
    return hash;
  }
}
