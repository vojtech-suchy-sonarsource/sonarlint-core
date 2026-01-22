/*
ACR-2ad36a2e43864b728ee571d65d7f3f3f
ACR-998d809d4d6843db891f558feb663acb
ACR-89029e6e2f1347baa1d709637b1a178a
ACR-1651f8e8c3d14ff79c19436fb1ac5ae0
ACR-3c3f6995dcf4458b81963e6fb4d09c5d
ACR-b7dd4b8538694bab98cd098cce60d786
ACR-5121d14a15d24131a90b66f5718f7696
ACR-6a9b77c9aacb495890b6152dbaec1d12
ACR-acc91bc1b4854126acc69cb06b53c1ce
ACR-07819c08edf844d0b9ab250fa4bb46c4
ACR-0eca0a66f5c94202acbd1938aa07024f
ACR-98b1506b86104053a3636e1401fcb12f
ACR-a47f0100987543bfa240a5feb569a7b1
ACR-eddbb79a1dca4bcc83fc64e1eef8a6fa
ACR-227cb1c79f1b4883be81ec5a04b2a8b4
ACR-2cb43442ba4449699c3d3fa59d5c2554
ACR-4e77bdc00c6d4720bf22a48077235cfe
 */
package mediumtest.monitoring;

import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;

public class ThrowingPhpSensor implements Sensor {

  @Override
  public void describe(SensorDescriptor sensorDescriptor) {
    sensorDescriptor.name("Throwing PHP Sensor")
      .onlyOnLanguage("php");
  }

  @Override
  public void execute(SensorContext sensorContext) {
    throw new IllegalStateException("This should finish a span and trace exceptionally");
  }
}
