/*
ACR-528841dab2d945d8bc28d3a236a6fdec
ACR-35382fe86294423f9e7290341fab62c8
ACR-79c6eb4db7d14be0a8f35475607ae2ac
ACR-c6a1a2ab2487470d9f64ef1115a539a2
ACR-2f5a8f9d87de4b1facd2864188570f33
ACR-3d2ae3308f914317a0982bdabf461884
ACR-ed502249d76444ff9eb7f0aaf7be3ede
ACR-6990102300274bf69763fa248686f39e
ACR-2c79ca0050574684a9ad76a79332d2c6
ACR-22fa60989f3e4f298574784f80de21ee
ACR-79b4ecc1f5bc418ca0ce4abe4efdb934
ACR-c3d97768d3ba431fbab16f49a65a4e5d
ACR-3d0abd060e6641cbae751e1d76a866f7
ACR-6c372072133642d5b99d65a4f8c43281
ACR-13c85fca275d43cba475fd4ec0e77cea
ACR-5de0ffe7c5114b0eaf6ba96f7d764002
ACR-aa25a6cf6cee451fbee9238a455c6004
 */
package mediumtest.analysis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;

public class PropertyDumpingSensor implements Sensor {

  public static final String PROPERTY_NAME_TO_DUMP = "PROPERTY_NAME_TO_CHECK";

  @Override
  public void describe(SensorDescriptor sensorDescriptor) {
    sensorDescriptor.name("Configuration dumping sensor");
  }

  @Override
  public void execute(SensorContext sensorContext) {
    var propertyName = sensorContext.config().get(PROPERTY_NAME_TO_DUMP).orElseThrow();
    sensorContext.config().get(propertyName).ifPresent(value -> dump(value, sensorContext.fileSystem().baseDir()));
  }

  private void dump(String propertyValue, File baseDir) {
    try {
      Files.writeString(baseDir.toPath().resolve("property.dump"), propertyValue);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
