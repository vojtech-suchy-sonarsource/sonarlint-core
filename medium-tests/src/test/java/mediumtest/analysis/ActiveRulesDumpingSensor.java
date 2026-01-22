/*
ACR-7c51c32aa9094ee3a6416569ce3b471c
ACR-c5ace213d82e464380ff4463ff6be44d
ACR-e6c3e78cba584cf085bbba3ca61281f4
ACR-dc4ec920970d4f63ad92b11610c91560
ACR-dd7f00d0536e4fa7855fed5b4cca6272
ACR-3711645d8c1d4c42b26ab9a1499fc75d
ACR-d067ab9dc81a46568b579a471612fe94
ACR-2f4c2c4abdfd47b1afcac81d66135f35
ACR-d6a4d2056fec4b61b8a12a4ce6f50606
ACR-ced878b0cb604b18a6f2399fb1d417e9
ACR-fefdcf666d364a9b8568104e457bfe3f
ACR-cfe1be54ee0f4b7cb567113d5271be49
ACR-01e1b75cc0d846499f36f199c152c4f5
ACR-efb905fa8b2244f19770928935969362
ACR-8bbfe6c0edf947f68158d69621972933
ACR-6aeb3de69f5e403283d1f983a939dd71
ACR-fd5ce96015dc43588e2da54b019e9cd9
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
