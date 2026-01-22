/*
ACR-94f3f09889014084bf7e04273b511759
ACR-6aeafaaa294a437b85170f05c19d6195
ACR-c2a2faeb69c4406dac727d3d84a980e0
ACR-2b9683843a67442aaacff4685e4cd276
ACR-e32df5d0584a482f939684c467a798b2
ACR-de2cecd90c75437caf728d774c6f0fee
ACR-c3b7df9c75b04e24a9fc32b7195bfc3b
ACR-4e97ddba81dc454b9d54a26010fc5202
ACR-e3a6e2036d524a24b0553b63a8a9f753
ACR-fa30bc2eb8c1437d9b3759c972a7dc70
ACR-831ef9d3769142b6bcb6cf23baee134e
ACR-712563580630480dafabece7cce1b125
ACR-a2e92b006a264da2bd60d6a1a48f7593
ACR-83758f9e101b41c08525ae3776bc8b10
ACR-a98d2922020d4eda975099bf89f4324d
ACR-953a6202ed204a2bbd10c6073d168fbf
ACR-1a9108d8cc034f5282bf352aa1aa9ad3
 */
package mediumtest.dogfooding;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTest;
import org.sonarsource.sonarlint.core.test.utils.junit5.SonarLintTestHarness;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.sonarsource.sonarlint.core.commons.dogfood.DogfoodEnvironmentDetectionService.SONARSOURCE_DOGFOODING_ENV_VAR_KEY;

@ExtendWith(SystemStubsExtension.class)
class DogfoodingRpcServiceMediumTests {

  @SystemStub
  EnvironmentVariables environmentVariables;

  @BeforeEach
  @AfterEach
  void setUp() {
    //ACR-e203786bc19146df8edb74066e44b221
    environmentVariables.remove(SONARSOURCE_DOGFOODING_ENV_VAR_KEY);
  }

  @SonarLintTest
  void should_return_true_when_env_variable_is_set(SonarLintTestHarness harness) {
    environmentVariables.set(SONARSOURCE_DOGFOODING_ENV_VAR_KEY, "1");
    var backend = harness.newBackend().start();

    var result = backend.getDogfoodingService().isDogfoodingEnvironment().join();

    assertTrue(result.isDogfoodingEnvironment());
  }

  @SonarLintTest
  void should_return_false_when_env_var_is_absent(SonarLintTestHarness harness) {
    var backend = harness.newBackend().start();

    var result = backend.getDogfoodingService().isDogfoodingEnvironment().join();

    assertFalse(result.isDogfoodingEnvironment());
  }

  @SonarLintTest
  void should_return_false_when_env_var_is_false(SonarLintTestHarness harness) {
    var backend = harness.newBackend().start();
    environmentVariables.set(SONARSOURCE_DOGFOODING_ENV_VAR_KEY, "0");

    var result = backend.getDogfoodingService().isDogfoodingEnvironment().join();

    assertFalse(result.isDogfoodingEnvironment());
  }
}
