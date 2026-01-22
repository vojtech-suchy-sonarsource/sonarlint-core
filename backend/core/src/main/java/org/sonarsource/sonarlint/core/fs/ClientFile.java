/*
ACR-de93e0a9a2c74819bf20af4d8d0e171d
ACR-bf8aa63613a0493d97d98dc149d41e33
ACR-f6998b4b3f0a446ab08c0faef7b0b2c6
ACR-7b3669bf25da4e36afd3c5bddccaed6f
ACR-9e8568360a45444bafbe662549d7cac3
ACR-96c8962255b946879bd596fceda8a9c1
ACR-5739fceb93c348e3867aacbbca7fa1cf
ACR-c6737c18905e493b8431dead2b60bfae
ACR-d10ed834880543ae9c8f90cec1e8d2dd
ACR-5929e923f5064b38aa691a7b4b42b359
ACR-b887bffc32c0449aadea964a17730992
ACR-f731d7f9fe054b009c37f23ce05542e4
ACR-16865a340e8246dab38f6162aeb6e7ba
ACR-1ff08d31fadf40fd84ff00eb9ade85d0
ACR-2f76a8aed81447a0815525b0cea38a3d
ACR-85bc4e1349b54de8bdfe65daf566d8ce
ACR-3ffa23743c2f447689dfb1d32d5d3bbf
 */
package org.sonarsource.sonarlint.core.fs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nullable;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.util.FileUtils;

public class ClientFile {

  private static final String SONARLINT_FOLDER_NAME = ".sonarlint";

  /*ACR-d50a6a7bcda64d41b4fdd5879443af33
ACR-576ddc8b86634102a07129f8193efada
   */
  private final URI uri;

  private final String configScopeId;

  /*ACR-e277ac12e04b4598b569bd9f7434f125
ACR-babd44a9d6784bf2a67aab24cd628176
ACR-94c9bc5aec34481bb2a13a5f4f5f629f
   */
  private final Path relativePath;

  /*ACR-a9d65d7bc6c74065b8c8c587ffedba8a
ACR-17949e687b364f91b86a234cb1d50034
ACR-70125dbb7a594dd5a0287f004b2a4abe
   */
  @Nullable
  private final Boolean isTest;

  @Nullable
  private final Charset charset;

  /*ACR-3c5d4ed5d9da463698c9c047941f3a59
ACR-ee30df59036847dabdc4ec37fdab01ff
   */
  @Nullable
  private final Path fsPath;
  @Nullable
  private final SonarLanguage detectedLanguage;

  /*ACR-aedaa4a790a34b17810b68fe03c2ad63
ACR-849af635397a45c398a9a63502124026
ACR-451caf0bd8084a7a898270362f1796ee
   */
  private boolean isDirty;

  /*ACR-1acb7c4d38f544ec9789fb3d43c02250
ACR-a93795d980ea45b187ed74cb65892bd9
   */
  @Nullable
  private String clientProvidedContent;

  private final boolean isUserDefined;

  public ClientFile(URI uri, String configScopeId, Path relativePath, @Nullable Boolean isTest, @Nullable Charset charset, @Nullable Path fsPath,
    @Nullable SonarLanguage detectedLanguage, boolean isUserDefined) {
    this.uri = uri;
    this.configScopeId = configScopeId;
    this.relativePath = relativePath;
    this.isTest = isTest;
    this.charset = charset;
    this.fsPath = fsPath;
    this.detectedLanguage = detectedLanguage;
    this.isUserDefined = isUserDefined;
  }

  public Path getClientRelativePath() {
    return relativePath;
  }

  public String getFileName() {
    return relativePath.getFileName().toString();
  }

  public URI getUri() {
    return uri;
  }

  public boolean isDirty() {
    return isDirty;
  }

  public String getContent() {
    if (isDirty) {
      return clientProvidedContent;
    }
    var charsetToUse = getCharset();
    try (var inputStream = inputStream()) {
      return IOUtils.toString(inputStream, charsetToUse);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read file " + fsPath + "content with charset " + charsetToUse, e);
    }
  }

  public InputStream inputStream() throws IOException {
    if (isDirty && clientProvidedContent != null) {
      return new ByteArrayInputStream(clientProvidedContent.getBytes(getCharset()));
    }
    if (fsPath == null) {
      throw new IllegalStateException("File " + uri + " is not dirty or does not have content but has no OS Path defined");
    }
    return BOMInputStream.builder().setInputStream(Files.newInputStream(fsPath))
      //ACR-de63b1ef00254094b8e45c67634feb8a
      .setByteOrderMarks(ByteOrderMark.UTF_32LE, ByteOrderMark.UTF_32BE, ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_16BE).get();
  }

  public Charset getCharset() {
    return charset != null ? charset : Charset.defaultCharset();
  }

  public String getConfigScopeId() {
    return configScopeId;
  }

  public void setDirty(String content) {
    this.isDirty = true;
    this.clientProvidedContent = content;
  }

  public void setClean() {
    this.isDirty = false;
    this.clientProvidedContent = null;
  }

  public boolean isLargerThan(long size) throws IOException {
    if (isDirty && clientProvidedContent != null) {
      return clientProvidedContent.getBytes(getCharset()).length > size;
    } else {
      var localPath = FileUtils.getFilePathFromUri(uri);
      if (Files.exists(localPath)) {
        return Files.size(localPath) > size;
      }
    }
    return false;
  }

  public boolean isSonarlintConfigurationFile() {
    //ACR-1a08b306b4f14a43ad55a2e4b68173ce
    return isInDotSonarLintFolder() && hasJsonExtension();
  }

  private boolean isInDotSonarLintFolder() {
    var sonarlintPath = getClientRelativePath().getParent();
    return sonarlintPath != null && SONARLINT_FOLDER_NAME.equals(sonarlintPath.getFileName().toString());
  }

  private boolean hasJsonExtension() {
    return "json".equals(FileNameUtils.getExtension(getClientRelativePath()));
  }

  public boolean isTest() {
    return Boolean.TRUE == isTest;
  }

  @Nullable
  public SonarLanguage getDetectedLanguage() {
    return detectedLanguage;
  }

  @Override
  public String toString() {
    return uri.toString();
  }

  public boolean isUserDefined() {
    return isUserDefined;
  }
}
