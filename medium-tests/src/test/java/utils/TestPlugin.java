/*
ACR-25f0a9d1fb49464db47026585a706559
ACR-96dabd21cfa5450692385396ee73d14f
ACR-8e022bae41574922bbf34443b8d83c7e
ACR-9f267cb997b54b8d8a7060f26713f7ee
ACR-6df03b6cfd994939bcb4055f6f3b1b39
ACR-e9bc6bef50bd48d0a979fad491e902b7
ACR-01f40a64d0174adf8155875c89b12fbe
ACR-0be8fc4942434809a889b3969de983d0
ACR-7696ef3472d54db3af0d8a24c84a5ead
ACR-e7655cdcf7314d3fba7b1d726a71ec4a
ACR-2f2d771081094897a1952adf21c8a0e2
ACR-5fcc15efe3df41ef850165222e35cca5
ACR-39b835b92d1f43dc9697db86ac54e26e
ACR-ce105668778541408ae47a98b583b025
ACR-a2998c60f04742978d08c14b5bb1e1d5
ACR-7b42850c5a1a424ea9803476e0d2a1a5
ACR-6e9747acc8df49ac898e4320ce9f2ee0
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
