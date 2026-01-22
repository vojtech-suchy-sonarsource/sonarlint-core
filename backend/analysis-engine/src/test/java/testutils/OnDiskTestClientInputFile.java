/*
ACR-a89af116be7c42589fc5dc31c083e7d1
ACR-024f1c84f1b74ae4b2ff24225ea25b30
ACR-0ee4edc406f5444a8bcfac3e7616a8bf
ACR-63e31c11cf6b4c17bce0cff817d44f73
ACR-e88007a7b03a4f0a85b03ff12a887bac
ACR-5cddb06fdb0b4adf84d1ae851253663e
ACR-187b27b239564afa85b8ee36f00ffcd6
ACR-83f06b1076ef4dd285821d63f1dc3b40
ACR-748221cffc164158b2ab856023be2e7c
ACR-2916086bf72044fc92c9b136f2733875
ACR-5795d99f128244858a5845c442391980
ACR-0d91973176e94f288c2316c1b03bd463
ACR-f2d0c82890914c8c96d6ae1c9c1701d6
ACR-6936991f3e574d2cb8e6919d5e049936
ACR-96d3ccf99665461ab7913365654b4845
ACR-0bae20da8f7642339df7bb89bf1586a9
ACR-de397bbc12324a51a8c99d75819ea4f5
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
