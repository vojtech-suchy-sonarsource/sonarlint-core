/*
ACR-bf00ad482b314e5392232bb720a71b65
ACR-87bbe1c8b35e4f56966767da8a44ad14
ACR-ec91946580ed48a0af11a165eb7839d0
ACR-f84bff735ef944ffa3527b3b15e08127
ACR-8f10b24b82cc4521b8e77967da2059ad
ACR-7961236eacbb476abb337b29f427c7d6
ACR-beaebc193aa845a0945ece4b3b4f6a73
ACR-e18b5e0c185d4b63bf4ef43f18660349
ACR-12dccb76f6ed4be792e4fbb9ca2f99db
ACR-956df72900014797b05da111540414ef
ACR-5a5f6e6dae034893bbb29bc76548353d
ACR-8a47e10952ed45a8b76f05028891b11a
ACR-6600996ee12b4d10aae0c3684f9bf994
ACR-602b1cdbeaea4340a43fbd41b0541f15
ACR-ea9ecd31fb7645ca97d07849405b911e
ACR-d6a9232b3de646f1937fbe66d7a1f9c9
ACR-2f3fb528830243c5975673d82119497b
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
    //ACR-33a76be7f5864c76ab9d6eff40edb370
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
    //ACR-4eaa160614ab458b83dde71216cb7801
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
    //ACR-4d5d35c5a9b14be8baefb389ddd72bdc
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
    //ACR-096a874331464b01b8cdc1c49eb28db2
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
