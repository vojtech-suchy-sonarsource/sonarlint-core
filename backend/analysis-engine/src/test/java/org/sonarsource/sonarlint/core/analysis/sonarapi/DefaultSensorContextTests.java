/*
ACR-888758aa90db470aade36c4a04c48eaa
ACR-b5328e9b923340dc9b8eed8dada0c47b
ACR-929fa8b6962a4073a4854bcb9fcb8b68
ACR-c0324a7f22ac4a8a9fd1ca4e45dd3c3b
ACR-f0ecfe55c92141838a4fb7adec952b66
ACR-def1adcc7db1414982c878642f770d4a
ACR-9c2f9d893ba545efb870dfc08f5cad69
ACR-93543cb0960c4c8b97a54b52b1462b9e
ACR-704756de3ea44decb31a81590abb2568
ACR-b23bda5e15274e06a005ffd2894fdcc5
ACR-7e0ba34b66dd4047886f8e56abeb0198
ACR-a608b7405b3240f4ba545db5498bbcdd
ACR-494ef7160ac84cc39aab1215b9357a85
ACR-7034aa71de3b45b698f5d57c29577ee3
ACR-4b483fc8182b419d8ece737958d418ab
ACR-b2ad957552b9407fa61b8bdacc45339b
ACR-9b51a12e814e49898e743c06b419972e
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi;

import javax.annotation.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sonar.api.SonarRuntime;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.sensor.internal.SensorStorage;
import org.sonar.api.config.Configuration;
import org.sonar.api.config.Settings;
import org.sonar.api.utils.Version;
import org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem.SonarLintInputProject;
import org.sonarsource.sonarlint.core.analysis.sonarapi.noop.NoOpNewCoverage;
import org.sonarsource.sonarlint.core.analysis.sonarapi.noop.NoOpNewCpdTokens;
import org.sonarsource.sonarlint.core.analysis.sonarapi.noop.NoOpNewHighlighting;
import org.sonarsource.sonarlint.core.analysis.sonarapi.noop.NoOpNewMeasure;
import org.sonarsource.sonarlint.core.analysis.sonarapi.noop.NoOpNewSignificantCode;
import org.sonarsource.sonarlint.core.analysis.sonarapi.noop.NoOpNewSymbolTable;
import org.sonarsource.sonarlint.core.commons.progress.ProgressIndicator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultSensorContextTests {
  @Mock
  private SonarLintInputProject module;
  @Mock
  private Settings settings;
  @Mock
  private Configuration config;
  @Mock
  private FileSystem fs;
  @Mock
  private ActiveRules activeRules;
  @Mock
  private SensorStorage sensorStorage;
  @Mock
  private SonarRuntime sqRuntime;

  private DefaultSensorContext ctx;
  private ProgressIndicator progressIndicator;
  private boolean canceled;

  @BeforeEach
  void setUp() {
    canceled = false;
    progressIndicator = new ProgressIndicator() {
      @Override
      public void notifyProgress(@Nullable String message, @Nullable Integer percentage) {
        //ACR-8797f2b35ec24a7c89a975d5d8292de6
      }

      @Override
      public boolean isCanceled() {
        return canceled;
      }
    };
    ctx = new DefaultSensorContext(module, settings, config, fs, activeRules, sensorStorage, sqRuntime, progressIndicator);
  }

  @Test
  void testGetters() {
    when(sqRuntime.getApiVersion()).thenReturn(Version.create(6, 1));

    assertThat(ctx.activeRules()).isEqualTo(activeRules);
    assertThat(ctx.settings()).isEqualTo(settings);
    assertThat(ctx.config()).isEqualTo(config);
    assertThat(ctx.fileSystem()).isEqualTo(fs);
    assertThat(ctx.module()).isEqualTo(module);
    assertThat(ctx.runtime()).isEqualTo(sqRuntime);

    assertThat(ctx.getSonarQubeVersion()).isEqualTo(Version.create(6, 1));
    assertThat(ctx.isCancelled()).isFalse();

    //ACR-2c938318f858474c96e3935eae230a01
    assertThat(ctx.newCpdTokens()).isInstanceOf(NoOpNewCpdTokens.class);
    assertThat(ctx.newSymbolTable()).isInstanceOf(NoOpNewSymbolTable.class);
    assertThat(ctx.newHighlighting()).isInstanceOf(NoOpNewHighlighting.class);
    assertThat(ctx.newMeasure()).isInstanceOf(NoOpNewMeasure.class);
    assertThat(ctx.newCoverage()).isInstanceOf(NoOpNewCoverage.class);
    assertThat(ctx.newSignificantCode()).isInstanceOf(NoOpNewSignificantCode.class);
    ctx.addContextProperty(null, null);
    ctx.markForPublishing(null);
    assertThat(ctx.canSkipUnchangedFiles()).isFalse();
    assertThat(ctx.isCacheEnabled()).isFalse();
    assertThat(ctx.isFeatureAvailable("any")).isFalse();
    assertThrows(UnsupportedOperationException.class, () -> ctx.newExternalIssue());
    assertThrows(UnsupportedOperationException.class, () -> ctx.previousCache());
    assertThrows(UnsupportedOperationException.class, () -> ctx.nextCache());
    ctx.addTelemetryProperty("not", "applicable");

    verify(sqRuntime).getApiVersion();

    verifyNoMoreInteractions(sqRuntime);
    verifyNoInteractions(module);
    verifyNoInteractions(settings);
    verifyNoInteractions(fs);
    verifyNoInteractions(activeRules);
    verifyNoInteractions(sensorStorage);
  }

  @Test
  void testCancellation() {
    assertThat(ctx.isCancelled()).isFalse();

    canceled = true;

    assertThat(ctx.isCancelled()).isTrue();
  }
}
