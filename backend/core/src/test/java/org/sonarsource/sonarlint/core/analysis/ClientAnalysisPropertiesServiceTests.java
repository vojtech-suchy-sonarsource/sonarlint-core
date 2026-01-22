/*
ACR-2d4e627c8c7242669be87485a6b0d675
ACR-d23d4b9c6cde4b9b99492812d9c97064
ACR-0fa74227a6164541aa1cba1072b269a2
ACR-8dd82de628954ec19f8886a9609bd1a9
ACR-01dd451bd7aa44a98b072fcf29ad4fff
ACR-b6039567cc5d4bf79a872b43e206416a
ACR-e151566b5502414e8e5dd8e44fb561ab
ACR-c1fb44696ec34a6b8ac3f00e5d84cf87
ACR-d67a8f18782c4f9e9025b627e481bd97
ACR-46ca5c285f024e508c80493c8dc28c1b
ACR-969bc7fa8b864ce4987e7ce50a9ea9f8
ACR-33711261a572415bb707ed185ad9867f
ACR-2f9f2a21a30e4b169ff395b29b7b2d53
ACR-e9f9853aa86f4b748354e88866032692
ACR-cacb5c146ec547e087d5fbd1621f0b05
ACR-85de0c7080c549fb85fb128fb2b650d3
ACR-f0ee379ce55249b294d7222754ca6b05
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
