/*
ACR-c2e689a75b6a4fd8911792c4e1048ce9
ACR-daddb1a82d4749e28a04cdd5b7ae3e84
ACR-857bd20e3ea3448c9d6071fbff1163b8
ACR-6a850518bbd649c48b413ceca22593ec
ACR-5e485c8c861b4f878ff42004c44500a4
ACR-1dc3052b6e01440cac7109e4b07d6913
ACR-1ea4bb7a30754097b42381adad20e7d3
ACR-aba23c5555e24c208021378055c2603d
ACR-d4b8f3e4dd19420fa349aca204c21571
ACR-5e6aa1fe42d9425f86891838cf1d2ae5
ACR-a502aa7ea23c44379d53e928f528d5af
ACR-f21c135a9c8641cb977703bff46d120e
ACR-4e3d16248b534c4299eb6de903364741
ACR-3123d7628e7f41ff8e151f710c503966
ACR-a8928d5ef222455b83cd5f86de89d989
ACR-c83e391fc4434eb29c8e7366988ba717
ACR-1bb2636b69a749c791a1e0ac0fc7061b
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.config.Configuration;

public class DefaultSensorDescriptor implements SensorDescriptor {

  private String name;
  private String[] languages = new String[0];
  private InputFile.Type type = null;
  private String[] ruleRepositories = new String[0];
  private boolean global = false;
  private Predicate<Configuration> configurationPredicate;

  public String name() {
    return name;
  }

  public Collection<String> languages() {
    return Arrays.asList(languages);
  }

  @Nullable
  public InputFile.Type type() {
    return type;
  }

  public Collection<String> ruleRepositories() {
    return Arrays.asList(ruleRepositories);
  }

  public Predicate<Configuration> configurationPredicate() {
    return configurationPredicate;
  }

  public boolean isGlobal() {
    return global;
  }

  @Override
  public DefaultSensorDescriptor name(String name) {
    this.name = name;
    return this;
  }

  @Override
  public DefaultSensorDescriptor onlyOnLanguage(String languageKey) {
    return onlyOnLanguages(languageKey);
  }

  @Override
  public DefaultSensorDescriptor onlyOnLanguages(String... languageKeys) {
    this.languages = languageKeys;
    return this;
  }

  @Override
  public DefaultSensorDescriptor onlyOnFileType(InputFile.Type type) {
    this.type = type;
    return this;
  }

  @Override
  public DefaultSensorDescriptor createIssuesForRuleRepository(String... repositoryKey) {
    return createIssuesForRuleRepositories(repositoryKey);
  }

  @Override
  public DefaultSensorDescriptor createIssuesForRuleRepositories(String... repositoryKeys) {
    this.ruleRepositories = repositoryKeys;
    return this;
  }

  @Override
  public SensorDescriptor global() {
    this.global = true;
    return this;
  }

  @Override
  public DefaultSensorDescriptor onlyWhenConfiguration(Predicate<Configuration> configurationPredicate) {
    this.configurationPredicate = configurationPredicate;
    return this;
  }

  @Override
  public SensorDescriptor processesFilesIndependently() {
    //ACR-7124ab2155ab4bc9ade5655a76aa1068
    return this;
  }

  @Override
  public SensorDescriptor processesHiddenFiles() {
    //ACR-ec4dc5d9f0744e729b2919b1c0f7a955
    return this;
  }

}
