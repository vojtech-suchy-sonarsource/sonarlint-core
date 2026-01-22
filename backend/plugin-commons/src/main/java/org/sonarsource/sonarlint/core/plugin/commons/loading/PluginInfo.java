/*
ACR-c728688b160e4272849abb74da4edbd6
ACR-084f42431e2c46faad5d2ece15c8a36f
ACR-b6e845303cb04c479112b312361faf41
ACR-f619e14554bb4f1e859a333b79478f7f
ACR-77f47bf853744dec8d1f32f1303dc765
ACR-25812b881ffd40b28459543999d3d17b
ACR-0fef30a1609f4c31b5f7a303702bbbce
ACR-7cf51dda26514aa98de59a8db815eaad
ACR-f9a17a00a3e2474c8acf277df8480887
ACR-6263c39a871f421ba5b42991fe40470f
ACR-2b398c0038314bec9e5fdc8b64c283fa
ACR-0d5f1d476e2f477aa9879eb38264d30f
ACR-caa5c36a52e4409eb86eda415791a688
ACR-81760cd94f964c5eb7b79a3a1ae0c650
ACR-7130f45e619f4f6c957a53c772ea9faf
ACR-23b4b32a79104c21848b10f6b6051cff
ACR-33aeaefdbc514cf7938815ba5a5c4c53
 */
package org.sonarsource.sonarlint.core.plugin.commons.loading;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.sonar.api.utils.MessageException;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.plugin.commons.loading.SonarPluginManifest.RequiredPlugin;

import static java.util.Objects.requireNonNull;

public class PluginInfo {

  private final String key;
  private String name;

  private File jarFile;

  @CheckForNull
  private String mainClass;

  @CheckForNull
  private Version version;

  @CheckForNull
  private Version minimalSqVersion;

  @CheckForNull
  private String basePlugin;

  private final Set<RequiredPlugin> requiredPlugins = new HashSet<>();

  @CheckForNull
  private Version jreMinVersion;

  @CheckForNull
  private Version nodeJsMinVersion;

  private List<String> dependencies = List.of();

  public PluginInfo(String key) {
    requireNonNull(key, "Plugin key is missing from manifest");
    this.key = key;
    this.name = key;
  }

  public PluginInfo setJarFile(File f) {
    this.jarFile = f;
    return this;
  }

  public File getJarFile() {
    return jarFile;
  }

  public String getKey() {
    return key;
  }

  public String getName() {
    return name;
  }

  @CheckForNull
  public Version getVersion() {
    return version;
  }

  @CheckForNull
  public Version getMinimalSqVersion() {
    return minimalSqVersion;
  }

  @CheckForNull
  public String getMainClass() {
    return mainClass;
  }

  @CheckForNull
  public String getBasePlugin() {
    return basePlugin;
  }

  public Set<RequiredPlugin> getRequiredPlugins() {
    return requiredPlugins;
  }

  @CheckForNull
  public Version getJreMinVersion() {
    return jreMinVersion;
  }

  @CheckForNull
  public Version getNodeJsMinVersion() {
    return nodeJsMinVersion;
  }

  public List<String> getDependencies() {
    return dependencies;
  }

  public PluginInfo setName(@Nullable String name) {
    this.name = Optional.ofNullable(name).orElse(this.key);
    return this;
  }

  public PluginInfo setVersion(Version version) {
    this.version = version;
    return this;
  }

  public PluginInfo setMinimalSqVersion(@Nullable Version v) {
    this.minimalSqVersion = v;
    return this;
  }

  /*ACR-afab29c7ca4e4053b4e791a4bc32a27a
ACR-c68a4da2b865401dada46473756c0d54
   */
  public PluginInfo setMainClass(String mainClass) {
    this.mainClass = mainClass;
    return this;
  }

  public PluginInfo setBasePlugin(@Nullable String s) {
    this.basePlugin = s;
    return this;
  }

  public PluginInfo addRequiredPlugin(RequiredPlugin p) {
    this.requiredPlugins.add(p);
    return this;
  }

  private PluginInfo setMinimalJreVersion(@Nullable Version jreMinVersion) {
    this.jreMinVersion = jreMinVersion;
    return this;
  }

  private PluginInfo setMinimalNodeJsVersion(@Nullable Version nodeJsMinVersion) {
    this.nodeJsMinVersion = nodeJsMinVersion;
    return this;
  }

  public PluginInfo setDependencies(List<String> dependencies) {
    this.dependencies = dependencies;
    return this;
  }

  /*ACR-d8bfaaf8a6fa4065a9e12bff572c1bc4
ACR-2b6daf9e2e7d4c9d89e38f15d24a67c1
ACR-07a0b70022d34db6ac0af260d36721ef
ACR-51b07a93ce614edcaeabc8e2ef666f87
   */
  public boolean isCompatibleWith(String implementedApi) {
    if (null == this.minimalSqVersion) {
      //ACR-044ff30ef6884beca22ed1a35f69253d
      return true;
    }

    //ACR-803abfedfdb64284a9559dec5dd9f18b
    var requestedApi = Version.create(minimalSqVersion.getMajor() + "." + minimalSqVersion.getMinor());
    var implementedApiVersion = Version.create(implementedApi);
    return implementedApiVersion.compareToIgnoreQualifier(requestedApi) >= 0;
  }

  @Override
  public String toString() {
    return "[" + key + " / " + version + "]";
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    var info = (PluginInfo) o;
    if (!key.equals(info.key)) {
      return false;
    }
    return !(version != null ? !version.equals(info.version) : (info.version != null));

  }

  @Override
  public int hashCode() {
    var result = key.hashCode();
    result = 31 * result + (version != null ? version.hashCode() : 0);
    return result;
  }

  public static PluginInfo create(Path jarFile) {
    var manifest = SonarPluginManifest.fromJar(jarFile);
    return create(jarFile, manifest);
  }

  static PluginInfo create(Path jarPath, SonarPluginManifest manifest) {
    if (StringUtils.isBlank(manifest.getKey())) {
      throw MessageException.of(String.format("File is not a plugin. Please delete it and restart: %s", jarPath.toAbsolutePath()));
    }
    var info = new PluginInfo(manifest.getKey());

    info.setJarFile(jarPath.toFile());
    info.setName(manifest.getName());
    info.setMainClass(manifest.getMainClass());
    info.setVersion(Version.create(manifest.getVersion()));

    info.setMinimalSqVersion(manifest.getSonarMinVersion().orElse(null));
    info.setBasePlugin(manifest.getBasePluginKey());
    manifest.getRequiredPlugins().forEach(info::addRequiredPlugin);
    info.setMinimalJreVersion(manifest.getJreMinVersion().orElse(null));
    info.setMinimalNodeJsVersion(manifest.getNodeJsMinVersion().orElse(null));
    info.setDependencies(manifest.getDependencies());
    return info;
  }

}
