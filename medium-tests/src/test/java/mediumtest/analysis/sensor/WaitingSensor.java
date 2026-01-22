/*
ACR-d66bdf63037047b8a2fbdcf52f9bcad3
ACR-3a59129a045e47efbbdf6711f60e156f
ACR-7bb1593e9b184b7088e81be444eb0ce3
ACR-d6964db275534de1b70caaef064bf7aa
ACR-89591f406db84355901ca081dc7ad276
ACR-697609d8a9eb4e61907298435367c275
ACR-6c952614d6a247f19ac769073b872919
ACR-e962387d4e634181b9bfba8ef2a5a760
ACR-87f537b635bb4b98bf13126af8adf2a7
ACR-4130178b01ee487e8615b232f1fe807b
ACR-210d52c9bd084ae1b6547cd09843e656
ACR-926d1216373f4f14a2a17e97769cb8c2
ACR-d895f66883f7438293b4024d44a8a70b
ACR-66c927db7d354a238bd6fa3436dec67d
ACR-101d2e184e6747adaa692425913454ba
ACR-c54e24f2b12a4b3189e465bccdb32674
ACR-9d4ce00d1ff14b78a76aee0ed647f691
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
