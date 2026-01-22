/*
ACR-6d59ecc1694a45b58dc58c17f4aabe4b
ACR-d21c73a9aad34c43a2d7b9d573b1ae74
ACR-b43725a5d12548a3957bb89a12192333
ACR-968cec2a916e4440809471dccccbf9b8
ACR-2e3e9f436b1448c7b21c8a8296f51c95
ACR-1f22572f126749ecb025b0aa62ec17a7
ACR-de4535d79c594677911a0c5d82a512b3
ACR-78a19645a9424089b2812b5b16da32c3
ACR-1e57c18526804476a61e06eb6e53571f
ACR-a1da5843d8aa44c599b88a88559f90aa
ACR-39f48176a7524b5db7436984fb41e82a
ACR-965e81451a9a4572a275cf86a9978ce1
ACR-122a4578160145908e5d5b609d8ac40c
ACR-e0150596c201456f92dd83830746d184
ACR-4a8b6f1bc83240d4b9bb8a2a6375cced
ACR-53ac9ae110ca40688661fc4c3b7a3c7e
ACR-c421e000e533477f95edcea1cfa1bf5b
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
