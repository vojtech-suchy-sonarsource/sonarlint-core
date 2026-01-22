/*
ACR-8e6b53bf84df4899a5f31a7c9fee4f1b
ACR-e63deab5ca46470d800f0630217cf29e
ACR-3edd34e9862648ab96b64eaa3f78ee4b
ACR-bd360f4cabc9453a86d344166ae87d51
ACR-dd6bd14fcdbe41bfa72d36dd8bc3d6b5
ACR-cc6bc3bdab804379baec3d78af16c66d
ACR-04a9ab406da343b0ab7836f7158bc1ba
ACR-6ea997bb53704dcd841f63ac036be216
ACR-2914701180d04c01b09ff0fdbfd9609d
ACR-3de0e5ccd9aa48109ac73b7f51b5e582
ACR-84bc88312485405585adaf0f2a35e2e5
ACR-a5f89b0ce21542d992e770ac0065095a
ACR-c0c52d1ab6984ee0a69eeb061711103d
ACR-5d19b2850e564b50940760678d45dfc3
ACR-1daea1adbfdb4053ae580be298f7863c
ACR-dc528a84d5ce46bfbeb502ae942e8fb1
ACR-bdf6844c6c5f48de82bdcf0ff4a55614
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
