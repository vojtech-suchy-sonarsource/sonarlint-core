/*
ACR-6b84c3bd08a5462ebcf5136994231402
ACR-fda2194c38e643868c163eee1e65faba
ACR-7902c9ff38d04207a968dd26d7502a34
ACR-f14ddec8358e47dda65f48a706a97325
ACR-3822edfa71254ebeba0bcd9e1a589bb7
ACR-b2218257f1df448687f377fb4dc292da
ACR-6a67e6286eaa4744b9be0cb7a48ad3ab
ACR-0c0ab7848b7347e195a3bed749adeb6a
ACR-6d830a3369a545f9b3a7e6f0a1ea3ed9
ACR-157fbd11c2704059a47a34d72d09ac9e
ACR-0c802a38856d4b9fa02bcd575988a8b3
ACR-48acef67d5974c16b38b9573a50d4482
ACR-3bd74cbdf2ef4ba6a6f0a4ac251d1bb0
ACR-07cbf6b3fb324e3d91d97d567ee0f918
ACR-d8504e2f10fd41ffa6547a15580a7385
ACR-318149f24d054e12a069c99cbf5cc259
ACR-27cdae9c0fdd4e3cb41b7ce4cda71adf
 */
package mediumtest.analysis.sensor;

import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;

public class WaitingSensor implements Sensor {

  @Override
  public void describe(SensorDescriptor sensorDescriptor) {
    sensorDescriptor.name("WaitingSensor");
  }

  @Override
  public void execute(SensorContext sensorContext) {
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
