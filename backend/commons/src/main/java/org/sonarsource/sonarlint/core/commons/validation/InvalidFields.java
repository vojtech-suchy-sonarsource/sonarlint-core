/*
ACR-1fdb0b4b1d614dc98e2efbedab721790
ACR-6155185cb502469abe3ab5586114b75a
ACR-aa9f3200cc0d4eb293754361cd3965e2
ACR-6834262a7af24b5996c97569beebabf9
ACR-7ae49481d30d4771bfd3188a879b234c
ACR-1ea531cc6f7149c9ac35bb9f2c662578
ACR-18cd41f8d3964c8b853cf554fad9b4d1
ACR-57db0dd89a314c0a8e8c92050f43e9b1
ACR-7ae0310f3bf34c56a2489b7421f7e1b9
ACR-e2de7a4043db479fb4b31fe4ba3c0c75
ACR-409400181007487a8e615dd5cb6da9bb
ACR-07309320a25d41c1aaddc2e9468475b1
ACR-42acdf3658d5486295a856c44fec4ab9
ACR-c550491d9ba1476b9ca0ca57f6d01d2d
ACR-266094d27e4c4772a493bbebb9426c19
ACR-543c560b62dc4179a50250dd1420c2f8
ACR-90976011751144d4957bd521210ab8be
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
