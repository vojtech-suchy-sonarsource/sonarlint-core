/*
ACR-f9e15c67830b48afa8f2bcb8a654b54f
ACR-5e8549b43f294a6e99bc7d378630bf8d
ACR-194c69f3623c49f68d4cf90bb6c11053
ACR-01c1b3f33aff47e6b5242c04ff456c38
ACR-aded98ee3f3841a891605d612785cd9b
ACR-ad48f27276144a2fad292ae5db003e53
ACR-8df01f61eaa343b7b0f5a408ba7fc2c7
ACR-4f20ffbbf37e44bc99d97cfaf50c71ef
ACR-d34e673f2856437882c4a027809bb2a2
ACR-d9e3fa45907d4364ba8846fe888c1fe7
ACR-2a0719f1e1074647aef3f09d664e90ab
ACR-c811e92bb8cf4be8a1c042465a5c5c44
ACR-6af9ce3492df4a609fa3ceef489dea1d
ACR-d72c293074f94c72a09e84dcea22e9e0
ACR-563bb19705d947eeb69033ef579a144a
ACR-4a51e190678f4309861e35b74fa419a0
ACR-52544e76305f4dbc988dde5fca773a0e
 */
package testutils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import javax.annotation.Nullable;
import org.sonar.api.utils.PathUtils;
import org.sonarsource.sonarlint.core.analysis.api.ClientInputFile;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;

public class InMemoryTestClientInputFile implements ClientInputFile {
  private final boolean isTest;
  private final SonarLanguage language;
  private final String relativePath;
  private final String contents;
  private final Path path;

  public InMemoryTestClientInputFile(String contents, String relativePath, @Nullable Path path, final boolean isTest, @Nullable SonarLanguage language) {
    this.contents = contents;
    this.relativePath = relativePath;
    this.path = path;
    this.isTest = isTest;
    this.language = language;
  }

  @Override
  public String getPath() {
    if (path == null) {
      throw new UnsupportedOperationException("getPath");
    }
    return PathUtils.sanitize(path.toString());
  }

  @Override
  public String relativePath() {
    return relativePath;
  }

  @Override
  public boolean isTest() {
    return isTest;
  }

  @Override
  public SonarLanguage language() {
    return language;
  }

  @Override
  public Charset getCharset() {
    return StandardCharsets.UTF_8;
  }

  @Override
  public <G> G getClientObject() {
    return null;
  }

  @Override
  public InputStream inputStream() throws IOException {
    return new ByteArrayInputStream(relativePath.getBytes(StandardCharsets.UTF_8));
  }

  @Override
  public String contents() throws IOException {
    return contents;
  }

  @Override
  public URI uri() {
    if (path == null) {
      return URI.create("file://" + relativePath);
    }
    return path.toUri();
  }
}
