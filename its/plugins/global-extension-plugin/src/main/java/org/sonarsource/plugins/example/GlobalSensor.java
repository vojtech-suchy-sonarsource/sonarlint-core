/*
ACR-cc768d8a8213454eaea54096ca33a956
ACR-5df6a9b7d5e04881987a3f2e73a5b2e7
ACR-03acf643a2ef4c90a4fefc58f5b57bf8
ACR-4308ca9b7ea44018ada6b1079204c54a
ACR-2dd800979a5e4d719417e5dd521a18f9
ACR-1955a7fa5c7b48a881b3a6cbc525aaa7
ACR-1438a113ec354850b045deb91c2dd51d
ACR-fa5f87484f2e4eccb31663e8dcb32254
ACR-e2fa041dcdc84247aa28f41481d55e4b
ACR-554d895f210f4ed582e11a75800c18a4
ACR-8a18620dd15c4c1eb68ecef2dd20a593
ACR-a8895f35ec1845fd8c365de79c0ba29a
ACR-c8073db8b1c544cdaed3a82cdab9c2b7
ACR-804c639285fb43c1bf0e1690e9fbe881
ACR-6e29dee099934db3a9012d609c6b4251
ACR-9688e633efc44ff18ededbe5b7c10d3c
ACR-f4a9ce09d9014221b32c20f22bd4195e
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
