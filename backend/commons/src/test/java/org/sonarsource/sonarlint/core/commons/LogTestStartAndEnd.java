/*
ACR-d1cec1446d3a44578f23f6537d903246
ACR-3b608319501a49d4a939bf7191cced51
ACR-000beab213e0457cb4f8a5697d69ba3d
ACR-f4e79a42aaa047e4810c2bed4017793d
ACR-cfd3f3b8cf9f4ca3822192f24cd585ae
ACR-b6c71309d0904454a997747311219866
ACR-dc060ba83e2548eb99c57dc88edb06b4
ACR-12cd807381134cdcb8baea49b3b7eecd
ACR-82891426f57a49eaa43d0b23e34c1e2e
ACR-281fee1f8c9649afa78f5fb9512ef85f
ACR-534366a29a5e406cbbf4b27e1f7df49b
ACR-7cda7ecaea3547a3b98b30780ebed53a
ACR-8c8d6c06fffd48b386df0d3fbde19270
ACR-10e96ece1b4a4505822afe4daa4c5207
ACR-2c1994f2041648bd9f72b392b601369a
ACR-a55c662e7ea547b4a2335824a4fea0b7
ACR-eddca5cc18da42d7b100fb21cf673568
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
