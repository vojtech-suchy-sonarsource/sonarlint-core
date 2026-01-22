/*
ACR-2bfd31c54d25442bae20f03f4c38d6b1
ACR-e510478cfe8646e5ab43353695ea8596
ACR-29715641db1c42bfa53d8ea2f7ddf27b
ACR-a8d137bc188d4d5484de2fd24cf5870d
ACR-63d93ad9607a443faf7bcb5eb313c946
ACR-8d291b90c1574331acd69d2276b8ee18
ACR-c0f1549633d94bb19291efa273ba208a
ACR-c9258b035d3a45b699b91a9d271ffc7d
ACR-1c883226ef054875807e8b804a365c0e
ACR-a5ab9c7f324c479cbce45d8142c86780
ACR-6d06d02e802e4349a5f11a3693450c91
ACR-8d0d12790c2a4efd9761f14c0d8570aa
ACR-991eac7e2b614d15b7a97cee45cbb80b
ACR-a0a5deab9e1349089e9fea72e59f0600
ACR-f574a4f0cfb14aea819a41eb7b334185
ACR-07b4cd5b784d4b36916af15862112df1
ACR-365624b70d324a95a69eaa05492b6ce9
 */
package org.sonarsource.sonarlint.core.analysis;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;

import static org.assertj.core.api.Assertions.assertThat;

class ClientAnalysisPropertiesServiceTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();
  private static final String CONFIG_SCOPE_ID = "scope-id";
  private static final String ANOTHER_CONFIG_SCOPE_ID = "another-scope-id";
  UserAnalysisPropertiesRepository underTest;

  @BeforeEach
  public void setup() {
    underTest = new UserAnalysisPropertiesRepository();
  }

  @Test
  void it_should_remove_previous_config_and_set_provided_user_properties() {
    var properties = underTest.getUserProperties(CONFIG_SCOPE_ID);
    assertThat(properties).isEmpty();

    underTest.setUserProperties(CONFIG_SCOPE_ID, Map.of("key1", "value1", "key2", "value2"));
    properties = underTest.getUserProperties(CONFIG_SCOPE_ID);
    assertThat(properties).hasSize(2).containsEntry("key1", "value1").containsEntry("key2", "value2");

    underTest.setUserProperties(CONFIG_SCOPE_ID, Map.of("key2", "new-value2", "key3", "new-value3"));

    properties = underTest.getUserProperties(CONFIG_SCOPE_ID);
    assertThat(properties).hasSize(2).containsEntry("key2", "new-value2").containsEntry("key3", "new-value3");
  }

  @Test
  void it_should_not_modify_other_config_scope_properties() {
    var properties = underTest.getUserProperties(CONFIG_SCOPE_ID);
    assertThat(properties).isEmpty();

    underTest.setUserProperties(CONFIG_SCOPE_ID, Map.of("key1", "value1", "key2", "value2"));
    underTest.setUserProperties(ANOTHER_CONFIG_SCOPE_ID, Map.of("key1", "value1"));
    properties = underTest.getUserProperties(CONFIG_SCOPE_ID);
    assertThat(properties).hasSize(2).containsEntry("key1", "value1").containsEntry("key2", "value2");

    underTest.setUserProperties(CONFIG_SCOPE_ID, Map.of("key2", "new-value2", "key3", "new-value3"));

    properties = underTest.getUserProperties(CONFIG_SCOPE_ID);
    assertThat(properties).hasSize(2).containsEntry("key2", "new-value2").containsEntry("key3", "new-value3");
    var anotherProperties = underTest.getUserProperties(ANOTHER_CONFIG_SCOPE_ID);
    assertThat(anotherProperties).hasSize(1).containsEntry("key1", "value1");
  }

}
