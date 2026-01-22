/*
ACR-c19e3ef97bb346c2afd702310d79682a
ACR-313f1171956343229d79e68c612695ff
ACR-85ebd2ffdf4f44a3bedb4d666f99f5ae
ACR-9e6d72d500f44445a147173bf135cfbb
ACR-c28006da59214dfe9520058286bafbc1
ACR-628e799fc4544e2bb5ee62fc279ddd6e
ACR-1e02e7b6309e43289aac73b2655a9bb3
ACR-b5756d1f2afe4e65a25e68e43025bdea
ACR-535131256cb142e7ba395070896c8f22
ACR-6b8004e1ae0b4b62b05bc64286fcea0c
ACR-504289c71c3e4520b6b6c3caa9b171b5
ACR-61db6f907e6c4cc68266f30e3916fdff
ACR-0c88284240524be7a41fee0cb351681d
ACR-92b9bccc3cf64c1cb9aafd57d744ba81
ACR-8696e37fbffd4d57ac147952f120f4de
ACR-d41d763b774c4711a873ac92a46507f1
ACR-07e070f264df4d93b6054f8b50b23506
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
