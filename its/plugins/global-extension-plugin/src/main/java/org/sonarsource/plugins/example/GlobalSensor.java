/*
ACR-eac8b29a7fc346d2b93b922aeae4533f
ACR-2ae159fe754c4f608d7c22f5c9592e32
ACR-82bc940a9a5643889f82a2d1f3e4e729
ACR-dc5346248ad445b5835a7227961e1f69
ACR-e0d416ea7f46479fb5efc5b7990963ce
ACR-3a222a44669b4dbfa9101a5b83ea4a23
ACR-383e73aca5ab4c15a0fc81db89e844db
ACR-40caada90b004dbf82eb7c40b048e503
ACR-e3dc2413cedc4ab99c47c7eef5d49b14
ACR-dd72d5c098ee467b97c9898432a71568
ACR-35b5c9cd62724d3185d53e1c6f429e8b
ACR-0102245588d7482e8300bcb8cc6f37c3
ACR-616936a162f644f6bc9557acf662fb38
ACR-1facc7d8d11f4ef785c6f15b73da0a08
ACR-06b1a4be0fff47bd9ad3a77a7e916495
ACR-ba589e5c904e4815bfcb68f39765c087
ACR-898c5167335040c1a6fc85c0fd28315d
 */
package org.sonarsource.plugins.example;

import java.time.Clock;
import java.util.Arrays;
import java.util.stream.Stream;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

public class GlobalSensor implements Sensor {

  private static final Logger LOGGER = Loggers.get(GlobalSensor.class);

  private final Clock clock;

  public GlobalSensor(Clock clock) {
    this.clock = clock;
  }

  @Override
  public void describe(final SensorDescriptor descriptor) {
    descriptor.name("Global")
      .onlyOnLanguage(GlobalLanguage.LANGUAGE_KEY);
  }

  @Override
  public void execute(final SensorContext context) {
    long timeBefore = clock.millis();
    RuleKey globalRuleKey = RuleKey.of(GlobalRulesDefinition.KEY, GlobalRulesDefinition.RULE_KEY);
    ActiveRule activeGlobalRule = context.activeRules().find(globalRuleKey);
    if (activeGlobalRule != null) {
      Stream.of("stringParam", "textParam", "intParam", "boolParam", "floatParam", "enumParam", "enumListParam", "multipleIntegersParam")
        .map(k -> Arrays.asList(k, activeGlobalRule.param(k)))
        .forEach(kv -> LOGGER.info("Param {} has value {}", kv.get(0), kv.get(1)));
    } else {
      LOGGER.error("Rule is not active");
    }
    var inputFiles = context.fileSystem().inputFiles(context.fileSystem().predicates().all());
    if (!inputFiles.iterator().hasNext()) {
      LOGGER.error("File system is empty");
    } else {
      for (InputFile f : inputFiles) {
        NewIssue newIssue = context.newIssue();
        newIssue
          .forRule(globalRuleKey)
          .at(newIssue.newLocation().on(f).message("Issue number " + GlobalExtension.getInstance().getAndInc()))
          .save();
      }
    }
    long timeAfter = clock.millis();
    LOGGER.info(String.format("Executed Global Sensor in %d ms", timeAfter - timeBefore));
  }

}
