/*
ACR-a61e5bd8e4584b22948c703a71e2544b
ACR-e7ad00e88693467a9e4f7ce17e356b7d
ACR-792fec5e861c48c3a09761f488f45888
ACR-1667983c295842e3a139f533dade6ab2
ACR-bc00ba79a98a4ab5b5124245733369f6
ACR-27d8e0533e334803b06e548f837874ec
ACR-c21ec96f792b4e5f8d6d1def5540d4c3
ACR-762fc4ac2d154cb8b3003551f1feb691
ACR-04779f92933a4c16a707a33a24ab3e8e
ACR-3c9cf1339d2741058cf2e132b3cba5f8
ACR-155d76c99b954d1791be8503cb21b87e
ACR-4c42f26baf2d4934a3a05ed5d0960d80
ACR-ca45ed7ddde24df2b0f2ccf412c95c42
ACR-2746af7760484fc6b33264ada53bb631
ACR-2b02801a7dbe43939d9239fdda977169
ACR-28c4d7441495437d87eb7b5a094fcb9f
ACR-7b86c7c3f03e457485f19f22f436cce4
 */
package mediumtest.analysis.sensor;

import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;

public class ThrowingSensorConstructor implements Sensor {
  public ThrowingSensorConstructor() {
    throw new IllegalStateException("kaboom");
  }

  @Override
  public void describe(SensorDescriptor sensorDescriptor) {
    throw new IllegalStateException("This is unreachable");
  }

  @Override
  public void execute(SensorContext sensorContext) {
    throw new IllegalStateException("This is unreachable");
  }
}
