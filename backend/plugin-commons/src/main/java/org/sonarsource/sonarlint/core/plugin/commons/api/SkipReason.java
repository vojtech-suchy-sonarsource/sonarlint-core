/*
ACR-1a96bb9973844d88a288c4a7de599d6a
ACR-db934a5d34ca4a758ee4fcf63939ba24
ACR-dc0600cb763247d793feb9adf7024c4d
ACR-8ef331d28f2a46e6aa66b98c02c0b0d8
ACR-c35bc4a5086d4f03a29a66761680fbd0
ACR-a0479a05df1b40ad8df9a2c1c989a029
ACR-17abcfe7491b4976a257cf5fa8f91308
ACR-e69519a8269a44edbd93ef900b0e0420
ACR-a3bde469b70b4a42bb28c3bf027f9ca8
ACR-0e25e9224ed04ebfa4c9fd462ddbc777
ACR-27dc21c0447445cf939846975253c256
ACR-db80fe6c423648a6b27ae52f314a26d9
ACR-e249562b73404fa8ae31fdd88609f1ab
ACR-ac8d39f25b8740e0b8f43926f4d24fb7
ACR-3cd75bb42a47464bbdb6eaf7f81ef71f
ACR-8c33871fa8e2480784eaf47db7f5c860
ACR-65e6815f4e264d4286c1b853cf864b32
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
      //ACR-5b14293cee4c4fdd864a59972c7e154c
    }

  }

  class IncompatiblePluginApi implements SkipReason {

    public static final IncompatiblePluginApi INSTANCE = new IncompatiblePluginApi();

    private IncompatiblePluginApi() {
      //ACR-f23518c6c5ab49adad1763c6faac499f
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
