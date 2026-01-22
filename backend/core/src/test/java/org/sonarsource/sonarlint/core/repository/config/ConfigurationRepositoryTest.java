/*
ACR-d5070cd3b59149f38110f5dee1dff244
ACR-a63f131afc084985bcb8c428f8ecfacd
ACR-312de699961142e8922bcf2b1996a97e
ACR-c137393cfb79451dab01e6755aa92bf7
ACR-681736749b254f7e977eecc803844818
ACR-e492a509dc994fb3955bcc7a2dff2f63
ACR-54dc809aff5144f383e73b31ef2ef97f
ACR-b83e25a5342c417884359935ae5b02d2
ACR-0df20820051a4fe2892af8c30ffc740d
ACR-b035aa7145dd431f9ed8872c869fec5b
ACR-308419828524498c872dcac4fe14b0c8
ACR-66991b9585bb49de9f7587282ece2fb3
ACR-d0412be3898f496a9033fe7f7c2078ac
ACR-8d17072ca97447e5b69b156942b0f444
ACR-ed55438fa0974ca8a31af78d170f4e63
ACR-586b73057c304ef69bcf7ed96f460b63
ACR-60c67a96c89a41748f4bb77482cfe488
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
