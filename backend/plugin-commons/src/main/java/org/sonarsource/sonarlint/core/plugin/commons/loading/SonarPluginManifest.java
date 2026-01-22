/*
ACR-c6abb6240a774f7eb91c5a98c754a67f
ACR-9501eb49b1ba44c59e6169b1812e3099
ACR-7de0ee28127e4a4584c076ce046f2b5d
ACR-f671db9143a94ca4b4a6697f9e7770e0
ACR-9d0d4c0a47aa40db9fbc0bf8688959aa
ACR-a990846dac314ca39817164481c30013
ACR-78e2e9f4c152411188f1516fad592e9d
ACR-7ca3d19207dc4b02bffbfcf7f821142b
ACR-1a4ae4f73b874712922bb17c0778b8d9
ACR-61d95d1c4b5f4dfe829a3c774ae1644f
ACR-4da8a2590d9146b0a6e30f29f3b987a5
ACR-0ffc68a9223147bdaf4ff6e6bd371631
ACR-7819551f7cc34160968ad00bab0e8481
ACR-4b938932e5bd4b90b2340b073105785a
ACR-6f2d41d298514114b409f5ed1771981b
ACR-90a84c8f29334af690acb48536c1f194
ACR-751f7d082cac420f8c0c548585992111
 */
package org.sonarsource.sonarlint.core.plugin.commons.loading;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.annotation.CheckForNull;
import org.apache.commons.lang3.StringUtils;
import org.sonarsource.sonarlint.core.commons.Version;

import static java.util.Objects.requireNonNull;

/*ACR-58f6fe40df8f43bc9ed86818acb6d384
ACR-da0ef63c15cd44d787bb4e9ac57feb52
 */
public class SonarPluginManifest {

  public static final String KEY_ATTRIBUTE = "Plugin-Key";
  public static final String MAIN_CLASS_ATTRIBUTE = "Plugin-Class";
  public static final String NAME_ATTRIBUTE = "Plugin-Name";
  public static final String VERSION_ATTRIBUTE = "Plugin-Version";
  public static final String SONAR_VERSION_ATTRIBUTE = "Sonar-Version";
  public static final String DEPENDENCIES_ATTRIBUTE = "Plugin-Dependencies";
  public static final String REQUIRE_PLUGINS_ATTRIBUTE = "Plugin-RequirePlugins";
  public static final String BASE_PLUGIN = "Plugin-Base";
  public static final String JRE_MIN_VERSION = "Jre-Min-Version";
  public static final String NODEJS_MIN_VERSION = "NodeJs-Min-Version";

  private final String key;
  private final String name;
  private final String mainClass;
  private final String version;
  private final Optional<Version> sonarMinVersion;
  private final List<String> dependencies;
  private final String basePluginKey;
  private final List<RequiredPlugin> requiredPlugins;
  private final Optional<Version> jreMinVersion;
  private final Optional<Version> nodeJsMinVersion;

  public static class RequiredPlugin {

    private static final Pattern PARSER = Pattern.compile("\\w+:.+");

    private final String key;
    private final Version minimalVersion;

    public RequiredPlugin(String key, Version minimalVersion) {
      this.key = key;
      this.minimalVersion = minimalVersion;
    }

    public String getKey() {
      return key;
    }

    public Version getMinimalVersion() {
      return minimalVersion;
    }

    public static RequiredPlugin parse(String s) {
      if (!PARSER.matcher(s).matches()) {
        throw new IllegalArgumentException("Manifest field does not have correct format: " + s);
      }
      var fields = StringUtils.split(s, ':');
      return new RequiredPlugin(fields[0], Version.create(fields[1]).removeQualifier());
    }

  }

  /*ACR-33eeb06e02234ce9b848c464767938d2
ACR-2da664d4fd194e42b82da6e67702e65e
   */
  public static SonarPluginManifest fromJar(Path jarPath) {
    try (var jar = new JarFile(jarPath.toFile())) {
      var manifest = jar.getManifest();
      if (manifest != null) {
        return new SonarPluginManifest(manifest);
      } else {
        throw new IllegalStateException("No manifest in jar: " + jarPath.toAbsolutePath());
      }
    } catch (Exception e) {
      throw new IllegalStateException("Error while reading plugin manifest from jar: " + jarPath.toAbsolutePath(), e);
    }
  }

  public SonarPluginManifest(Manifest manifest) {
    var attributes = manifest.getMainAttributes();
    this.key = requireNonNull(attributes.getValue(KEY_ATTRIBUTE), "Plugin key is mandatory");
    this.mainClass = attributes.getValue(MAIN_CLASS_ATTRIBUTE);
    this.name = attributes.getValue(NAME_ATTRIBUTE);
    this.version = attributes.getValue(VERSION_ATTRIBUTE);
    this.sonarMinVersion = Optional.ofNullable(attributes.getValue(SONAR_VERSION_ATTRIBUTE)).map(Version::create);
    this.basePluginKey = attributes.getValue(BASE_PLUGIN);

    var deps = attributes.getValue(DEPENDENCIES_ATTRIBUTE);
    this.dependencies = List.of(StringUtils.split(StringUtils.defaultString(deps), ' '));

    var requires = attributes.getValue(REQUIRE_PLUGINS_ATTRIBUTE);
    this.requiredPlugins = Stream.of(StringUtils.split(StringUtils.defaultString(requires), ',')).map(RequiredPlugin::parse).toList();
    this.jreMinVersion = Optional.ofNullable(attributes.getValue(JRE_MIN_VERSION)).map(Version::create);
    this.nodeJsMinVersion = Optional.ofNullable(attributes.getValue(NODEJS_MIN_VERSION)).map(Version::create);
  }

  public String getKey() {
    return key;
  }

  @CheckForNull
  public String getName() {
    return name;
  }

  public List<RequiredPlugin> getRequiredPlugins() {
    return requiredPlugins;
  }

  @CheckForNull
  public String getVersion() {
    return version;
  }

  public Optional<Version> getSonarMinVersion() {
    return sonarMinVersion;
  }

  public String getMainClass() {
    return mainClass;
  }

  public List<String> getDependencies() {
    return dependencies;
  }

  @CheckForNull
  public String getBasePluginKey() {
    return basePluginKey;
  }

  public Optional<Version> getJreMinVersion() {
    return jreMinVersion;
  }

  public Optional<Version> getNodeJsMinVersion() {
    return nodeJsMinVersion;
  }

}
