/*
ACR-f8be6a6d3a944bb4ac06c0e78aea8815
ACR-b64f19d39063461cbf168f234d63d8fc
ACR-b0f2efd8bcd94f58b676d179b62326ad
ACR-0b4c606e87e0455f89124f324c34c4b7
ACR-ebae784d61ba4d9fb1e8e5eb20ea6458
ACR-ecb63326b62d4d8f92306abb23b72a4a
ACR-3cc39acb0eda475aaf94440f2a18cbba
ACR-e12128dcc08a410ab84d5c2bc2fab66a
ACR-76cd200f672646f3b1f7a90efab7c3bd
ACR-a44a3a6c1a664919bb84c48d6bb3b923
ACR-debd5bedd1dc46cb9fd74df672d6a9dc
ACR-a27c12427ed54e7790b5d64993747491
ACR-b9b9408c5e1447baa6a11d23a0d2974d
ACR-04908aba427d4b9caa1f1f4e476f0b07
ACR-9a3b35e6528b4f6cb183ff45aaf5c723
ACR-11b516d4634c434f94d720bb46bece88
ACR-45aa8f3530ba48d5b3a1c151fb8d2afa
 */
package org.sonarsource.sonarlint.core.tracking.matching;

import java.util.Optional;

public interface MatchingAttributesMapper<G> {


  String getRuleKey(G issue);

  Optional<Integer> getLine(G issue);

  Optional<String> getTextRangeHash(G issue);

  Optional<String> getLineHash(G issue);

  String getMessage(G issue);

  Optional<String> getServerIssueKey(G issue);
}
