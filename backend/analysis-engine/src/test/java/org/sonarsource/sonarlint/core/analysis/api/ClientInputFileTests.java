/*
ACR-95619cd71fd340e5afaca508dd6a601f
ACR-432f1531a5d849128a0f619f243cbe2f
ACR-02e02c04d1a74620ba453782dafaf99f
ACR-7034b03e86254f6a9b7d1328fdf1331b
ACR-802147ae8b77453599d03d7cf397e625
ACR-dff08c13c5694de88ce6e7cddcbe80fd
ACR-c24b1ba77d984882bdbfd3fe0df080bd
ACR-6fbca4ae519b400d84e42baabade4812
ACR-c3e980c679a64cb8987a8e8336439873
ACR-a2870f1ffd414323a5859e84146e5ab9
ACR-a23c0b485cdb4ab98f04b428cf22c592
ACR-9d53569687b342638c8b8a7979fbd2cc
ACR-021ec9e45f934e91b9833a06e397271f
ACR-2181042cf0674bb3830804c169fee794
ACR-17e30416e20e43499a15c916043ea70b
ACR-c3809e5890934030a1d00f0f31dd84d2
ACR-fa5a7fe4cf8d4ada8dc7f70f34bd7396
 */
package org.sonarsource.sonarlint.core.analysis.api;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

class ClientInputFileTests {

  @Test
  void testDefaults(@TempDir Path tempDir) {
    var path = tempDir.resolve("Foo.java");
    var underTest = new ClientInputFile() {
      @Override
      public boolean isTest() {
        return false;
      }

      @Override
      public InputStream inputStream() {
        return null;
      }

      @Override
      public String getPath() {
        return path.toAbsolutePath().toString();
      }

      @Override
      public String relativePath() {
        return path.getParent().toString();
      }

      @Override
      public <G> G getClientObject() {
        return null;
      }

      @Override
      public Charset getCharset() {
        return null;
      }

      @Override
      public String contents() {
        return null;
      }

      @Override
      public URI uri() {
        return path.toUri();
      }
    };

    assertThat(underTest.language()).isNull();
    assertThat(underTest.uri()).hasScheme("file");
    assertThat(underTest.uri().getPath()).endsWith("/Foo.java");
  }

}
