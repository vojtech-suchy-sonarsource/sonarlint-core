/*
ACR-8f79b4df21c04b6d99ec97587903caab
ACR-c27cf531848c4a9595183a5368c66410
ACR-3558d2763d13411b9b288e7488c0b7a5
ACR-91ef8169ab314c63ac3712b05437061a
ACR-b7c2793c871842f99aa7c5af6173300a
ACR-fcfd048a7cce4d4ab2f172a85d7f22f5
ACR-b9c9b58d64b345ce824d14150269f8c6
ACR-8252d23b99a94f0bada77d5de72a2a0e
ACR-c4cfbc8598d44f6781961e19aeafd22a
ACR-b654341ab2fc4156b9bbaf7f6853926e
ACR-8846ac2a258540d29bd590cc79bb9206
ACR-6970e5a2ea0a46f7bcb2339e35051b90
ACR-e9b867f8a84646b5a171d7c5c6b34a48
ACR-82d884bcd3c5407789692f7cdaf2f38d
ACR-feecf95d774a41c6a02430587629730d
ACR-57c0069386a44efa90e359d2e284d28b
ACR-38bd872b1e87432098ce6fe74278af52
 */
package org.sonarsource.sonarlint.core.repository.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationRepositoryTest {

  private ConfigurationRepository configurationRepository;

  @BeforeEach
  void prepare() {
    configurationRepository = new ConfigurationRepository();
  }

  @Test
  void it_should_not_find_any_binding_on_an_unknown_scope() {
    var binding = configurationRepository.getEffectiveBinding("id");

    assertThat(binding).isEmpty();
  }

  @Test
  void it_should_not_find_any_binding_on_an_unbound_scope() {
    configurationRepository.addOrReplace(new ConfigurationScope("id", null, true, "name"), BindingConfiguration.noBinding(true));

    var binding = configurationRepository.getEffectiveBinding("id");

    assertThat(binding).isEmpty();
  }

  @Test
  void it_should_consider_the_binding_configured_on_a_scope_as_effective() {
    configurationRepository.addOrReplace(new ConfigurationScope("id", null, true, "name"), new BindingConfiguration("connectionId", "projectKey", true));

    var binding = configurationRepository.getEffectiveBinding("id");

    assertThat(binding)
      .hasValueSatisfying(b -> {
        assertThat(b.connectionId()).isEqualTo("connectionId");
        assertThat(b.sonarProjectKey()).isEqualTo("projectKey");
      });
  }

  @Test
  void it_should_get_the_effective_binding_from_parent_if_child_is_unbound() {
    configurationRepository.addOrReplace(new ConfigurationScope("parentId", null, true, "name"), new BindingConfiguration("connectionId", "projectKey", true));
    configurationRepository.addOrReplace(new ConfigurationScope("id", "parentId", true, "name"), new BindingConfiguration(null, null, true));

    var binding = configurationRepository.getEffectiveBinding("id");

    assertThat(binding)
      .hasValueSatisfying(b -> {
        assertThat(b.connectionId()).isEqualTo("connectionId");
        assertThat(b.sonarProjectKey()).isEqualTo("projectKey");
      });
  }

}
