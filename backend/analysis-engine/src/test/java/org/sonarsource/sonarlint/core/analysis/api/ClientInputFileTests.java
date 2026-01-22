/*
ACR-d2b595c1b7ff44a983172bb4bbb21d0e
ACR-d78151696b1e48f9889d37635145ca70
ACR-7d3ea8d6d61848819344b010937d01f2
ACR-19ebb908cdaf4bfcb9474e18af1bbb74
ACR-b4656ed9dd02495bbcbc1e1ea3a9a857
ACR-c1778187c5eb4fc9bdad555c1ba7cd74
ACR-b73b71d7f213425d8f47d37ce7c20fac
ACR-130c6b3d881949638f77981359ac1207
ACR-54db41cc0c724701a76413aad1d3be14
ACR-84ab0993f6a14d10b256971612db62bc
ACR-91892b5dd7a7414492e3124035b2a80a
ACR-c89f921728844476ba24f019d3626f87
ACR-cf3d77e6a03b43cdb4db023686932833
ACR-a85f129fd2cd48528a3b80991bf8d536
ACR-23a9f47baf3d45b2baf3c15e4b5839c4
ACR-5fd1280ae2514303af07bed8868813e7
ACR-2d978f89ad5840f3b98f13d0d45b1112
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
