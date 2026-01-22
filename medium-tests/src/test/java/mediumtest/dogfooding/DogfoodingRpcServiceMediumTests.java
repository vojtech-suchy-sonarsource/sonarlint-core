/*
ACR-ec2c756c6b8f4a66aafe6dd4b84cc8e0
ACR-ef6e9499bfa94873b258d20ffc384a79
ACR-de6f5d51f500459b8ecf4e29b3f3e041
ACR-f36894e6eba84d159bc19f05be0bc97d
ACR-c796a937e6bc49609a1ef860dbc4c303
ACR-67643de2e8034078926c816b9821a29a
ACR-9346b4eb92c04c569df921903f27122d
ACR-f7e13644e17e47f0ad511f60b2c5c7ba
ACR-c48a60338cfe41f1a4106034fdd7dd60
ACR-07bbc3057e9341cfb4115da72a981431
ACR-a68c1ff66bc64a6cabc19ead817d897f
ACR-018c902c2a3a4277b6e2f261c5dc303a
ACR-e21896d5ff14457982e5a57e0c43492c
ACR-8d53e1622df94e189297ac000b22d6c0
ACR-f85b9e61d8854a01b1d03d6c440af3d7
ACR-06ab75f6ac224cfc9d98f18c08c7cfe7
ACR-04f56602ce4a4fd2b00e0e9ebd386450
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
    //ACR-c63c54ce764b4312ac6cf61ebdc22713
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
