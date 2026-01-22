/*
ACR-babe514f21bb473c811e38a82a468405
ACR-b05104a96c204436ba2d78f504a40952
ACR-8ea2a3265a924525baa955786b6b7de0
ACR-60ce926849964e99a3bda719dc2db8e7
ACR-693e52df182a49ebbf0cd791b3c4e21a
ACR-460b1886be9b4ddcb8df01eb41e962e8
ACR-6994f3bb2eb54365a154a308433fb6b1
ACR-4632f8c8acea4d6aad1a5331af931b28
ACR-aa2651a227234cae92fb58a9784eca31
ACR-6cfbbe40d3014339b5394c927243d62f
ACR-ad525acd65ca45aea9954ee92a419718
ACR-4350110bca59439cbf9c642757bee619
ACR-baaecbd1c46745b7af04ea41a565349a
ACR-cab75bc502314689b3c129e497c8da08
ACR-429a5349a72b4f55aea9b1da54e37cb3
ACR-77b7485c7bdc4b2fb6741e88b2dc2dcb
ACR-0c93c5f88943493394ef4f0eb667181a
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
