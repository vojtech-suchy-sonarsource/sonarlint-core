/*
ACR-3b6459e703af4cc0bd5394fd37a479cf
ACR-df87d1ae43f94d608792c085038d11b4
ACR-1ea93179f04d4e9ca177407e893537f8
ACR-018b20b839874f8eb58a1dce59784901
ACR-83fd92c06e4e4730ad26bfb0637a67a6
ACR-470f58959c5a48a4b7ffcbd840191bf2
ACR-749e176c3739491fb5a85c8f182d513f
ACR-ecdcfa28239041b7b0aa9fea56a04bf4
ACR-0d695fe58b63453fa587fdb39bb24452
ACR-1bdc286015ff4637b469b957b77f695a
ACR-d298ec186def4024b19e23012b168d34
ACR-e238df37d14840009451602167865998
ACR-0c5885a75476451f934823939f15516f
ACR-6886691698a7412b9037fa87f1b39802
ACR-78387ab2f981441f955ee6ec38a1f74d
ACR-bb9f5842509d4d3486fc94c250202716
ACR-859be35b61d7457da317d58fd6a99025
 */
package org.sonarsource.plugins.example;

import org.slf4j.LoggerFactory;
import org.sonar.api.Startable;
import org.sonar.api.config.Configuration;
import org.sonarsource.api.sonarlint.SonarLintSide;

import static org.sonarsource.api.sonarlint.SonarLintSide.MULTIPLE_ANALYSES;

@SonarLintSide(lifespan = MULTIPLE_ANALYSES)
public class GlobalExtension implements Startable {

  public static GlobalExtension getInstance() {
    return instance;
  }

  private static final org.sonar.api.utils.log.Logger SONAR_API_LOG = org.sonar.api.utils.log.Loggers.get(GlobalExtension.class);
  private static final org.slf4j.Logger SLF4J_LOG = LoggerFactory.getLogger(GlobalExtension.class);
  private static GlobalExtension instance;

  private int counter;

  private final Configuration config;

  public GlobalExtension(Configuration config) {
    instance = this;
    this.config = config;
  }

  @Override
  public void start() {
    SONAR_API_LOG.info("Start Global Extension " + config.get("sonar.global.label").orElse("MISSING"));
  }

  @Override
  public void stop() {
    SLF4J_LOG.info("Stop Global Extension");
  }

  public int getAndInc() {
    return counter++;
  }

}
