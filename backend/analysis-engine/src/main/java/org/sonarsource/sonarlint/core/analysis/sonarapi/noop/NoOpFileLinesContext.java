/*
ACR-e32bf9bf6a9f424585942acb976c97ce
ACR-8b890eb8a1f348aabc82e0352c5b541c
ACR-a42e50e5a6304e4cabdac537c83ff427
ACR-6a5cee9cde50477582b5e870a5d19760
ACR-af516a74b01e4076a3acc53b012d8fd6
ACR-7dee67249ded4f5481f44ddd777c69b8
ACR-5b4b44348a2c4bcda8f908dfeff7ce5e
ACR-644761d577874199b53fcd99d4a662b3
ACR-fc730ab6113a476e8c4e93cd0b721c3e
ACR-323865c7a0e046f485eda19454663b7f
ACR-2bbfa502b2fd4d6bb48d681e8db9169c
ACR-59a9d33e9cfa476784721f4cbaf0c314
ACR-c93f4c2355414bb187451a71c4ec392c
ACR-b9c2d134de9c4c7aa9a3e98a48a36179
ACR-9c38d5c8eae1418d95b72c853318d2ed
ACR-81eeb71cb7194c65b85ac409ed0bace9
ACR-7c945219ef724b6d87f1c17ab77667bb
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
