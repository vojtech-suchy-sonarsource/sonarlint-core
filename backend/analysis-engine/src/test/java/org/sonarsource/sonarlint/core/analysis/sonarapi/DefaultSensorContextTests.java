/*
ACR-563525fa61dc40eb941118d7ddbf6309
ACR-4e9a03bcdede49ffb8d323e3be9f661e
ACR-4bb4618f20a442bc8a7e612c9b53bf76
ACR-9d0ca2877b474604920236df06bc3416
ACR-549cb6c5a5f24ffab8db908e3db1ecd7
ACR-f04ae45f485548c4bfaf271e8e8db425
ACR-dfb6b6570b90412aae2ec6a06b55fbad
ACR-9ca56794dc8e4e9f87bd41e2ee79c7b4
ACR-209a64d378224557ac98d0d6a8960fbf
ACR-0d7be9f39aa546c99e8ecb993cc39050
ACR-62e084a4aa4c45b8a5ff05f8ae260353
ACR-71671e6b868049738fa8487e41c993dc
ACR-4137f94189584d74ba033ce9e5e717c8
ACR-bfad8d6dca834c57a15c60012391f950
ACR-3b981558dc7f4bb4ba50c751c5ce0c2f
ACR-f2883ef461db43368e1e55e4167e9bf4
ACR-06171fb9a94b411797873924363f7e6b
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
        //ACR-5d50956c519f48aa81b9cfd97d1ecb3d
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

    //ACR-0d692946bd76414eb6298c48e0e0a670
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
