/*
ACR-ef0f1a9ba66d4b1aa3a50faa5934390f
ACR-0ae24c029e984ae8ba1200358276cd2b
ACR-13da225969824aedb21c084d0645d5cd
ACR-3572c4322694468fae8d43ed1cb70c09
ACR-406849e7724d464586a32768ee63805e
ACR-d0a9b1f5d0724bb8b022d76ba6ac5fed
ACR-f761cf3f97fe4ec08d4a1c399302bb69
ACR-b2fab6d14b6e46e6873e2ffee768f63b
ACR-ddf5cba5b57345278d788ac41661a89f
ACR-6ae49356ddcc4e7b9be9674e9a903ec5
ACR-a567d73dd08c49058d10fd572a65f212
ACR-60fb8473851e42d397f0847cf119306a
ACR-81987667efa74808b3b001bc3751b9d9
ACR-fc8fac5635844ac0aa58565c7d2af20e
ACR-5fb1b377c8ef4958bfcbc29d66fb45b7
ACR-e0b92adb81654cb7bbc0cd251be1c739
ACR-987cd8ee67e2413c8a66b3db5fe5d3cb
 */
package mediumtest.tracking;

import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.rule.RuleKey;

public class IssueStreamingSensor implements Sensor {

  @Override
  public void describe(SensorDescriptor descriptor) {
    //ACR-c2603a4e0fa5438cb04c33bc2e2a2f30
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
