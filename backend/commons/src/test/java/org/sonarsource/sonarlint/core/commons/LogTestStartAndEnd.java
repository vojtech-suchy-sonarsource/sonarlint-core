/*
ACR-8208c197984a41a584161d2826097c3c
ACR-5c8683d87a8f47bca4fb740c8f4cdee4
ACR-e24af93a93db4d878d2034ed3288858b
ACR-846e166591ed49f994c9a793a29bd233
ACR-ae9f0f1c6b094a5bbca28b9736ba03b5
ACR-4ecd5c8b3bc04e6e94b40fa6a71cda62
ACR-665f6410e5cc42418393be3d60d3d036
ACR-dd4905063ff645e3b0b25e5bbb6a5ece
ACR-062e15b449de4b1fbbc5a623d1fbe347
ACR-e21b35d9b2a646f6bde755845817d86b
ACR-9352fbe185d64ce0965d8b24d924255c
ACR-7d70d53486e342dbb4905b61556dc742
ACR-f315e6cf1c8d49bea821a506ac2a286e
ACR-0dda1f2aaf614bd18df237c4d60198bb
ACR-025a285372e54eee833075f2d0f3fcfe
ACR-28480a93ff2545a1ac462bb3c7e822e6
ACR-1e6accda87ac4041a1fe7bb2ff3117ef
 */
package org.sonarsource.sonarlint.core.commons;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class LogTestStartAndEnd implements BeforeEachCallback, AfterEachCallback {
  @Override
  public void beforeEach(ExtensionContext extensionContext) {
    extensionContext.getTestMethod().ifPresent(method -> System.out.printf(">>> Before test %s%n", method.getName()));
  }

  @Override
  public void afterEach(ExtensionContext extensionContext) {
    extensionContext.getTestMethod().ifPresent(method -> System.out.printf("<<< After test %s%n", method.getName()));
  }
}
