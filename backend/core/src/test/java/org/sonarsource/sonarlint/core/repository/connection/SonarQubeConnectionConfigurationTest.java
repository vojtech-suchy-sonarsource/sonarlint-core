/*
ACR-9434c41633ff4f16993369691a01d187
ACR-5437da5e250d4b3c8acc9c7373514b49
ACR-15e1d11e018b4fd7899165fe606f627f
ACR-cb5ea3bad3a74839a58e682871d39bca
ACR-a96fa41318b34f0080c83b6ad373c6c2
ACR-6973bf25df2a42a4a5ddf217f5b960c9
ACR-661f98b14ba94cb3931cd326db63bead
ACR-3430d603591e46738b22f8ff9e134e7e
ACR-8322ba76c35b4b389e5d4cac879412cd
ACR-25b80ff338744a809f895c82a9509d50
ACR-b7b8770125a64f4cb747280a5fbccbec
ACR-a80ae686d9c248f58397691699398ad1
ACR-67fe4a8624ee4343a18e1397a761d1b4
ACR-2426e025a8944e92a0a917873df265d3
ACR-ae55d521dd5541a386f9cba1633dfd3a
ACR-de09f1b765fb4d25a30beb466c160f34
ACR-f8b7caaf782e4eb1a14b738f12ff86c9
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
    //ACR-396aef8331d34792b9a5cd05261e1e14
    assertThat(underTest.isSameServerUrl("https://Mycompany.Org")).isTrue();
    //ACR-31b569a9af14443882245263c4916219
    assertThat(underTest.isSameServerUrl("https://mycompany.org/")).isTrue();
    //ACR-17c2c358bdba4561b490d60815a91130
    assertThat(underTest.isSameServerUrl("http://mycompany.org")).isFalse();
    //ACR-69c0377ad82e4ec5a82b1dd2e199f451
    assertThat(underTest.isSameServerUrl("https://mycompany.org/sonarqube")).isFalse();
    //ACR-8781ab03d6a04a61906ffe6648f8d49e
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
