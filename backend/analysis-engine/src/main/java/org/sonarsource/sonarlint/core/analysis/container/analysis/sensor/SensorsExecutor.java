/*
ACR-30e9631d8e5e4e729dc9afb2c22cc3c7
ACR-2c05c64f4a4d483a99581a035772ddd9
ACR-678ad0db52d348cf9ee9c62ad934f93c
ACR-0d7f994dcd9a46db820170d292cfbf51
ACR-f227955f66014a85af44472e14bf8dbe
ACR-fa5f5ead02fa4d9691775ab382a45662
ACR-4a7fd2e4f4d44c33a9c2262dbbd23a04
ACR-5437220d2d44439793adc6a3c2b8b6e1
ACR-5c74f08ba09b4091a202165544e9ba6f
ACR-6e89620cc6d44e90ad081302f3810985
ACR-568b56a9627c475b99025746c10b16bd
ACR-4dec1fbb2f8149f687752a337969ae3e
ACR-6220ab4e286f48c3b0a776c0fb633500
ACR-4ca0bc60efa34bf29cd62ca2a3829134
ACR-55ba25dd390f47b1b49ecb26af5e35c0
ACR-7e773afba06047b6962abd969a50e7c7
ACR-81201090cae44d8a8d39640884fe4cac
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.sensor;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.batch.Phase;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.scanner.sensor.ProjectSensor;
import org.sonar.api.utils.AnnotationUtils;
import org.sonar.api.utils.dag.DirectAcyclicGraph;
import org.sonarsource.sonarlint.core.analysis.sonarapi.DefaultSensorContext;
import org.sonarsource.sonarlint.core.analysis.sonarapi.DefaultSensorDescriptor;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.tracing.Trace;

import static org.sonarsource.sonarlint.core.commons.tracing.Trace.startChild;

/*ACR-bd6c31ca11d64e888515f1d147681872
ACR-4b2bef31b3b14439ad4d6d9eb106f2dc
 */
public class SensorsExecutor {

  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final SensorOptimizer sensorOptimizer;
  private final List<ProjectSensor> sensors;
  private final DefaultSensorContext context;
  @Nullable
  private final Trace trace;

  public SensorsExecutor(DefaultSensorContext context, SensorOptimizer sensorOptimizer, Optional<Trace> trace, Optional<List<ProjectSensor>> sensors) {
    this.context = context;
    this.sensors = sensors.orElse(List.of());
    this.sensorOptimizer = sensorOptimizer;
    this.trace = trace.orElse(null);
  }

  public void execute() {
    var sensorGroups = sensors.stream().collect(Collectors.partitioningBy(s -> {
      var isModernGlobalSensor = !(s instanceof Sensor);
      if (isModernGlobalSensor) {
        return true;
      } else {
        var descriptor = new DefaultSensorDescriptor();
        s.describe(descriptor);
        return descriptor.isGlobal();
      }
    }));

    var moduleSensors = sensorGroups.get(false);
    var globalSensors = sensorGroups.get(true);

    executeSensors(moduleSensors);
    executeSensors(globalSensors);
  }

  private void executeSensors(List<ProjectSensor> sensors) {
    for (var sensor : sort(sensors)) {
      if (context.isCancelled()) {
        LOG.debug("Analysis is canceled");
        return;
      }
      var descriptor = new DefaultSensorDescriptor();
      sensor.describe(descriptor);
      if (sensorOptimizer.shouldExecute(descriptor)) {
        executeSensor(context, sensor, descriptor, trace);
      }
    }
  }

  private static void executeSensor(SensorContext context, ProjectSensor sensor, DefaultSensorDescriptor descriptor, @Nullable Trace trace) {
    var sensorName = descriptor.name() != null ? descriptor.name() : describe(sensor);
    LOG.debug("Execute Sensor: {}", sensorName);
    try {
      startChild(trace, "executeSensor", sensorName, () -> sensor.execute(context));
    } catch (Throwable t) {
      LOG.error("Error executing sensor: '{}'", sensorName, t);
    }
  }

  static String describe(Object o) {
    try {
      if (o.getClass().getMethod("toString").getDeclaringClass() != Object.class) {
        var str = o.toString();
        if (str != null) {
          return str;
        }
      }
    } catch (Exception e) {
      //ACR-a97b2a1480174ae79edfc7f8a4d3f67c
    }

    return o.getClass().getName();
  }

  private static <T> Collection<T> sort(Collection<T> extensions) {
    var dag = new DirectAcyclicGraph();

    for (T extension : extensions) {
      dag.add(extension);
      for (Object dependency : getDependencies(extension)) {
        dag.add(extension, dependency);
      }
      for (Object generates : getDependents(extension)) {
        dag.add(generates, extension);
      }
      completePhaseDependencies(dag, extension);
    }
    List<?> sortedList = dag.sort();

    return (Collection<T>) sortedList.stream()
      .filter(extensions::contains)
      .toList();
  }

  /*ACR-5accee5ef9c3457fbf522b20d004973d
ACR-891491ddafb74b94a1f361fb58c30546
   */
  private static <T> List<Object> getDependencies(T extension) {
    return new ArrayList<>(evaluateAnnotatedClasses(extension, DependsUpon.class));
  }

  /*ACR-86fbabcabc054cafb3543afcb6acc9fc
ACR-eb3bb5465ab84c35a66ab8917e6f1060
   */
  private static <T> List<Object> getDependents(T extension) {
    return new ArrayList<>(evaluateAnnotatedClasses(extension, DependedUpon.class));
  }

  private static void completePhaseDependencies(DirectAcyclicGraph dag, Object extension) {
    var phase = evaluatePhase(extension);
    dag.add(extension, phase);
    for (Phase.Name name : Phase.Name.values()) {
      if (phase.compareTo(name) < 0) {
        dag.add(name, extension);
      } else if (phase.compareTo(name) > 0) {
        dag.add(extension, name);
      }
    }
  }

  private static Phase.Name evaluatePhase(Object extension) {
    var phaseAnnotation = AnnotationUtils.getAnnotation(extension, Phase.class);
    if (phaseAnnotation != null) {
      return phaseAnnotation.name();
    }
    return Phase.Name.DEFAULT;
  }

  static List<Object> evaluateAnnotatedClasses(Object extension, Class<? extends Annotation> annotation) {
    List<Object> results = new ArrayList<>();
    Class<?> aClass = extension.getClass();
    while (aClass != null) {
      evaluateClass(aClass, annotation, results);
      aClass = aClass.getSuperclass();
    }

    return results;
  }

  private static void evaluateClass(Class<?> extensionClass, Class<? extends Annotation> annotationClass, List<Object> results) {
    Annotation annotation = extensionClass.getAnnotation(annotationClass);
    if (annotation != null) {
      if (annotation.annotationType().isAssignableFrom(DependsUpon.class)) {
        results.addAll(Arrays.asList(((DependsUpon) annotation).value()));

      } else if (annotation.annotationType().isAssignableFrom(DependedUpon.class)) {
        results.addAll(Arrays.asList(((DependedUpon) annotation).value()));
      }
    }

    var interfaces = extensionClass.getInterfaces();
    for (Class<?> anInterface : interfaces) {
      evaluateClass(anInterface, annotationClass, results);
    }
  }
}
