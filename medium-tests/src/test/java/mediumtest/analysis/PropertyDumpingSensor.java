/*
ACR-2bac6c9d0834408abe66dce3322082d7
ACR-c2439b0b182f4e0b96bb9c263866155b
ACR-6fa6d568bb704589a7739c18d1dc1c1c
ACR-456847eef54148f487a2b5019a22e00a
ACR-dc1259ed1bed4f3584b2cc690acfd397
ACR-17c360e4cec74f05b7bafcfa2bbbd506
ACR-c151637bcfc24a2497f30f1d7a94aeff
ACR-6faeabfd383d4f2e9520ad49a0008183
ACR-e44cd52cbf3c4fcc87a03dffd9c009af
ACR-c86549f2a4b640c28abb6a3a207d3daa
ACR-4abfd3c01ab94c6f83c73c42fd485518
ACR-513224f4e8674ad4b634b2b5d97c606d
ACR-1278060d70e84be99cf8129496cee475
ACR-e33e6a17943e4472a6404c2beece6fe4
ACR-fa5c94d19af44830b332e88802c60c49
ACR-5bdb8e0dc3e44cb59d433dc6d71cd366
ACR-ebea3cf414bd4605a5a9bf1cd5b608f5
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
