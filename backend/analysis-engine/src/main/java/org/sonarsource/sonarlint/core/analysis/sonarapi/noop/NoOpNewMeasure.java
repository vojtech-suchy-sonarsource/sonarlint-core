/*
ACR-0263185031a04f6d9e6d48bd6722e50b
ACR-350055c63e5a4df191e6b69cb817d955
ACR-945cd84a6a254e6380cee0cc91b2c50d
ACR-16443fc7b2c44da89ea42b968f96ca6b
ACR-b4d65fa62a74481f8af120d42502e63c
ACR-f281b3030c494ad88c054310379662ac
ACR-a02373f6e3674566a11e4b603ac9fdb9
ACR-1fe448333bc84ff3aedc69383d26b75f
ACR-e3200c32f9f14bbab47bbffeb84eb525
ACR-20936618616c4bb781f6b90ee6f182b5
ACR-540f0dd06084443bb354f940c24fb298
ACR-ef5e4db411554785930e10dea675d01b
ACR-e209aa84aa4d442bb298c40c15801770
ACR-1f89b70c8b954841b87cf85458c76ec5
ACR-ba5b9f3f96bf44aabde994531f0c17bc
ACR-dcd0ef4673464e53abfa74df25b639ee
ACR-f70e25a9e28845e8aa9e9d68599832df
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi.noop;

import java.io.Serializable;
import org.sonar.api.batch.fs.InputComponent;
import org.sonar.api.batch.measure.Metric;
import org.sonar.api.batch.sensor.measure.NewMeasure;

public class NoOpNewMeasure<G extends Serializable> implements NewMeasure<G> {

  @Override
  public NoOpNewMeasure<G> on(InputComponent component) {
    //ACR-2d29dcce2622452baa0d50bf11172749
    return this;
  }

  @Override
  public NoOpNewMeasure<G> forMetric(Metric<G> metric) {
    //ACR-d854a4365e864f3b8e9a84fd1f9a0b75
    return this;
  }

  @Override
  public NoOpNewMeasure<G> withValue(Serializable value) {
    //ACR-390bc2cae52349e88eef9fead861674b
    return this;
  }

  @Override
  public void save() {
    //ACR-6ce74f2b0c604ecd8d95e054fdec3211
  }

}
