/*
ACR-4461aafee19b48a8aeacea1c947f7f8f
ACR-38f36f028078491e962e607a8c1a0f4b
ACR-9725594e4ff44db1847990878cc44664
ACR-5d6aa5aaff7a4534aefda848dd5efb6d
ACR-cc12310fb7c24c8facd266599525cd22
ACR-5919b4afdb2c47309ca348ad01d6e3d2
ACR-40f0c0ca79ff44e3afb1a6795be74d54
ACR-54c22209484643ca8ef8f6eb59d768cb
ACR-4e217e31661147f68fd63aabdbd9d48e
ACR-f943d929932c472da9d324a16845d5b0
ACR-9f4c9a1e14c344649e4223308dbfe6e0
ACR-dcb9d5bf1cc84be492a75e2b8904097b
ACR-c283a1b8c4794a2aa2d180f7e928f3d6
ACR-27ec8329f9784eefab012b0242c8d5f7
ACR-17a09f9fcd2f4577b198464ca17e0104
ACR-c0fd2e86d9f642aea1c96ec1acf842a6
ACR-3334e2432ded45229552858f086938e2
 */
package org.sonarsource.sonarlint.core.spring;

import org.sonarsource.sonarlint.core.labs.IdeLabsSpringConfig;
import org.sonarsource.sonarlint.core.promotion.PromotionSpringConfig;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.telemetry.TelemetrySpringConfig;
import org.sonarsource.sonarlint.core.telemetry.gessie.GessieSpringConfig;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static java.util.Objects.requireNonNull;

public class SpringApplicationContextInitializer implements AutoCloseable {

  private final AnnotationConfigApplicationContext applicationContext;

  public SpringApplicationContextInitializer(SonarLintRpcClient client, InitializeParams params) {
    applicationContext = new AnnotationConfigApplicationContext();
    applicationContext.register(SonarLintSpringAppConfig.class);
    applicationContext.register(TelemetrySpringConfig.class);
    applicationContext.register(GessieSpringConfig.class);
    applicationContext.register(IdeLabsSpringConfig.class);
    applicationContext.register(PromotionSpringConfig.class);
    applicationContext.registerBean("sonarlintClient", SonarLintRpcClient.class, () -> requireNonNull(client));
    applicationContext.registerBean("initializeParams", InitializeParams.class, () -> params);
    applicationContext.refresh();
  }

  public ConfigurableApplicationContext getInitializedApplicationContext() {
    return applicationContext;
  }

  @Override
  public void close() throws Exception {
    applicationContext.close();
  }
}
