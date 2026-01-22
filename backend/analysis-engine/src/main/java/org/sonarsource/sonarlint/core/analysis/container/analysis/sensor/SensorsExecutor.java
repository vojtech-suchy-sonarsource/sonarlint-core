/*
ACR-62bad7c5075a4c01903351aadffaa262
ACR-b247c79497ab4e198cd4a851bb9aefbb
ACR-e2884add765d49ff9da68679b01ab885
ACR-e072bc95b9224d2b85ba72838106ca3d
ACR-e2875758d96745e9a8cc2c55fb94db5f
ACR-72c24b1a7ef442719d21627c193e0bc0
ACR-830e22e996cf4ffbb9207708f2ab1fcb
ACR-c2d1bb7b6c0d4646a55540662c486867
ACR-be32d32431344ceeb6214670eee42a96
ACR-f87a9924a5cb4789a86afa2b2b94ebd1
ACR-edf8e19e760a4b7bb05b15e252b58e72
ACR-a9f89c3b7d5f43cca2707b9fec155ab2
ACR-62b22431ac194b6fb6ca02871596b8c2
ACR-6c201478f9034ce88113f4ce03798079
ACR-409be457677b4b4fa21df7fe920d6264
ACR-0af0186e99c141cf98ac0aec15bb6932
ACR-ba482ab2f01d4d0f9ff34a4f088c30c2
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

/*ACR-bc948fecb2334b5dbb4e197740b131e6
ACR-76e457bee0c444faab0ced8b213dd034
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
      //ACR-59b51376a3dc43feac8ab516224b23da
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

  /*ACR-e8877ea18ec9496ab6da79ac2a49dbb4
ACR-6ed9a43519d54d1d829458a5b37c46aa
   */
  private static <T> List<Object> getDependencies(T extension) {
    return new ArrayList<>(evaluateAnnotatedClasses(extension, DependsUpon.class));
  }

  /*ACR-0b037bac39fc4b1d995f775ff7092b0e
ACR-2b0efe03615a443684df71745e1aaf17
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
