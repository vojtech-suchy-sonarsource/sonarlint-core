/*
ACR-5be381a49d3240c985334d3cc6c9a514
ACR-a58583079d5c41bd85f2258194bc48f7
ACR-c7a665830e1046e59e8f0f1dc6314921
ACR-27567921c3d6439182b76db2c875d8f5
ACR-5bb9aada75d14b67a591361276bfcc01
ACR-d6b34eb7292b46d3b119e78ab507b977
ACR-0f2cd8a6c38740d99f62464aeb6ee415
ACR-b23c67d113924b569994222e0e7c0399
ACR-0c9346be954346e8b0c8fecfaf98f097
ACR-db483281e29c4800b442d290171bcd5b
ACR-945dbdc0470e4b618928a00b6927c6ab
ACR-d86096531ca64aeb8aa8ca0548e1ae61
ACR-4d689ab7f8494f18b2a17e15b2e87fa8
ACR-d6bdaeb8401e4d9d81606b0022647b68
ACR-94dade3bf50b424da93a23e80675ff02
ACR-a9c29b89f2224f96a71ce61c4e84b107
ACR-c6bf80ca5ec547af89bd005c17d5f8ff
 */
package mediumtest.analysis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.TreeMap;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;

public class ActiveRulesDumpingSensor implements Sensor {

  @Override
  public void describe(SensorDescriptor sensorDescriptor) {
    sensorDescriptor.name("Active rules dumping sensor");
  }

  @Override
  public void execute(SensorContext sensorContext) {
    var activeRules = sensorContext.activeRules().findAll();
    dump(activeRules, sensorContext.fileSystem().baseDir());
  }

  private void dump(Collection<ActiveRule> activeRules, File baseDir) {
    try {
      var activeRuleStringRepresentations = activeRules.stream().map(a -> a.ruleKey() + ";" + a.language() + ";" + a.templateRuleKey() + ";" + new TreeMap<>(a.params())).toList();
      Files.writeString(baseDir.toPath().resolve("activerules.dump"), String.join("\n", activeRuleStringRepresentations));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
