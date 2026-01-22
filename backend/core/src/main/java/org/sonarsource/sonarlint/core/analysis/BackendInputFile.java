/*
ACR-7e462de888934a8fbb14b49db6ca256d
ACR-b19f7888246149119a75b1bcd88c6ed6
ACR-e6f23cb7b3c441de8eb3403fa4fd9030
ACR-255dff8170224b4094e37a067785da68
ACR-48f70315926842308758133ca57a7b78
ACR-b9ef3d0c46b6403cb2f36a92d5da094c
ACR-f734f1af992e42e18ec21aaf3719c5ae
ACR-366f79ec9c714689ae9da1e3e16a7d3b
ACR-1c722c1e407c427bbbd8efec3a5214a9
ACR-491ba2824e854504882f5150198c357a
ACR-1e76669dff6e472b8177e5418217dce6
ACR-0acee57647a74378b2b3a567559a86a3
ACR-3cc6337fd8ae46ab87f8dde9c61560c8
ACR-80ad5c57999443b4a499fea42586ecc8
ACR-0ccb189caf0f492fae7b61b58a567179
ACR-7dcfbc5119b74d85bca80b17e5bbaebb
ACR-a288c5a8a28048fe98e155f87223ab80
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
