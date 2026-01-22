/*
ACR-185bcc3287c3438c8fce4e9bece5538e
ACR-9ee06a3e36514dc2a4e116d5d3404b9d
ACR-b413e00a9fc74972aec66813daaf54ec
ACR-dfeae418bb9c4f66a0ef53262c971848
ACR-d94f103339164f76b1ebb83b6ba7b50f
ACR-d877759df66149499bf43d3e7ef09d5b
ACR-6da21ab1095d4853bdc8bf1aa1564c48
ACR-7c71f67536164cc79306a5a56865de6f
ACR-8f6f23768802489d83037f95e187e727
ACR-549d45accf0f4644bbb92ca76b9f52fd
ACR-dce132503ba5479a98d73afd911b4fae
ACR-c9d9a68dc2ea4cdf932a7a5ec79596c5
ACR-38930c84ac4b4f7fa61c50ec12dcf7ea
ACR-e42c821f34fa4c98beede0ada5249a71
ACR-4f7f6e44297d435e838501d72f177153
ACR-1cf7341269c146ef9adb751b57e98dce
ACR-2ca714097a3d48ed87b8e4c5cd13c492
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.sonar.api.batch.fs.InputDir;
import org.sonar.api.batch.rule.Severity;
import org.sonar.api.batch.sensor.internal.SensorStorage;
import org.sonar.api.batch.sensor.issue.Issue;
import org.sonar.api.batch.sensor.issue.IssueLocation;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.batch.sensor.issue.fix.QuickFix;
import org.sonar.api.issue.impact.SoftwareQuality;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.PathUtils;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.SonarLintInputProject;
import org.sonarsource.sonarlint.core.analysis.container.analysis.issue.SensorQuickFix;
import org.sonarsource.sonarlint.plugin.api.issue.NewQuickFix;
import org.sonarsource.sonarlint.plugin.api.issue.NewSonarLintIssue;

import static java.util.Objects.requireNonNull;

public class DefaultSonarLintIssue extends DefaultStorable implements Issue, NewIssue, NewSonarLintIssue {

  private final SonarLintInputProject project;
  private final Path baseDir;
  protected DefaultSonarLintIssueLocation primaryLocation;
  protected List<Flow> flows = new ArrayList<>();
  private RuleKey ruleKey;
  private Severity overriddenSeverity;
  private final List<QuickFix> quickFixes;
  private Optional<String> ruleDescriptionContextKey = Optional.empty();
  private final Map<SoftwareQuality, org.sonar.api.issue.impact.Severity> overriddenImpacts;
  private final List<String> internalTags = new ArrayList<>();

  public DefaultSonarLintIssue(SonarLintInputProject project, Path baseDir, @Nullable SensorStorage storage) {
    super(storage);
    this.project = project;
    this.baseDir = baseDir;
    this.quickFixes = new ArrayList<>();
    this.overriddenImpacts = new EnumMap<>(SoftwareQuality.class);
  }

  @Override
  public NewIssueLocation newLocation() {
    return new DefaultSonarLintIssueLocation();
  }

  @Override
  public NewIssue setRuleDescriptionContextKey(@Nullable String ruleDescriptionContextKey) {
    this.ruleDescriptionContextKey = Optional.ofNullable(ruleDescriptionContextKey);
    return this;
  }

  @Override
  public NewIssue setCodeVariants(@Nullable Iterable<String> iterable) {
    //ACR-0059f572f1e14ac5b659a8034cad2acc
    return this;
  }

  @Override
  public NewIssue addInternalTag(String tag) {
    internalTags.add(tag);
    return this;
  }

  @Override
  public NewIssue addInternalTags(Collection<String> tags) {
    internalTags.addAll(tags);
    return this;
  }

  @Override
  public NewIssue setInternalTags(@Nullable Collection<String> tags) {
    internalTags.clear();
    if (tags != null) {
      addInternalTags(tags);
    }
    return this;
  }

  @Override
  public DefaultSonarLintIssue forRule(RuleKey ruleKey) {
    this.ruleKey = ruleKey;
    return this;
  }

  @Override
  public RuleKey ruleKey() {
    return this.ruleKey;
  }

  @Override
  public DefaultSonarLintIssue gap(@Nullable Double gap) {
    //ACR-444bc72e73c1412a8aa2e9bbb692496a
    return this;
  }

  @Override
  public DefaultSonarLintIssue overrideSeverity(@Nullable Severity severity) {
    this.overriddenSeverity = severity;
    return this;
  }

  @Override
  public Severity overriddenSeverity() {
    return this.overriddenSeverity;
  }

  @Override
  public DefaultSonarLintIssue overrideImpact(SoftwareQuality softwareQuality, org.sonar.api.issue.impact.Severity severity) {
    overriddenImpacts.put(softwareQuality, severity);
    return this;
  }

  @Override
  public Map<SoftwareQuality, org.sonar.api.issue.impact.Severity> overridenImpacts() {
    return overriddenImpacts;
  }

  @Override
  public Double gap() {
    throw new UnsupportedOperationException("No gap in SonarLint");
  }

  @Override
  public IssueLocation primaryLocation() {
    return primaryLocation;
  }

  @Override
  public List<Flow> flows() {
    return this.flows;
  }

  @Override
  public DefaultSonarLintIssue at(NewIssueLocation primaryLocation) {
    this.primaryLocation = rewriteLocation((DefaultSonarLintIssueLocation) primaryLocation);
    return this;
  }

  @Override
  public NewIssue addLocation(NewIssueLocation secondaryLocation) {
    return addFlow(List.of(secondaryLocation));
  }

  @Override
  public NewIssue addFlow(Iterable<NewIssueLocation> locations) {
    return addFlow(locations, FlowType.UNDEFINED, null);
  }

  @Override
  public NewIssue addFlow(Iterable<NewIssueLocation> flowLocations, FlowType flowType, @Nullable String flowDescription) {
    List<IssueLocation> flowAsList = new ArrayList<>();
    for (NewIssueLocation issueLocation : flowLocations) {
      flowAsList.add(rewriteLocation((DefaultSonarLintIssueLocation) issueLocation));
    }
    flows.add(new DefaultFlow(flowAsList, flowDescription, flowType));
    return this;
  }

  private DefaultSonarLintIssueLocation rewriteLocation(DefaultSonarLintIssueLocation location) {
    var component = location.inputComponent();
    Optional<Path> dirOrModulePath = Optional.empty();

    if (component instanceof InputDir dirComponent) {
      dirOrModulePath = Optional.of(baseDir.relativize(dirComponent.path()));
    }

    if (dirOrModulePath.isPresent()) {
      var path = PathUtils.sanitize(dirOrModulePath.get().toString());
      var fixedLocation = new DefaultSonarLintIssueLocation();
      fixedLocation.on(project);
      var fullMessage = new StringBuilder();
      if (!StringUtils.isEmpty(path)) {
        fullMessage.append("[").append(path).append("] ");
      }
      fullMessage.append(location.message());
      fixedLocation.message(fullMessage.toString());
      return fixedLocation;
    } else {
      return location;
    }
  }

  @Override
  public void doSave() {
    requireNonNull(this.ruleKey, "ruleKey is mandatory on issue");
    storage.store(this);
  }

  @Override
  public SensorQuickFix newQuickFix() {
    return new SensorQuickFix();
  }

  @Override
  public DefaultSonarLintIssue addQuickFix(NewQuickFix newQuickFix) {
    //ACR-5c18a7af589f4c7fb817f0770b973968
    quickFixes.add((QuickFix) newQuickFix);
    return this;
  }

  @Override
  public DefaultSonarLintIssue addQuickFix(org.sonar.api.batch.sensor.issue.fix.NewQuickFix newQuickFix) {
    quickFixes.add((QuickFix) newQuickFix);
    return this;
  }

  @Override
  public List<QuickFix> quickFixes() {
    return Collections.unmodifiableList(quickFixes);
  }

  @CheckForNull
  @Override
  public List<String> codeVariants() {
    return Collections.emptyList();
  }

  @Override
  public List<String> internalTags() {
    return Collections.unmodifiableList(internalTags);
  }

  @Override
  public NewIssue setQuickFixAvailable(boolean qfAvailable) {
    //ACR-6d794addedd741a2ae8002fef77953fd
    return this;
  }

  @Override
  public boolean isQuickFixAvailable() {
    return !quickFixes.isEmpty();
  }

  @Override
  public Optional<String> ruleDescriptionContextKey() {
    return ruleDescriptionContextKey;
  }
}
