/*
ACR-9d4482d244c74b30a05e4924ffc9e78e
ACR-f79774faa8a14b829e5a5adddf751ad0
ACR-2c40901c2ad545ab9f55cb834823569b
ACR-cd3d4679715a426d810f5c66d8fa6620
ACR-0d3875fde28e438198ee05f5872558bc
ACR-dcc723b9ceed4fcdb3d6dd3a89944031
ACR-067614e1c57243debb5a7616e5031832
ACR-2d719011808c489584f77452515d796d
ACR-d41acc8f572443cbb5c7c802201bd769
ACR-f34dd165f0034928be47b418c291c4ee
ACR-82cc332af42a42c59b33e325d6cca1dc
ACR-04139d00a81742d4aad10a4854d251e2
ACR-2ca8c2832bb34b058f070ebdcbd99cff
ACR-7a1e3ebeb40a41dda08ad59dcee5e20a
ACR-d686f641ec0a43e2a1c2a1dd5d32aed1
ACR-f96e72ab50384a39a6529ce93677ead6
ACR-1881f7d6faec408db4cdab813056c02d
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
