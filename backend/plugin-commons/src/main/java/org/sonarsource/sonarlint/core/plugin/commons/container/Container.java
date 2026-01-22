/*
ACR-326a1f138eab4cb19afa967178b813fd
ACR-845531684f214021ad8e902d159b96ad
ACR-889197808aa44044bf13d6dfb24bdb1f
ACR-112ebec9a0654479b1aaee5886f7f4a6
ACR-3077082553dc4789b7dccefa2aff5d93
ACR-1a1f2b0d079f4335b79ea567921d4b35
ACR-fb624902f9ee4b71bc0c844e7b3cf64b
ACR-9b09b0050ded47e792a0f059d633d87e
ACR-8c5835681663481383a4aaa78c1b32f1
ACR-2e4592a90edc492fbd33a6a277887b15
ACR-8039ce483df34567b5241af721a45be2
ACR-6936b40dbf954ff2b6d7cf59d6b93c4a
ACR-5366666bb5d244dca0c996cbdc851efb
ACR-a1e893cdf42749cf97597e331ffa4642
ACR-e8525a0537bb4568bfe0f304e60da77d
ACR-1017ac0ee68d42f1ab85772838a471d5
ACR-b941321d49e04e8db79410e46fd1a3c3
 */
package org.sonarsource.sonarlint.core.plugin.commons.container;

import java.util.List;
import java.util.Optional;

public interface Container {
  Container add(Object... objects);

  <T> T getComponentByType(Class<T> type);

  <T> Optional<T> getOptionalComponentByType(Class<T> type);

  <T> List<T> getComponentsByType(Class<T> type);

  Container getParent();
}
