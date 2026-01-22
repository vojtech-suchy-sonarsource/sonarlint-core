/*
ACR-acd1a3239d57475b954e8b00d7c2af1c
ACR-51a8edfe19ae4a08afe96e8ccf3668c5
ACR-de87cf5352fb420e88367a8ceda94574
ACR-2e760548c29245e0b3220c1d985f3248
ACR-9271cc5618f54bbebe9dc5f886947405
ACR-c370ac9f75714c99b478f628bf9b9429
ACR-6868735ee8ff4c8ea2077d96551eec4a
ACR-57bb17e347754542859cc14329bfcf1a
ACR-51f5e5ab8591480bb86f7d8b394f8b11
ACR-795b8ff1814241b79080c0f56e62e78e
ACR-792038b3e7a74623b4897deca68c7d3e
ACR-30d87750eea74e0db7b3cfc016e6db3b
ACR-de2f25c26e894f5b84d21fb473c70088
ACR-1c3f2b82e5894c45a01e63663179169f
ACR-f25f6c0f2d49480e911ea54728e2d148
ACR-c5befdac39414048bc5e964f17c9f0b4
ACR-cea350e594f74fe6834aa1acd17b5dfe
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
