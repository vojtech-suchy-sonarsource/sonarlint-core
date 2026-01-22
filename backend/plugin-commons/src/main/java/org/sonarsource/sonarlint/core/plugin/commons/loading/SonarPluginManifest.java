/*
ACR-e13a240332b640d4b4bf63096dc3889b
ACR-39bb6c33f0894f999747c3c1b36711d4
ACR-d6462e301b3f44e88f625166bbaa14fd
ACR-0fef8d686ca143c1b8ea3cf739172b98
ACR-7364260af2564cbd8a147dfaabf330fa
ACR-05bd8cb08fc9433b8dece0b275dc766c
ACR-1db3d1569d55475ab9a8a37070456a43
ACR-9f4a69991934480d8fb4e37cf12c0f4a
ACR-66477fbfcbee488a8d086079d542b160
ACR-932a10fda6bf4d16ad1e7eebf71282a3
ACR-df1ca21e340f4831939ad133e0692243
ACR-9e977e6f795d4548b0711bb8aa09e8b3
ACR-b0989cc501cb455b9362f704805e3f50
ACR-25cc2f352d694dcea2d43662a431696f
ACR-c26365597c2741c188e86ebe2e973fec
ACR-711318ab965d4419900adffd9a17be1f
ACR-83329ff8408742909336ce2e9eb1521a
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

/*ACR-a1539e161f4d408da0d7058d4efa76e5
ACR-3f3618d80ec04d188084099de9490b4a
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

  /*ACR-9891ddb51023461baaf97a23e7d53e2c
ACR-841ac16b56b945f1920dc4fbe9c8320b
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
