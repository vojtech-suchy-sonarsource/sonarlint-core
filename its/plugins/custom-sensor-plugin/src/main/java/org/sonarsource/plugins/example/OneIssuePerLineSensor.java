/*
ACR-9de4c3e64b6b48ae9407002d1cfbdf60
ACR-3c3493b23cd24a62b81734577852e27f
ACR-3760ae7f43e74fba875c4f1c3e4b9b27
ACR-14097384ca1d41fd92060437869f3501
ACR-2a1d37cd9820455098cf20274099c62f
ACR-ff1980f3bc794e4d876e03d0d86989c3
ACR-900df86aa60b49cbb6e438dd6ba3a67c
ACR-9cf49a07da5441ea87df7443609740ac
ACR-a2055c663f8147e69d70f5fba4f492f6
ACR-db7e8e8b1de34941b98068789b3ef7e9
ACR-e620a3f9bc754f7dac16f78cdaad1225
ACR-7893d11de4f54241aa57ab73fed97477
ACR-97700a571ecb4ff5ab11003c7d02af14
ACR-a6cb8cb48a7448a1a33409d14174c9fe
ACR-b7dfd240762f43a7924b71826ece8d7a
ACR-d2460e59aade4b70b44a03e588c94d71
ACR-228619f4c1574558ab1966afa0bfc7dd
 */
package org.sonarsource.plugins.example;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.rule.RuleKey;

public class OneIssuePerLineSensor implements Sensor {

  @Override
  public void describe(final SensorDescriptor descriptor) {
    descriptor.name("One Issue Per Line");
  }

  @Override
  public void execute(final SensorContext context) {
    for (InputFile f : context.fileSystem().inputFiles(context.fileSystem().predicates().all())) {
      for (int i = 1; i < f.lines(); i++) {
        NewIssue newIssue = context.newIssue();
        newIssue
          .forRule(RuleKey.of(FooLintRulesDefinition.KEY, "ExampleRule1"))
          .at(newIssue.newLocation().on(f).at(f.selectLine(i)).message("Issue at line " + i))
          .save();
      }
    }
  }

}
