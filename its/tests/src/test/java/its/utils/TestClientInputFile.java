/*
ACR-2442675786e742abaeef62b8b58fc135
ACR-d4e1c4d10b3c40e992212453a7d030e1
ACR-eafcd7a1e5354cc7843012affd4f1154
ACR-4115de1ab33141d89d61bf46b2534b35
ACR-6c4315e332f74d378f8bbbf5c1b76d92
ACR-9c8c8abd3e9a491387c4fe0d68f7415f
ACR-b920a02c834a448692e936451af8bcc9
ACR-790a5daafd574119b386d40d4db69968
ACR-c95afc93cab548ae9e85e93842791c39
ACR-65572c944b754dcda1eb4ee9b0632aa5
ACR-feaec7b312644302b7f18535b05f0138
ACR-b7d699fa616e4f2689a2ebdc3eb26a10
ACR-bc63231d2d7c46038753e5d91f130def
ACR-fd3a34c9069d4f5fa40debe8f2fcd242
ACR-19557d8819db424084a6e98bbc36b697
ACR-f95f9462b5e94946a657a41610acef3d
ACR-ccb2af57610f43898c21cdf23c278f91
 */
package its.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import org.sonarsource.sonarlint.core.analysis.api.ClientInputFile;

public class TestClientInputFile implements ClientInputFile {
  private final Path path;
  private final boolean isTest;
  private final Charset encoding;
  private final Path baseDir;

  public TestClientInputFile(final Path baseDir, final Path path, final boolean isTest, final Charset encoding) {
    this.baseDir = baseDir;
    this.path = path;
    this.isTest = isTest;
    this.encoding = encoding;
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
  public URI uri() {
    return path.toUri();
  }
}
