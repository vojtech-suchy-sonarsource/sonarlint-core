/*
ACR-82fb49f606de4f3c810317f2a35591a2
ACR-c120cd9a322a42ef9a409bbe4be0ed72
ACR-36e39164bf2349488db9e473e59ec7a2
ACR-7b40e6fbad974aa6b4cb2366a80d1a89
ACR-8cc7b7cfbefe47c5865ec71a17e81a53
ACR-4303f09eabd74308aa4f3b977906939f
ACR-518341387514427ebdde0f336ec93d1f
ACR-baa6b8c5b6cd42f8a6c297351e32d44a
ACR-b63cb7961f134bf681a62d97aedacf75
ACR-797561929e034d33ad0a3e46fef4650f
ACR-982bc9ba623f47f4b6d4f1210cb779f9
ACR-090f30cb4d4444bab1042f5f1124d214
ACR-b2cf68680e8946dca8396de751428290
ACR-f5ef834360e64c1a9eb1f8fd39340aa0
ACR-4c6926ceeb694ad1a6deff6610045f2b
ACR-118a4a0d12c84a95ab6dc1ec78c031a5
ACR-cc2e49db854c47bfb011956e0e550e46
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
