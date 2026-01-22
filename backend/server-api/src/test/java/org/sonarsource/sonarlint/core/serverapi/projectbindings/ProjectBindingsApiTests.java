/*
ACR-eaaf97b9fed44fafb9742147ac392d6a
ACR-7fc4cb6331db41f295cc224f41110872
ACR-5a4d3340d98645b2824b6dd904d83d70
ACR-d15704bc8f8c4b0198940c3ad06fbe71
ACR-7e00907a4d59456aa8d3d9e9569ee891
ACR-293a7781d19e402a8334537f93cea77f
ACR-a9dc6b4997e7440cb88f18bbdb917727
ACR-1effd70fef054f4c91335267566bc5a9
ACR-719946956be04c8ba576d0c61de86f2e
ACR-dc593a5bb951471e848f67306eec4a09
ACR-2c6b321f105c48f4a3c114e909cc5162
ACR-6b7463a00bd34f73b2198aa72dd99c08
ACR-04054a43c2fa458bb13649a2f0995f33
ACR-937aec4c31a84cf18115be3f76ea903f
ACR-fa417c05d744498e923dfffd5b6df932
ACR-00e63b282c2247848c772c08b4d0b71d
ACR-dc38ca2233d240368a683736bfa90003
 */
package org.sonarsource.sonarlint.core.serverapi.projectbindings;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import mockwebserver3.MockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogTester;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.MockWebServerExtensionWithProtobuf;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectBindingsApiTests {
  @RegisterExtension
  private static final SonarLintLogTester logTester = new SonarLintLogTester();

  @RegisterExtension
  static MockWebServerExtensionWithProtobuf mockServer = new MockWebServerExtensionWithProtobuf();

  private ProjectBindingsApi underTest;

  @BeforeEach
  void setUp() {
    underTest = new ProjectBindingsApi(mockServer.serverApiHelper());
  }

  @Nested
  class SonarQubeCloud {
    @Test
    void should_return_project_id_by_url() {
      var url = "https://github.com/foo/bar";
      var encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8);
      mockServer.addStringResponse("/dop-translation/project-bindings?url=" + encodedUrl,
        "{\"bindings\":[{\"projectId\":\"proj:123\"}]}");

      var result = underTest.getSQCProjectBindings(url, new SonarLintCancelMonitor());

      assertThat(result).isEqualTo(new SQCProjectBindingsResponse("proj:123"));
    }

    @Test
    void should_return_empty_when_no_bindings() {
      var url = "https://github.com/foo/bar";
      var encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8);
      mockServer.addStringResponse("/dop-translation/project-bindings?url=" + encodedUrl,
        "{\"bindings\":[]}");

      var result = underTest.getSQCProjectBindings(url, new SonarLintCancelMonitor());

      assertThat(result).isNull();
    }

    @Test
    void should_return_empty_when_invalid_json() {
      var url = "https://github.com/foo/bar";
      var encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8);
      mockServer.addStringResponse("/dop-translation/project-bindings?url=" + encodedUrl,
        "this is not json");

      var result = underTest.getSQCProjectBindings(url, new SonarLintCancelMonitor());

      assertThat(result).isNull();
    }

    @Test
    void should_return_empty_when_request_fails() {
      var url = "https://github.com/foo/bar";
      var encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8);
      mockServer.addResponse("/dop-translation/project-bindings?url=" + encodedUrl,
        new MockResponse.Builder().code(500).body("Internal error").build());

      var result = underTest.getSQCProjectBindings(url, new SonarLintCancelMonitor());

      assertThat(result).isNull();
    }
  }

  @Nested
  class SonarQubeServer {
    @Test
    void should_return_project_key_by_url() {
      var url = "https://github.com/foo/bar";
      var encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8);
      mockServer.addStringResponse("/api/v2/dop-translation/project-bindings?repositoryUrl=" + encodedUrl,
        "{\"projectBindings\":[{\"projectId\":\"proj:123\",\"projectKey\":\"my-project-key\"}]}");

      var result = underTest.getSQSProjectBindings(url, new SonarLintCancelMonitor());

      assertThat(result).isEqualTo(new SQSProjectBindingsResponse("proj:123", "my-project-key"));
    }

    @Test
    void should_return_empty_when_no_bindings() {
      var url = "https://github.com/foo/bar";
      var encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8);
      mockServer.addStringResponse("/api/v2/dop-translation/project-bindings?repositoryUrl=" + encodedUrl,
        "{\"projectBindings\":[]}");

      var result = underTest.getSQSProjectBindings(url, new SonarLintCancelMonitor());

      assertThat(result).isNull();
    }

    @Test
    void should_return_empty_when_invalid_json() {
      var url = "https://github.com/foo/bar";
      var encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8);
      mockServer.addStringResponse("/api/v2/dop-translation/project-bindings?repositoryUrl=" + encodedUrl,
        "this is not json");

      var result = underTest.getSQSProjectBindings(url, new SonarLintCancelMonitor());

      assertThat(result).isNull();
    }

    @Test
    void should_return_empty_when_request_fails() {
      var url = "https://github.com/foo/bar";
      var encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8);
      mockServer.addResponse("/api/v2/dop-translation/project-bindings?repositoryUrl=" + encodedUrl,
        new MockResponse.Builder().code(500).body("Internal error").build());

      var result = underTest.getSQSProjectBindings(url, new SonarLintCancelMonitor());

      assertThat(result).isNull();
    }
  }
}
