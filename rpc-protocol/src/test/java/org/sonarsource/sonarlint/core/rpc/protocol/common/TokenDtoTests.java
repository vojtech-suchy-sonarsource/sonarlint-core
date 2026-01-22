/*
ACR-1ac560571895463ca205bff7e1397ab5
ACR-24ec3f9d8a33462abbfe74c934ec4e40
ACR-99ddc1da6d944cf49a0a753f8eeda49b
ACR-1681aa347bd34becab1009076328ae22
ACR-a1d61b81b13942c99fc72680450cf1c2
ACR-4b0a9cb4f53447a69e0eac0f3e867dea
ACR-706c0f9db7e4453ab52ce5a9bf1b6a15
ACR-a03930faafbd448388a44435413fd63a
ACR-b55bfab0f39540d7972c762bff5778ef
ACR-061831d0b6bd4f608d959ef4e1380328
ACR-9fb2dd530e864b53b12a8cc6a8178494
ACR-a9e8d8757df746f49778c0ea79e2f7f5
ACR-86af66375b96470eb3fd38f8dc471078
ACR-c5ff5551325b4dbbb06d0b67f4df25ef
ACR-9314bee59ad949bbaaa5ca35ca119b36
ACR-a1d32bcb38ac43d18bffa1f600a6524a
ACR-eb370439a0344b2893c6ee9faa34c74a
 */
package org.sonarsource.sonarlint.core.rpc.protocol.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenDtoTests {

  @Test
  void testEqualsAndHashCode() {
    TokenDto token = new TokenDto("token1");
    TokenDto sameToken = new TokenDto("token1");
    TokenDto differentToken = new TokenDto("token2");

    //ACR-b9f4d261e4b84db0975730b0e3af2cb1
    assertThat(token)
      .isEqualTo(token)
      .isEqualTo(sameToken)
      .isNotEqualTo(differentToken)
      .isNotEqualTo("token1")
      .hasSameHashCodeAs(sameToken)
      .doesNotHaveSameHashCodeAs(differentToken);
  }
}