/*
ACR-2120666f2d444476a5a42f524abec0bb
ACR-747058e1d2ca48b8a1268b563a1cce66
ACR-ea2b2abd7a0b429d861902a5d794c4fd
ACR-67dffed8f2824818b41d5e0912c00732
ACR-6ef18e4d3509470ebe834b01a9cde30f
ACR-b57d9555241140998442cf2ecf5e1d32
ACR-f4a32acc641047c79679dfe4ce622502
ACR-47e4080c93d348b59be13ed1efddd565
ACR-25fae6eaef2d4e5f8756d86dbdf923f0
ACR-e7c06a5b52774ac99d867a2b8c1c77ea
ACR-1cdd109472ec4a88baf5d91f5223e59d
ACR-3d3f5d304ae04782ad3a3642354d778f
ACR-5bd20e45e8184a47b9eb289476c43c8a
ACR-0db709d80475436b897e0fa7f2adcfb7
ACR-23d46b2802dc45b4b22168cea82d48ab
ACR-c9db5461121e485fb74e6b2d7b8eb3ec
ACR-b264d8b57ad2444fa5954e11b93ee889
 */
package org.sonarsource.sonarlint.core.analysis;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import org.sonarsource.sonarlint.core.analysis.api.ClientInputFile;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.util.FileUtils;
import org.sonarsource.sonarlint.core.fs.ClientFile;

public class BackendInputFile implements ClientInputFile {
  private final ClientFile clientFile;

  public BackendInputFile(ClientFile clientFile) {
    this.clientFile = clientFile;
  }

  @Override
  public String getPath() {
    return FileUtils.getFilePathFromUri(clientFile.getUri()).toAbsolutePath().toString();
  }

  @Override
  public boolean isTest() {
    return clientFile.isTest();
  }

  @Override
  public Charset getCharset() {
    return clientFile.getCharset();
  }

  @Override
  public ClientFile getClientObject() {
    return clientFile;
  }

  @Override
  public InputStream inputStream() throws IOException {
    return clientFile.inputStream();
  }

  @Override
  public String contents() {
    return clientFile.getContent();
  }

  @Override
  public String relativePath() {
    return clientFile.getClientRelativePath().toString();
  }

  @Override
  public URI uri() {
    return clientFile.getUri();
  }

  @Override
  public SonarLanguage language() {
    return clientFile.getDetectedLanguage();
  }

  @Override
  public boolean isDirty() {
    return clientFile.isDirty();
  }
}
