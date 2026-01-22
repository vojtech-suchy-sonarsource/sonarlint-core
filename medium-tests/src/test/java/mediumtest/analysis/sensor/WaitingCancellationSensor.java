/*
ACR-5579364cc5ac4089a70c5a8e8b80210c
ACR-bee30a980a1c4856a6ece8f8d4142812
ACR-1317ba5be4774a6eba57a59d0ac1a546
ACR-df423a6105bc467f95a11c61f893f1b8
ACR-744e7c33c0ef4d92a6c127b54d5ec870
ACR-0802e149b5ec466a9487108a40e49871
ACR-e0f4d8cbc7124896bcc4fa864289a439
ACR-a46b6386249e4383a070ef0ad63a1502
ACR-9f04b84f31d74eae9c783d7975a6dfa5
ACR-372cf44b57424b6dbdf113343556e0bf
ACR-1f95960cdcd844fd903da04be010266f
ACR-3e035ab99a1a48d09a9ad6b336d04638
ACR-b3dc22c773434f878b369068f4f321d6
ACR-d9175ad83df24c58b330baea9967db89
ACR-63017ed1f933426e960129536f17b62d
ACR-7c58f18fb9804b53b2f4fa37f71aaa83
ACR-902bb7c3f4f142f59d1c26493c02b0a8
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
