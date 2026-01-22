/*
ACR-91813777c677494e8e1c080dc827df05
ACR-1c4e8a3189c548e2bdb38f140b44607b
ACR-1a7ea3037ba1403cbbb1e232af4b0956
ACR-3ee07f029bed493388ea36bbe1d2bf59
ACR-de1ddb960ff14bed86958634d019231a
ACR-d26d47bd7d4143e5b917d2dace61247b
ACR-074b63106346496c94b1b4f90b3f1e8e
ACR-b976742632e64bb8bb03ebc79ab9e114
ACR-24eb9d4e3dd94f21ba37eaa44eb0f2e0
ACR-db40823d6a0d4a219e17f72f77ecf961
ACR-467e2ca257014069bc926cc3203e2871
ACR-ac931898ed6d40c4be7daf7a20d19498
ACR-0ac9e3a149d24bcbb899efe7173dd90c
ACR-fe6820002ac34cf0bebce468005f2c8e
ACR-7c06482c12c3450e8695e960bb70cec4
ACR-83249f31abd64d8ab7944e0a4e5c220d
ACR-1c52396e68bc404293c0f5680d6971af
 */
package mediumtest.analysis.sensor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;

public class WaitingCancellationSensor implements Sensor {

  public static final String CANCELLATION_FILE_PATH_PROPERTY_NAME = "cancellation.file.path";

  @Override
  public void describe(SensorDescriptor sensorDescriptor) {
    sensorDescriptor.name("WaitingCancellationSensor");
  }

  @Override
  public void execute(SensorContext sensorContext) {
    var cancellationFilePath = Path.of(sensorContext.config().get(CANCELLATION_FILE_PATH_PROPERTY_NAME)
      .orElseThrow(() -> new IllegalArgumentException("Missing '" + CANCELLATION_FILE_PATH_PROPERTY_NAME + "' property")));
    var startTime = System.currentTimeMillis();
    while (!sensorContext.isCancelled() && startTime + 8000 > System.currentTimeMillis()) {
      System.out.println("Helloooo");
      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
    System.out.println("Context cancelled: " + sensorContext.isCancelled());
    if (sensorContext.isCancelled()) {
      try {
        Files.writeString(cancellationFilePath, "CANCELED");
        System.out.println("Wrote to cancellation file: " + cancellationFilePath);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
