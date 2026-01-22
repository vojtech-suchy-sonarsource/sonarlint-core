/*
ACR-9050997fb78642de871af98dbc6131d5
ACR-72b9ed272d9a4f95a1b45c515f543842
ACR-4aceda8374db44fca302b3059bc24d92
ACR-6f7cece2fd0a4e828e82b89616669da2
ACR-909e562e08c24d2190d3d2bf728bca87
ACR-53680dbdfd414561b6ea123818d3a51d
ACR-79905de1f2984e34a2a132f31527c41d
ACR-0d23a9a6e76648c4b7dc98246eb463fc
ACR-f43ff2d28e384ec4b660ec5a3573b2d1
ACR-bd097f7236af412394d487016456c07d
ACR-8dd7973428464fc880789609190cb6d1
ACR-34177ca3cd7b4b3d89d126b9b4e62ab2
ACR-9b0558150c534e9e8b4a90750307448c
ACR-2ec7124710d24ca4b824e696be2e590e
ACR-53721085ccae4963ac76f8950441c94c
ACR-163fd31f519d492daed0009ace85d803
ACR-8ca9279a19c749f6919389ceec3b3215
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
