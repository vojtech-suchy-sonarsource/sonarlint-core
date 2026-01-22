/*
ACR-867e1a5e704d45a082f8b1391b6b5c31
ACR-a9b631578eab49f4a7c76d5261c9d249
ACR-fd74b0b2971b47cdb803f3d32ec7134e
ACR-94004f5607d54f2497d5b0949b01c0f6
ACR-0a4db9796a04478ca30670871181da90
ACR-dfccf3db70d2431cbfb194c7cff68f5c
ACR-69706f9867814d64b9426d52d9d8104b
ACR-0b24206876954b2ab594767a6869cba1
ACR-f67c00dc127344f287f6bf5245ddabf4
ACR-b78f665b8fcc479995aab1366fb03f94
ACR-33be8cb93f664ed388c6682bd4e56f52
ACR-296c0df3db1f445ab2dd0e0b7c24edfe
ACR-2acb718eab1a4a12b544ace3773c605c
ACR-ee3dfc0155944c8aae84ec91d7348dec
ACR-695b84f7212f4fcb84e8d66dbc63818e
ACR-8dca24f219b94743bba38bd7b3625316
ACR-207c0d1afdcf41098efa3c6a127cbbbd
 */
package org.sonarsource.sonarlint.core.serverconnection;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

class AnalyzerConfigurationStorageTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  @Test
  void should_consider_config_storage_invalid_if_not_readable_and_do_not_log_exception(@TempDir Path tempDir) {
    var analyzerConfigurationStorage = new AnalyzerConfigurationStorage(tempDir);

    var valid = analyzerConfigurationStorage.isValid();

    assertFalse(valid);
    assertThat(logTester.logs()).contains("Analyzer configuration storage doesn't exist: " + tempDir.toAbsolutePath().resolve("analyzer_config.pb"));
  }
}
