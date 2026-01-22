/*
ACR-2bf27bc8cd354ab08867520b374d18e0
ACR-5b6d5056fb874a03acb8de3390e1e471
ACR-649d9db664e7401d8aa6b0f66b88f561
ACR-edc0685ee3d646699cf84c0749631490
ACR-82e5f54ee91c4e89aec9f8366ce2b7b5
ACR-5f39bf0734eb4ba792da2b82707d3ea8
ACR-479e0dea62124719a010d2e386deba7c
ACR-0fd19b9c44f74b40888d082fcf8b09b9
ACR-8d5e0dd7da264f3a9799016e6cad8300
ACR-b2e2f22248e6468bbbae0b9c979c40ad
ACR-39ac17a113ed4461b99225c72f1ccf1f
ACR-7b1785c913b0462a911bc9c6ea32d854
ACR-7bcabf09ed3344f3978fe6187d7f94ef
ACR-bccdc6ff66ec4638a42b6d5ba7a2ee82
ACR-a5ac54d9812447de9f83b27c01933c76
ACR-691307144d024698b767f5d11a393b70
ACR-12479e8df9f24cb29be4793a298b4fbf
 */
package org.sonarsource.sonarlint.core.rule.extractor;

import org.junit.jupiter.api.Test;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.sonarsource.sonarlint.core.rule.extractor.SecurityStandards.CWES_BY_SL_CATEGORY;
import static org.sonarsource.sonarlint.core.rule.extractor.SecurityStandards.fromSecurityStandards;

class SecurityStandardsTest {
  @Test
  void fromSecurityStandards_from_empty_set_has_SLCategory_OTHERS() {
    SecurityStandards securityStandards = fromSecurityStandards(emptySet());

    assertThat(securityStandards.getStandards()).isEmpty();
    assertThat(securityStandards.getSlCategory()).isEqualTo(SecurityStandards.SLCategory.OTHERS);
    assertThat(securityStandards.getIgnoredSLCategories()).isEmpty();
  }

  @Test
  void fromSecurityStandards_from_empty_set_has_unknown_cwe_standard() {
    SecurityStandards securityStandards = fromSecurityStandards(emptySet());

    assertThat(securityStandards.getStandards()).isEmpty();
    assertThat(securityStandards.getCwe()).containsOnly("unknown");
  }

  @Test
  void fromSecurityStandards_finds_SLCategory_from_any_if_the_mapped_CWE_standard() {
    CWES_BY_SL_CATEGORY.forEach((slCategory, cwes) -> {
      cwes.forEach(cwe -> {
        SecurityStandards securityStandards = fromSecurityStandards(singleton("cwe:" + cwe));

        assertThat(securityStandards.getSlCategory()).isEqualTo(slCategory);
      });
    });
  }

  @Test
  void fromSecurityStandards_finds_SLCategory_from_multiple_of_the_mapped_CWE_standard() {
    CWES_BY_SL_CATEGORY.forEach((slCategory, cwes) -> {
      SecurityStandards securityStandards = fromSecurityStandards(cwes.stream().map(t -> "cwe:" + t).collect(toSet()));

      assertThat(securityStandards.getSlCategory()).isEqualTo(slCategory);
    });
  }
}
