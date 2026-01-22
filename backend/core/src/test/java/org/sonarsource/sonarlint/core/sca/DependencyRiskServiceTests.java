/*
ACR-64b700f970514c8f9f7fee2a1ab238c9
ACR-81044dab806c4d228901f7464a331e84
ACR-c7c7f8ca41e24473bd8b4318b3b973fa
ACR-71fc15b716264eb190867381b47daae8
ACR-4af9551f987d445e8776fa56f7bf7dc2
ACR-24f87f60ff7b4cb9ae2d82b61ab9cf19
ACR-0e7a7f29de424aa281916cc3a569d487
ACR-d1565d8c291f4067839dec0205047aca
ACR-41252250c81140e184aa52bfd07946f4
ACR-587b465c211a463c8a8ca7be8b3ff8ff
ACR-d79035018c5b4c7695b00e5614ac2d2a
ACR-edc0125b51c54d43b772074cbfcca29b
ACR-d584acaeb6b442219622b80f69f0488d
ACR-9f156440beeb4fae9a83a7bcf18d8569
ACR-e44e29011ab940999fdca7b9dfe7c36b
ACR-fda89decc6b544aca5d9bc581360b73d
ACR-81088f3ed4c74b75bce39b5096a735dd
 */
package org.sonarsource.sonarlint.core.sca;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.serverapi.EndpointParams;

import static org.assertj.core.api.Assertions.assertThat;

class DependencyRiskServiceTests {

  @Test
  void testBuildSonarQubeServerScaUrl() {
    var dependencyKey = UUID.randomUUID();
    assertThat(DependencyRiskService.buildDependencyRiskBrowseUrl("myProject", "myBranch", dependencyKey, new EndpointParams("http://foo.com", "", false, null)))
      .isEqualTo(String.format("http://foo.com/dependency-risks/%s/what?id=myProject&branch=myBranch", dependencyKey));
  }

}
