/*
ACR-898d33a7e4d448f7ac54162d433564e1
ACR-bc5dcbd7c22b480881237a6261cdc379
ACR-216819db211c442a9dfd32cf312c62b1
ACR-cf605fa4abd54c7484a6a96c0d2b61f4
ACR-e76944ad08e94e69960d95b392643eb9
ACR-13644e880fa444e592e043951a7fe538
ACR-c3540cfa29184a23b32297926cdbe3f3
ACR-a43385b0613945e6ad33beca6501148f
ACR-49bc052d39dd4546992b074978b28aed
ACR-877fcc0f81ef478e86addc0e0ad1464e
ACR-42067f14171e49c48579e13cd4617d95
ACR-b9099b53b17441e180644665b0a22b8f
ACR-235456665baa422990d5ed30d562002d
ACR-17a41c9e64184523aa87f646d1f33e97
ACR-347590da5f8d4efca43bdb16ba864297
ACR-1967aa1636404dc896a28ceebd2dc749
ACR-640c7f1c3de54be0a7452d7f2d1027e5
 */
package mediumtest.tracking;

import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.rule.RuleKey;

public class IssueStreamingSensor implements Sensor {

  @Override
  public void describe(SensorDescriptor descriptor) {
    //ACR-9dc4c81374544c58bc0ea42dfe5fa7f4
  }

  @Override
  public void execute(SensorContext context) {
    raiseIssue(context, 1);
    pause(500);
    raiseIssue(context, 2);
    pause(500);
  }

  private void raiseIssue(SensorContext context, int issueNumber) {
    var newIssue = context.newIssue();
    var newIssueLocation = newIssue.newLocation();
    var firstFile = context.fileSystem().inputFiles(file -> true).iterator().next();
    newIssue
      .at(newIssueLocation
        .message("Issue " + issueNumber)
        .at(firstFile.newRange(1, 0, 1, 1))
        .on(firstFile))
      .forRule(RuleKey.of("repo", "rule")).save();
  }

  private void pause(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
