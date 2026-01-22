/*
ACR-2da89711e7264c439d1b4a105c41461d
ACR-eed082c3840b49969c4a2c14b47a92aa
ACR-4d20494b55c642cf8b77d523157e0d62
ACR-ae7603795e3043efa6d96517238e44ed
ACR-01f67838e3ed419d8848dddc016a7656
ACR-70f9cacc0b2e4ca79e0d50ec0a1bd153
ACR-bfaac975cb6344b79b0dab25e81cd594
ACR-08bd5a810ed546f993361e7989a8fddd
ACR-0a03618579fa47e4991586e5956e95ed
ACR-0f7721a5f56d4d4cabdd1e5133ae5c79
ACR-0aaf379e00bc488ea209fbf916bceafe
ACR-ef30904f7a4e47cfb6281430bc724831
ACR-625a0d0114194df485665b5d320a233e
ACR-65e51aa5436e413da00d8ab1347718f0
ACR-6e15e73b15f4467b8666f0588b4add26
ACR-2995e5e09d644d9389f17cecdf8a2554
ACR-9ea281e8b8994fcc887682c16fd556aa
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
    //ACR-00402245af264c62896cba0652466a07
    return this;
  }

  @Override
  public SensorDescriptor processesHiddenFiles() {
    //ACR-94e1bdf089c14142a9849e4cc171c3fa
    return this;
  }

}
