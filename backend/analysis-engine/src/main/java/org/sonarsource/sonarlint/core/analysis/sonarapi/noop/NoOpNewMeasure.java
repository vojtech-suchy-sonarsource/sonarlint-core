/*
ACR-78280ca68d4b47c7933cbf8dc64c4b58
ACR-9a1518685b0e4be5aca6efd298e6535a
ACR-e0c8ff43a5264bb7a6fdde0c62bb5a11
ACR-d2b3b7f6e2e742c18813be36c43e56e4
ACR-80321bec492441f99bd5eaa59a1b605d
ACR-2b63931a1e79445eb5e20ef995e5f05c
ACR-41193219d2cc4e1ca8c0c31f230624f5
ACR-bd690764488c44ddb6919356676f64d8
ACR-0a7491ebd2e3402e873ea4a5316c0418
ACR-5706bcfdeb204e5f8dee2e1274ee488b
ACR-cfd8ee75d21845619d8998b3036048e5
ACR-d7039929222e4553bd5108ba77eaa3eb
ACR-b90d84f20fec483c9a34e153dd574b81
ACR-3d79a31862f94956a383a9bce73e1df1
ACR-a36650f6bdef4b97b6307ea246201cc7
ACR-685f5ea1206845d9a6071f3736875581
ACR-57cfc36ee4a045bdb54e681b864a59e3
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi.noop;

import java.io.Serializable;
import org.sonar.api.batch.fs.InputComponent;
import org.sonar.api.batch.measure.Metric;
import org.sonar.api.batch.sensor.measure.NewMeasure;

public class NoOpNewMeasure<G extends Serializable> implements NewMeasure<G> {

  @Override
  public NoOpNewMeasure<G> on(InputComponent component) {
    //ACR-b456e25b8f404589b7528e05f2550693
    return this;
  }

  @Override
  public NoOpNewMeasure<G> forMetric(Metric<G> metric) {
    //ACR-35542dd79f044344a1682bc2a4b94105
    return this;
  }

  @Override
  public NoOpNewMeasure<G> withValue(Serializable value) {
    //ACR-dd93bd3e4cfa4a3cb7cdb194cbb15469
    return this;
  }

  @Override
  public void save() {
    //ACR-bf305165963d4fd988904e544bd9c35e
  }

}
