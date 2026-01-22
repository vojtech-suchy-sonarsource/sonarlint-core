/*
ACR-06a2aa165fa946a891a5e34c0f924f17
ACR-7f741555bbcc4737a594211ef4869116
ACR-9e64980e7f7b480c9619f3fdd9cf799d
ACR-349eb2b9467849939fdfbe960b414f65
ACR-f1f3bd8b6387416288d60bc1668eddf4
ACR-ec3d3160dc2145cdb277f7461f4a8dad
ACR-0b233df0bfc84a3cae4a42f03e26e5ff
ACR-8ac0086050ba4a278d938fea7487b3af
ACR-8e08c032b98a4a89b7d1df37992a7d5f
ACR-b1d562d66e064b4aad89d98049a530c6
ACR-3e8c76f80bbb4d5fbbe872b5462fc334
ACR-26850294af0e41e6b350222cc45ab41e
ACR-0a1390fee01b4105931ef54f988f25b1
ACR-9b69003cc3ca4aa587d706e177042c2f
ACR-8d261885e5814f9e98a8a83ce2371e84
ACR-d3e7bdbe4165494c890b2be38dc0c321
ACR-630bc644b4b449f8bd09a396b3cafd21
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
