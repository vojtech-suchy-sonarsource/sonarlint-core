/*
ACR-a91f34efb546426f8d831f53ba1c01b5
ACR-12b21d6584344e6d9140c61f05f72583
ACR-a661c886cdf54d749102a6b5a90cbce3
ACR-e679aa8c9b814fcfab5e57ecc46d9dad
ACR-903f3165c7f14178ac021050f8ba6f46
ACR-7594aa46db31488e8564aa5ae1c6b96d
ACR-7143121e7c204e0281eee85810254064
ACR-d0f692d7da1542be8b0cbe7a1ce3b680
ACR-5dfdc6b280894716beab71196e427a13
ACR-9d478e2d2315414790ac621fb14d2df2
ACR-dff9c8011d4447e9ab3294dba5d3c40c
ACR-9b3bda98eaf9431f950e0b43a07ff5e2
ACR-2251b8167a9d4b8db5087307fd8474ab
ACR-1874d290f6264a67a31308cf04f1d306
ACR-97233d72e2674cd9a0dee5277288e693
ACR-57b031489603490d82d7d5e982df97f7
ACR-861934738cc94a2584576dc238d793cc
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
