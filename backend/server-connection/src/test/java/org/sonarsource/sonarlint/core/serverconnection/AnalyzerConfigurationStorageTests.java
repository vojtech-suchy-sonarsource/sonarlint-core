/*
ACR-591b37cc4449448aa829de9d3251706b
ACR-480bd15ca19a4333b5d1acde3bf9e184
ACR-d64808951358475cbd0658670ff1575c
ACR-ff03369d02d745fe8c3baec0d96452d2
ACR-6a4cbce574b1451384ffce9fb37debbb
ACR-fde8b67deb8a4207940f5e2df8dcbe9c
ACR-0543cf5555d644d694491eeb31f9d637
ACR-605a0ec6bb3c4512aa0bd9a0938f6d29
ACR-eed52ebc59d44fde96dd1da72c965e27
ACR-ce9cf2ecd8e94f14baa1bfa3d53c453e
ACR-ac89e661412a4032bc29998f807dff2a
ACR-c0a072c72aaa450a8d721625c3eedf41
ACR-c6947cb794a84bfebeabbf97f82cbfad
ACR-f5ded6be10854e5fb3d9d23cdea1206f
ACR-50ed67b3366146fa8b0ca2603f0a9ea0
ACR-0d1341963bea422f8985696f0ea0a945
ACR-9871384823a74997ae4f8f0b24605e65
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
