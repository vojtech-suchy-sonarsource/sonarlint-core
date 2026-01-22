/*
ACR-a8064f6d10a34426ac7fd9be97b32c50
ACR-eed5dbe2668241c2b78c4f8edc3e29a7
ACR-f8cd0c0a9bff48d589d31fe0d48be41b
ACR-c5812fa5692f42d88bf89f14098dad76
ACR-abf16c4c58bc4a4c946e622fe8059990
ACR-ab8c41ab88604623aff6edf864f77905
ACR-a333cdecc63845d29a1d2f53f9d51321
ACR-63a09d4c951d408f9ebb1a003ebfb761
ACR-44a90ec07a174caaad30dbec8e70f88d
ACR-ce4ace6c967c4988b267c50c014dfd08
ACR-d3b57f194b8c4aa08a39d7d355434145
ACR-2cdf6d5931a047e29844db2b3fafa6d3
ACR-883d23326eac4a1d85515e8e3b280f64
ACR-763d1b602e7246a8a0822abc220e708f
ACR-047dffb7721c41d6905398d707cf02b4
ACR-65b383e7b801442da651331d13533ac3
ACR-40e3e6a5ac86409281cc9eda9429d15b
 */
package org.sonarsource.sonarlint.core.commons.validation;

import java.util.ArrayList;
import java.util.List;

public class InvalidFields {

  private final List<String> names = new ArrayList<>();

  public void add(String name) {
    names.add(name);
  }

  public String[] getNames() {
    return names.toArray(new String[0]);
  }

  public boolean hasInvalidFields() {
    return !names.isEmpty();
  }
}
