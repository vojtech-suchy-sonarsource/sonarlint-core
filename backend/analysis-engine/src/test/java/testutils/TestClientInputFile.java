/*
ACR-ec659c8990174780b6fa85c141548010
ACR-3cf3263f3b714020a52092cb5561ba11
ACR-252519e584ea4af69cdd3781ad30d1be
ACR-e00f763ecbe64fe6b153058da9e031ea
ACR-8d96226fa4654ab0a896478110840e50
ACR-d899721c7f2c416c94d969f921eb27bf
ACR-394a33af6fd640048b1e804e1d083e43
ACR-a61e9b3ce07b4d30bfd596b277de6905
ACR-26b26022a5c245bda1060db46c263e36
ACR-a5aba9525a394482a6d1c88e195b3724
ACR-57c4cb5befce411f9115a86e541c9b3c
ACR-ab0cb5ae62e94a4694e6d806fed552b5
ACR-7c87ee61f22349af9158c92f1b41a269
ACR-b016345e86ba4ba082f8e44bc92e4023
ACR-653f16a18f5d4fd89e230f6f28212049
ACR-f84f92b2e49e4e55a5bce101d0596c2e
ACR-14549cf99b914519aafefb22710d315b
 */
package testutils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.analysis.api.ClientInputFile;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;

public class TestClientInputFile implements ClientInputFile {
  private final Path path;
  private final boolean isTest;
  private final Charset encoding;
  private final SonarLanguage language;
  private final Path baseDir;

  public TestClientInputFile(final Path baseDir, final Path path, final boolean isTest, final Charset encoding, @Nullable SonarLanguage language) {
    this.baseDir = baseDir;
    this.path = path;
    this.isTest = isTest;
    this.encoding = encoding;
    this.language = language;
  }

  @Override
  public String getPath() {
    return path.toString();
  }

  @Override
  public String relativePath() {
    return baseDir.relativize(path).toString();
  }

  @Override
  public boolean isTest() {
    return isTest;
  }

  @Override
  public Charset getCharset() {
    return encoding;
  }

  @Override
  public <G> G getClientObject() {
    return null;
  }

  @Override
  public InputStream inputStream() throws IOException {
    return Files.newInputStream(path);
  }

  @Override
  public String contents() throws IOException {
    return new String(Files.readAllBytes(path), encoding);
  }

  @Override
  public SonarLanguage language() {
    return language;
  }

  @Override
  public URI uri() {
    return path.toUri();
  }
}
