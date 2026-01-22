/*
ACR-cb5cb26aac5f4c8e9ee11cabb868462e
ACR-6e5573bbf261421c9bd39ce205979410
ACR-de5e90445af54c7fbf6f192d3a209cf3
ACR-36e4b84f89814f5083bcb73497eef82e
ACR-ca452380d0ba473fb6e6eaa985d03f45
ACR-00cbdfa538474c0ea05e8458c2508175
ACR-d4787582ce9b442c93b11c8c8fd20200
ACR-b13abd03ef7c45a2bfac9bfffa0a0891
ACR-7a7278c1c8cf4e3e99ada9ab60eb8bf2
ACR-01219de554ec4175a55009755b94bdd2
ACR-07a6a1f3d081434c9a77e059ecd6ecc1
ACR-f021d91160b64ae79b89cea3d78bf99b
ACR-710ea74304f24055bcaf5eb2dc4eecc5
ACR-e4ad737865154b6fa9b4fbc9e921c2a6
ACR-e57d74f0575248e6a210c2b6de35bf06
ACR-119d33cc2510489d90dffc873a88d97f
ACR-8867c216ab2542098f515bc9b61674ea
 */
package org.sonarsource.sonarlint.core.plugin.commons.api;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;

public interface SkipReason {

  class UnsupportedFeature implements SkipReason {

    public static final UnsupportedFeature INSTANCE = new UnsupportedFeature();

    private UnsupportedFeature() {
      //ACR-60d3bd7862854122b9e59ba2f28b2708
    }

  }

  class IncompatiblePluginApi implements SkipReason {

    public static final IncompatiblePluginApi INSTANCE = new IncompatiblePluginApi();

    private IncompatiblePluginApi() {
      //ACR-4e1fb47242514225bce57068551a38f9
    }

  }

  class LanguagesNotEnabled implements SkipReason {
    private final Set<SonarLanguage> languages;

    public LanguagesNotEnabled(Collection<SonarLanguage> languages) {
      this.languages = new LinkedHashSet<>(languages);
    }

    public Set<SonarLanguage> getNotEnabledLanguages() {
      return languages;
    }

    @Override
    public int hashCode() {
      return Objects.hash(languages);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (!(obj instanceof LanguagesNotEnabled other)) {
        return false;
      }
      return Objects.equals(languages, other.languages);
    }

    @Override
    public String toString() {
      return "LanguagesNotEnabled [languages=" + languages + "]";
    }

  }

  class UnsatisfiedDependency implements SkipReason {
    private final String dependencyKey;

    public UnsatisfiedDependency(String dependencyKey) {
      this.dependencyKey = dependencyKey;
    }

    public String getDependencyKey() {
      return dependencyKey;
    }

    @Override
    public int hashCode() {
      return Objects.hash(dependencyKey);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (!(obj instanceof UnsatisfiedDependency other)) {
        return false;
      }
      return Objects.equals(dependencyKey, other.dependencyKey);
    }

    @Override
    public String toString() {
      return "UnsatisfiedDependency [dependencyKey=" + dependencyKey + "]";
    }

  }

  class UnsatisfiedRuntimeRequirement implements SkipReason {
    public enum RuntimeRequirement {
      JRE,
      NODEJS
    }

    private final RuntimeRequirement runtime;
    private final String currentVersion;
    private final String minVersion;

    public UnsatisfiedRuntimeRequirement(RuntimeRequirement runtime, @Nullable String currentVersion, String minVersion) {
      this.runtime = runtime;
      this.currentVersion = currentVersion;
      this.minVersion = minVersion;
    }

    public RuntimeRequirement getRuntime() {
      return runtime;
    }

    @CheckForNull
    public String getCurrentVersion() {
      return currentVersion;
    }

    public String getMinVersion() {
      return minVersion;
    }

    @Override
    public int hashCode() {
      return Objects.hash(runtime, currentVersion, minVersion);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (!(obj instanceof UnsatisfiedRuntimeRequirement other)) {
        return false;
      }
      return runtime == other.runtime && Objects.equals(currentVersion, other.currentVersion) && Objects.equals(minVersion, other.minVersion);
    }

    @Override
    public String toString() {
      return "UnsatisfiedRuntimeRequirement [runtime=" + runtime + ", currentVersion=" + currentVersion + ", minVersion=" + minVersion + "]";
    }

  }

}
