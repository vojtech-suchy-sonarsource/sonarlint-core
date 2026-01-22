/*
ACR-7cd5d284f70a475492bbe48b12d6b1d5
ACR-ba7576d229484452928c75aa4375df18
ACR-9de903069adb47b698bcae76783681ac
ACR-cccfc889b5254fa2832ade19597b5317
ACR-565e54978d804fc78117e9de34d16ae1
ACR-f47d4c23d7d34645b827e176bdc9b6e6
ACR-d9486d9092344f8e98ba9c38b553347d
ACR-c9470d445cc14cf4baee4661ade3349c
ACR-73530caec48d4c078e94628789ea687e
ACR-169e4905789d462fbb7d16a1f4eacaa7
ACR-2563b267d0d14507b6fc6deb004853d6
ACR-ab2c373536fe4cd9aae493dade8f3add
ACR-6f6c1bb79dc649f8852216b3971af076
ACR-bb474b65a27f4ecd9f9091cff165bfd8
ACR-05f0f12de8a54ad0a74fdda7a591f187
ACR-386df1a8ceca4dcc8869e9fc1cdb7e62
ACR-3c6382b51a03451eae0ef8494d6f637c
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
