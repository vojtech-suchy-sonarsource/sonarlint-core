/*
ACR-9d0b254ec3344c6ea714cf9ca479a7a3
ACR-8d81b63f37c64be98856cc85f0c35514
ACR-92f813bbe32947cb8cb9165b736593d8
ACR-6228987bf0a5459789ea9d9aa8b15990
ACR-7d5393b5f26c42e3839d435387467c43
ACR-2c012623b45646d1b99b59494c1b8411
ACR-500436692694411b8204aae5b55eab0f
ACR-006181dd5091417ebdfd46de75baaa5b
ACR-098a1ec1bde0437d81bcaa9fb14b6eeb
ACR-3334d558e70c439ab63fee2295f9b071
ACR-dac40e26c7a74055bde233c33c87e195
ACR-c34ca35991c14161b5d7a376711b0e5a
ACR-f98d4fc6c3b940e8ad4392fec5161f28
ACR-0b8f6cbe365b4b639697e15df33de2df
ACR-f46a05a6491e4d969774f9531a14e36a
ACR-2e2f9bc666bc45b593fc67742cc5b695
ACR-6bd7abbf90c84aab980db30b3543eebe
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
      //ACR-a064528e2b084d5dafc36f9ad06bb2a2
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
