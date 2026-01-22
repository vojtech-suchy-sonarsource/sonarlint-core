/*
ACR-b7eec67508c44c808a27460678f54b78
ACR-3778a462a9a741d88b4b0fac83dfd501
ACR-aa30b9f0d18f4f85a74704a6fcdd1046
ACR-06039a98213d4b2d807d1363f889dba0
ACR-36b30009d6f6446eb13f43fa70c6a6af
ACR-1a82606c3f2e4ce089354db36d75b35a
ACR-c4c02c9956cd42eb927a51f8632d77ea
ACR-46a72d00fbe84d53872c01535d65c5f8
ACR-0d031b9695614e22835b471e60948983
ACR-9ffbf2f1a813468ea90397a8a1d9b512
ACR-6d0e6dc10ce6416da9ffe1728e74826b
ACR-4e75228f57a84ccfa9967cf3f7f97b3d
ACR-fcbe43b33b574f57a52125530467bcfd
ACR-3cb3829b125045b397a2d4c6c1b1e5af
ACR-0a63dbeb387b48e6acc639a6d8ceb41e
ACR-56d3281c89b54ef1b7b3375b3d05de04
ACR-792700a5266a460d807eed3f7f2ea5c8
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.sensor;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.scanner.sensor.ProjectSensor;
import org.sonarsource.sonarlint.core.analysis.sonarapi.DefaultSensorContext;
import org.sonarsource.sonarlint.core.commons.log.LogOutput;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.ProgressIndicator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SensorsExecutorTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();
  public static final DefaultSensorContext DEFAULT_SENSOR_CONTEXT = new DefaultSensorContext(null, null, null, null, null, null, null, new ProgressIndicator() {
    @Override
    public void notifyProgress(@Nullable String message, @Nullable Integer percentage) {
      //ACR-cf69cbb13c324da5b501364c355c88dc
    }

    @Override
    public boolean isCanceled() {
      return false;
    }
  });

  private static class MyClass {
    @Override
    public String toString() {
      return null;
    }
  }

  @Test
  void testDescribe() {
    Object withToString = new Object() {
      @Override
      public String toString() {
        return "desc";
      }
    };

    var withoutToString = new Object();

    assertThat(SensorsExecutor.describe(withToString)).isEqualTo(("desc"));
    assertThat(SensorsExecutor.describe(withoutToString)).isEqualTo("java.lang.Object");
    assertThat(SensorsExecutor.describe(new MyClass())).endsWith("MyClass");
  }

  @Test
  void testThrowingSensorShouldBeLogged() {
    var sensorOptimizer = mock(SensorOptimizer.class);
    when(sensorOptimizer.shouldExecute(any())).thenReturn(true);
    var executor = new SensorsExecutor(DEFAULT_SENSOR_CONTEXT, sensorOptimizer, Optional.empty(), Optional.of(List.of(new ThrowingSensor())));

    executor.execute();

    assertThat(logTester.logs(LogOutput.Level.ERROR)).contains("Error executing sensor: 'Throwing sensor'");
  }

  @Test
  void shouldRunGlobalSensorLast() {
    var sensorOptimizer = mock(SensorOptimizer.class);
    when(sensorOptimizer.shouldExecute(any())).thenReturn(true);

    var regularSensor = new RegularSensor();
    var globalSensor = new GlobalSensor();
    var oldGlobalSensor = new OldGlobalSensor();

    var executor = new SensorsExecutor(DEFAULT_SENSOR_CONTEXT, sensorOptimizer, Optional.empty(), Optional.of(List.of(globalSensor, regularSensor, oldGlobalSensor)));

    executor.execute();

    assertThat(logTester.logs(LogOutput.Level.INFO)).containsExactly("Executing 'Regular sensor'", "Executing 'Global sensor'", "Executing 'Old Global sensor'");
  }

  private static class ThrowingSensor implements Sensor {
    @Override
    public void describe(SensorDescriptor descriptor) {
      descriptor.name("Throwing sensor");
    }

    @Override
    public void execute(SensorContext context) {
      throw new Error();
    }
  }

  private static class RegularSensor implements Sensor {
    @Override
    public void describe(SensorDescriptor descriptor) {
      descriptor.name("Regular sensor");
    }

    @Override
    public void execute(SensorContext context) {
      SonarLintLogger.get().info("Executing 'Regular sensor'");
    }
  }

  private static class GlobalSensor implements ProjectSensor {

    @Override
    public void describe(SensorDescriptor descriptor) {
      descriptor.name("Global sensor");
    }

    @Override
    public void execute(SensorContext context) {
      SonarLintLogger.get().info("Executing 'Global sensor'");
    }
  }

  private static class OldGlobalSensor implements Sensor {
    @Override
    public void describe(SensorDescriptor descriptor) {
      descriptor.name("Old Global sensor").global();
    }

    @Override
    public void execute(SensorContext context) {
      SonarLintLogger.get().info("Executing 'Old Global sensor'");
    }
  }


}
