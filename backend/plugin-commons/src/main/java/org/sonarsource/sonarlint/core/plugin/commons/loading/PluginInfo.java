/*
ACR-a512df2987e646f3ba2aea56dba0a246
ACR-e89190f6c6c248e494ff930c05f0b321
ACR-cf4d1000f65d491bae12d9e1815e9013
ACR-6d0f305cb3b74c6191f44da6949ac940
ACR-8b8a0b7d4d2d4fa6be59634adb91cc0d
ACR-30b48a4bae524003ba7d45f9ce6b3a6c
ACR-5d0cf528d87e435db9fabaaf5bf31c55
ACR-472f5261f7894da4988f2d52d83638bc
ACR-c02d39a953a94bbab856c6e3e2b8ec5b
ACR-1602718f6bfa44adb2262b25ffe13c6b
ACR-9609a3cb09ef48bcb7a7b2f979168ca2
ACR-62b7e7f45b904468a881dba0b8110932
ACR-b344661122a4486986edbc808a8ac126
ACR-e0cf0e319d32464687defa948f78a3af
ACR-7a3c0be2ac3147daaf3d4aa9496d6619
ACR-66fb2256e1834f829e331134a587af73
ACR-66333a8c34614528a5df6f470647effc
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

  /*ACR-8f6e78c6354d4fc09f0eac7db37a098f
ACR-fbec3b9f370a439aa1f36845a90d05f6
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

  /*ACR-b3326e2123e249e797cf58775a75aa29
ACR-ff9a584e8ddb494f9d3cee3b9dd62030
ACR-2defe7a925d04e48b2720e677a1b10dd
ACR-8ad782b8f3aa48028b31f992183fb57d
   */
  public boolean isCompatibleWith(String implementedApi) {
    if (null == this.minimalSqVersion) {
      //ACR-a41537df4edc415c905500ae3de5d5e3
      return true;
    }

    //ACR-eafefe7bb382405eadc0dc301467b062
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
