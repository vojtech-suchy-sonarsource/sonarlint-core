/*
ACR-37577c0760c64f7da916600b364eca49
ACR-7fcaf788e7f54ed19d6263a9b482366e
ACR-f73d16cf254f41d09618f695e90e2ac4
ACR-885115bcc6d2453e85dbc01a2d8fab24
ACR-0bd6c888eacd41edb3cb543114315211
ACR-7742edc708654784b0d72f9794b1ee5f
ACR-c32566b4f2264a9994b935d2fa110074
ACR-b7e8f3fc8d1b486390454001ecd0fcce
ACR-e4d21c20d840467aaa1f995e5407b3e1
ACR-320069550a1947728adadb1c93187506
ACR-57baac1d38d34f48ae370c462162a703
ACR-152ae72ed03042719ee27586657bea89
ACR-4437be17092b4a338fd2a236dfbd532a
ACR-7016e14e954949e599b9d2a9b6ef5ab5
ACR-4f34a38f5eaf4e59b7fc06684253a9e8
ACR-17fffc4f9c4e4d68911fe044672e34f9
ACR-5b2abc8d5f944b6c84b5e7ce22681c51
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

  /*ACR-a12c4ce99ad141a8a2bcd8085d6f452a
ACR-b58eefa6c729423d9745575f1bdf2724
   */
  private final URI uri;

  private final String configScopeId;

  /*ACR-93ebc242b639456a82ee12674ad00656
ACR-34b4b18a02854131b756d2e6e0e265d7
ACR-5c460a73e6544418a71f376a6c010686
   */
  private final Path relativePath;

  /*ACR-8828b81f29e14893bdffd647c3d19722
ACR-f9f6e93741ae4f558c0d8b61876046fc
ACR-c163ca3fc22f45f6ad55581e8fa470aa
   */
  @Nullable
  private final Boolean isTest;

  @Nullable
  private final Charset charset;

  /*ACR-f351931a089c400993f68fc867b75b54
ACR-0ae15e099d1a42dea74123136df93896
   */
  @Nullable
  private final Path fsPath;
  @Nullable
  private final SonarLanguage detectedLanguage;

  /*ACR-199e69d4bc004121a3cdec5c555e50d7
ACR-9beffe3d983b46338e3fb1794d337c8d
ACR-1d301838b8f345e691b78c480396ccce
   */
  private boolean isDirty;

  /*ACR-a56f75feed3c4957a74a6dabc5dd2bae
ACR-29ab767db9e244a48b2cf3617746b469
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
      //ACR-0fdb07999a8b4349ba14b0d9448843c0
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
    //ACR-221428eeb3c74072b06efc06cb72c905
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
