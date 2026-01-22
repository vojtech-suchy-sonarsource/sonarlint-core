/*
ACR-ec3ff50f75d24cffb32c67b8e83172e8
ACR-5497b4d507914e7393085e04ad6ae447
ACR-a973bc7cbdbe4abcb8b859a2258f4a4a
ACR-d532adae2bd7471b853f995bd55868bc
ACR-15861a181e8e4fb1aa3291422e5e0cf8
ACR-15b802dd9b824297ac9b84849e3cd6fd
ACR-39a63e974cc842e999faab93ec0c9730
ACR-496c5097a16446e7a7e412818f383930
ACR-7b5f543564a246a7be580f02a24e25ce
ACR-b10c91352da7488693bcea37d5bb8b93
ACR-890a4beef58c4791b5f6bdd8f937b962
ACR-3e76ae718be44439873593f318c1472b
ACR-a8fd0ad948db4ca4a6db7eb3e9ff147c
ACR-ea90d1dce29d4fe1bcf3afe262897aae
ACR-0d10f66e089c49fa907e17d2116395f4
ACR-eb9dcafdf46e4e8eacc5d49a003a3ecc
ACR-ac6f4eea0b204091a0fdcb5e5e1370ef
 */
package org.sonarsource.sonarlint.core.rules;

import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.commons.CleanCodeAttribute;
import org.sonarsource.sonarlint.core.commons.CleanCodeAttributeCategory;
import org.sonarsource.sonarlint.core.commons.ImpactSeverity;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.SoftwareQuality;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonarsource.sonarlint.core.rules.RuleDetailsAdapter.adapt;

class RuleDetailsAdapterTests {

  @Test
  void it_should_adapt_all_cca_enum_values() {
    for (var cca : CleanCodeAttribute.values()) {
      var adapted = adapt(cca);
      assertThat(adapted.name()).isEqualTo(cca.name());
    }
  }

  @Test
  void it_should_adapt_all_ccac_enum_values() {
    for (var ccac : CleanCodeAttributeCategory.values()) {
      var adapted = adapt(ccac);
      assertThat(adapted.name()).isEqualTo(ccac.name());
    }
  }

  @Test
  void it_should_adapt_all_severity_enum_values() {
    for (var s : IssueSeverity.values()) {
      var adapted = adapt(s);
      assertThat(adapted.name()).isEqualTo(s.name());
    }
  }

  @Test
  void it_should_adapt_all_ruletype_enum_values() {
    for (var t : RuleType.values()) {
      var adapted = adapt(t);
      assertThat(adapted.name()).isEqualTo(t.name());
    }
  }

  @Test
  void it_should_adapt_all_language_enum_values() {
    for (var l : SonarLanguage.values()) {
      var adapted = adapt(l);
      assertThat(adapted.name()).isEqualTo(l.name());
    }
  }

  @Test
  void it_should_adapt_all_impact_severity_enum_values() {
    for (var is : ImpactSeverity.values()) {
      var adapted = adapt(is);
      assertThat(adapted.name()).isEqualTo(is.name());
    }
  }

  @Test
  void it_should_adapt_all_software_quality_enum_values() {
    for (var sq : SoftwareQuality.values()) {
      var adapted = adapt(sq);
      assertThat(adapted.name()).isEqualTo(sq.name());
    }
  }

}
