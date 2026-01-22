/*
ACR-24515fe1681e4906900f0dc37b5c1844
ACR-cb5e1f8c0c7649fd898bb9d47fe3d5e1
ACR-b5baf7e2398844119bf06baddd025d12
ACR-924460c4b1c540fdbc727d00128ed561
ACR-180d175f7966457eb9900e962a260911
ACR-b348c6ed2e0b4f4b9a10b87054d1e63b
ACR-a069fd78de964ab9b32625311e0dc2c6
ACR-33dcd5dfb441478496bf7db1a913e5cb
ACR-2932164dc737487b9528d608a1483817
ACR-e5320e17d0904b9d9b5cce73de94761b
ACR-d048b9303b7b49d5bc5c60663d9fc520
ACR-caf091c2e9e54964b1873f8e6185e502
ACR-8325167bb90b451a85ac68d628fc3c89
ACR-a308f4e204874e1b98fa933e45286c23
ACR-9e0f296e93b54268975ebb370f1e9229
ACR-968584c9b0054b8c844a0e0e4d462296
ACR-3bed608410f2442b8ed91f55a20f59d0
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

public class OnDiskTestClientInputFile implements ClientInputFile {
  private final Path path;
  private final boolean isTest;
  private final Charset encoding;
  private final SonarLanguage language;
  private final String relativePath;

  public OnDiskTestClientInputFile(final Path path, String relativePath, final boolean isTest, final Charset encoding) {
    this(path, relativePath, isTest, encoding, null);
  }

  public OnDiskTestClientInputFile(final Path path, String relativePath, final boolean isTest, final Charset encoding, @Nullable SonarLanguage language) {
    this.path = path;
    this.relativePath = relativePath;
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
  public URI uri() {
    return path.toUri();
  }
}
