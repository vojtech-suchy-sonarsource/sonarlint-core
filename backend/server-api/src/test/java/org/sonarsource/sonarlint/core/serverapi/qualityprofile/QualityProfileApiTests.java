/*
ACR-55995b7eb434483299f6ff5df41ed9e7
ACR-de68e639c5d9411f9b9d02d1a1556025
ACR-1fb73e511e794ca5ade83a868ba80e8c
ACR-522d8e55495f4b80ac78d907d840a9a4
ACR-8dfba1711be646e582fd79d76adfd6c1
ACR-0e457cae290f453ab37da358561017f2
ACR-e8da227228f640f39359658d90f1e571
ACR-41357f10f5a84ccb8fc02fe0255624a6
ACR-5255fe771f234ff1982c69f7209b16ea
ACR-d2ec1620ec144dd490de51a3aeb31447
ACR-9fc5a46b915c422ebdd51cf54f348c76
ACR-2158d5fecd0044a8b6d664254ea76ebc
ACR-ea10fada7d20401ead508e57063aec66
ACR-dd62f397eaaf410381f52d9867e881a6
ACR-1229904b73a341389cdd82dac3e61af9
ACR-609bc205d8674d149c921acf78f9ee7c
ACR-1a3a60733c4b4a6b83f22c062d039a9c
 */
package org.sonarsource.sonarlint.core.serverapi.qualityprofile;

import mockwebserver3.MockResponse;
import okhttp3.Headers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.MockWebServerExtensionWithProtobuf;
import org.sonarsource.sonarlint.core.serverapi.exception.ProjectNotFoundException;
import org.sonarsource.sonarlint.core.serverapi.exception.ServerErrorException;
import org.sonarsource.sonarlint.core.serverapi.proto.sonarqube.ws.Qualityprofiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QualityProfileApiTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  @RegisterExtension
  static MockWebServerExtensionWithProtobuf mockServer = new MockWebServerExtensionWithProtobuf();

  @Test
  void should_throw_when_the_endpoint_is_not_found() {
    var underTest = new QualityProfileApi(mockServer.serverApiHelper());

    mockServer.addResponse("/api/qualityprofiles/search.protobuf?project=projectKey", new MockResponse(404, Headers.EMPTY, ""));

    var cancelMonitor = new SonarLintCancelMonitor();
    assertThrows(ProjectNotFoundException.class, () -> underTest.getQualityProfiles("projectKey", cancelMonitor));
  }

  @Test
  void should_throw_when_a_server_error_occurs() {
    var underTest = new QualityProfileApi(mockServer.serverApiHelper());

    mockServer.addResponse("/api/qualityprofiles/search.protobuf?project=projectKey", new MockResponse(503, Headers.EMPTY, ""));

    var cancelMonitor = new SonarLintCancelMonitor();
    assertThrows(ServerErrorException.class, () -> underTest.getQualityProfiles("projectKey", cancelMonitor));
  }

  @Test
  void should_return_the_quality_profiles_of_a_given_project() {
    var underTest = new QualityProfileApi(mockServer.serverApiHelper());

    mockServer.addProtobufResponse("/api/qualityprofiles/search.protobuf?project=projectKey", Qualityprofiles.SearchWsResponse.newBuilder()
      .addProfiles(Qualityprofiles.SearchWsResponse.QualityProfile.newBuilder()
        .setIsDefault(true)
        .setKey("profileKey")
        .setName("profileName")
        .setLanguage("lang")
        .setLanguageName("langName")
        .setActiveRuleCount(12)
        .setRulesUpdatedAt("rulesUpdatedAt")
        .setUserUpdatedAt("userUpdatedAt")
        .build())
      .build());

    var qualityProfiles = underTest.getQualityProfiles("projectKey", new SonarLintCancelMonitor());

    assertThat(qualityProfiles)
      .extracting("default", "key", "name", "language", "languageName", "activeRuleCount", "rulesUpdatedAt", "userUpdatedAt")
      .containsOnly(tuple(true, "profileKey", "profileName", "lang", "langName", 12L, "rulesUpdatedAt", "userUpdatedAt"));

  }
}
