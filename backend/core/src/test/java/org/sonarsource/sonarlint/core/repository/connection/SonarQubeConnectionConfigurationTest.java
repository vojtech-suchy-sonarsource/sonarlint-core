/*
ACR-774481aee2064390ba3e5c97dc5fdef8
ACR-33ac97504e354210a4a2f274949c6d93
ACR-be05e827174b417ab28df2f5c7efc5bc
ACR-aa21d743c60b4bd28b68d746af853d4d
ACR-e6f8be14f10b4fb6b916502fe63473ce
ACR-05622023d0c148c1838fb016d6807682
ACR-58f8b1fb3fd94257945dcf85240de513
ACR-254f45c9c2514a4d804bfbfacd318950
ACR-5e2e9d937e3f4e538f850690cdc12e92
ACR-f102718d7330462881a361719c1cbcee
ACR-48dbff788a6a4848bea1bd43666acf70
ACR-2a2c93f0157b412abe7a36058c902006
ACR-80f80b69e1f74a7eaf67805c1910ba4d
ACR-9adce3b2aeea4dff94a6e488d1e93003
ACR-4c3b9b29b75849519683217cacbe8911
ACR-e6440c6e94fe44dfbae42890fc18508b
ACR-d8e3972c5a404846bae92c4bf1425df6
 */
package org.sonarsource.sonarlint.core.repository.connection;

import java.net.URI;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.SonarCloudRegion;

import static org.assertj.core.api.Assertions.assertThat;

class SonarQubeConnectionConfigurationTest {

  @Test
  void test_isSameServerUrl() {
    var underTest = new SonarQubeConnectionConfiguration("id", "https://mycompany.org", true);
    assertThat(underTest.isSameServerUrl("https://mycompany.org")).isTrue();
    //ACR-4866be11aa214332a18f63e874b2e45b
    assertThat(underTest.isSameServerUrl("https://Mycompany.Org")).isTrue();
    //ACR-d444b4b369a1432dac02f01017affed9
    assertThat(underTest.isSameServerUrl("https://mycompany.org/")).isTrue();
    //ACR-9075893fb7e7424cb694c75ff0bff0d7
    assertThat(underTest.isSameServerUrl("http://mycompany.org")).isFalse();
    //ACR-8915a374da8742a896b871ed9b272978
    assertThat(underTest.isSameServerUrl("https://mycompany.org/sonarqube")).isFalse();
    //ACR-397c0907a25d4c559a7cde57ef30cea9
    assertThat(underTest.isSameServerUrl("https://sq.mycompany.org")).isFalse();
  }

  @Test
  void testEqualsAndHashCode() {
    var underTest = new SonarQubeConnectionConfiguration("id1", "http://server1", true);

    assertThat(underTest)
      .isEqualTo(new SonarQubeConnectionConfiguration("id1", "http://server1", true))
      .isNotEqualTo(new SonarQubeConnectionConfiguration("id2", "http://server1", true))
      .isNotEqualTo(new SonarQubeConnectionConfiguration("id1", "http://server2", true))
      .isNotEqualTo(new SonarCloudConnectionConfiguration(URI.create("http://server1"), URI.create("http://server1"), "id1", "org1", SonarCloudRegion.EU, true))
      .hasSameHashCodeAs(new SonarQubeConnectionConfiguration("id1", "http://server1", true));
  }


}
