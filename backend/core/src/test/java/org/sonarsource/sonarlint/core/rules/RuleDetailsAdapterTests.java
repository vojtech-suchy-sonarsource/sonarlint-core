/*
ACR-0390c4b54c914ed6a291e61b56468765
ACR-4f8ff390f0f745dcb4c9ba34383cd747
ACR-e719643f1f6242d09ae164c83f14f673
ACR-15479919b6004411a791ffe293bf24af
ACR-a70b77cbb60441cfbaacae99a42360e9
ACR-cb8b4ffd0d6b458f9b45755731635689
ACR-46d8c0ff373149dc9054cd5d9f511a10
ACR-0bd48896a69a4b269fe744a45518db18
ACR-5de34d4023174eed9a0d9f2cb820dbeb
ACR-4dfdefe5f4ab49cd9d53f8a918d5956f
ACR-b60b396f8b4e4541810cddc50cd92296
ACR-f10c56163d414cef8cce82e540e4b017
ACR-acd6806fce224f6283af229395d14a0a
ACR-b0101d2d1367440f82b8beb785c01a9c
ACR-a28b1ff733754b06af6d09e462ddb6bc
ACR-f600f5d7b40d4f6599d32fbae55e8ec0
ACR-68ca07256bcb4096be0d52bf071056d6
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
