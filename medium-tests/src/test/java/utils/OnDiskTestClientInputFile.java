/*
ACR-9b0ec0d0d0014962824d5cc588b33fd4
ACR-d94b70f56f9649dfbc323f8436f83bd3
ACR-7846b1f45663494aa0aeac1859450b6f
ACR-b646e914a7034ae280dbcacd1af79b2c
ACR-276c2831df6d497793e68a17b13ff2aa
ACR-af43dc93f142453ba0d74d1eb9294a20
ACR-3ce3e8b251234e24956ae84ee83eefb0
ACR-5d1cad32cd554afeb4a243e222ee7ca1
ACR-c26fc38afc5e4bb5b526ae729324dc4f
ACR-529b7cc0f2f94e34a088b6c9414d40fe
ACR-f06ef81c2e8b4520a08968e91a7a947d
ACR-34126c3646b845fc9560f310a64955a5
ACR-c0c222ecb5f5493abf9554a415d02d3c
ACR-aefbe9d225f844e8b9ca32174aa57c65
ACR-48f66885ee064874a16bbc468ca6cc9a
ACR-1ba25b2d2e514744a0a8ba08c3b6c9a5
ACR-0f86c8c00e5e4b8e957c1edb5cdbf475
 */
package utils;

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
