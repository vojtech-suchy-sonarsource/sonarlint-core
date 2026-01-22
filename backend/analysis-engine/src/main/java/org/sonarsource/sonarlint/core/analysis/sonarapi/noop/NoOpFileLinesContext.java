/*
ACR-aacfbb4f8ac84a19809db0d957293a5b
ACR-f6d50e79d765461293d00fb8f3339fcd
ACR-b47fc2b0f5d942ddb48fc992bcd45b79
ACR-3d5a6470efa5419eb5b409cc44be3df0
ACR-2db997b5761047728ab78550aafe13ac
ACR-878b1bbc044640178152b2f8fcf25cd0
ACR-860a24fbc1e5470b83ce89f4e4451274
ACR-906a6f32945a446f81876c1e7b57b852
ACR-20b903894a584788be9b46791867cb85
ACR-d28210ddd31446dca945001acfdbf3f3
ACR-43fdfc99adee4b61a1b7642211b0699d
ACR-516bb9958e2447a6957861e2bb07a479
ACR-0dc056db93bc4a81a5897403cd413bc9
ACR-5f95ff15561543198a43eb74d480ff30
ACR-9182936fda984808bc45501c66e09487
ACR-8fae007af4164b6b9f885a39314b0d69
ACR-79a9a82fff334edcab451c472fa5e4ca
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi.noop;

import org.sonar.api.measures.FileLinesContext;

public class NoOpFileLinesContext implements FileLinesContext {

  @Override
  public void setIntValue(String metricKey, int line, int value) {
  }

  @Override
  public void setStringValue(String metricKey, int line, String value) {
  }

  @Override
  public void save() {
  }

}
