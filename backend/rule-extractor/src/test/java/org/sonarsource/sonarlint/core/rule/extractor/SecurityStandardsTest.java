/*
ACR-79c7e64f453a4a0d9fb3ef34ce5b8082
ACR-c3071e53d149415c9824ca08134064d0
ACR-4afc03e046d345838e40a202d6e6b046
ACR-14c516dbed084756aab0bb7e48c3103b
ACR-1c7e645e7e564d3292f71d27ac60d6a0
ACR-c2aa6a6f0ee14a26982a258fd1c0e2ee
ACR-92ec4ced543d4b8fba0555b2284e1a45
ACR-50247d91efe94a9cb0d80c002b85f575
ACR-eaedaa43f07c40fd908b4c3fd75f2677
ACR-9f568e3ddcd444c88b46a29652d84f44
ACR-63304b0d156f45cf8ec941956c3d57db
ACR-e97e72c9c9384756b67866572a83d74f
ACR-405d4f582e0042dea84c8c5898d909ba
ACR-c66924c7e28b43c9983145433559f464
ACR-aa82a2abe176475aadaf4a40b28f7026
ACR-a9862aaa772b4d5e9393d68b3cc9d842
ACR-f34122411b6e4dedbf2c1702e09bfaf4
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
