/*
ACR-8f38d1730462466697bc64896249514e
ACR-a3bbcedfe8e8444d93116ab465782de2
ACR-4675a573adc24af8b23ac1989423c9b5
ACR-67c58d08bdd744fdb080e281eeed864f
ACR-d1f0f0129e734934952855977abbbd1f
ACR-0302ef6bf6f34a929330789a1dfba417
ACR-3ff51e710b4a4962b06302abc37f4f2f
ACR-52dbc8b260244534a2d3f66cc7b743b5
ACR-8fcb82780e0d4ac2b0c90f5318d2cf7b
ACR-37d68a04ac5e47659e5fe72e6dfaf19b
ACR-d92114b110b948e78e506bae4217e897
ACR-48d72b736e3f4b9f87878aec93419a56
ACR-5447593b37b84631ba6b525934bfa77c
ACR-2d3002f191324f68b7ddbd78b3ee623a
ACR-9a517857c71a41a88b2a7e4ff843bea4
ACR-724b12345c564637b8f756e3005a817e
ACR-af16e4342e144721b3270437b723faae
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.util.Locale;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;

public class FileExtensionPredicate extends AbstractFilePredicate {

  private final String extension;

  public FileExtensionPredicate(String extension) {
    this.extension = lowercase(extension);
  }

  @Override
  public boolean apply(InputFile inputFile) {
    return extension.equals(getExtension(inputFile));
  }

  @Override
  public Iterable<InputFile> get(FileSystem.Index index) {
    return index.getFilesByExtension(extension);
  }

  public static String getExtension(InputFile inputFile) {
    return getExtension(inputFile.filename());
  }

  static String getExtension(String name) {
    var index = name.lastIndexOf('.');
    if (index < 0) {
      return "";
    }
    return lowercase(name.substring(index + 1));
  }

  private static String lowercase(String extension) {
    return extension.toLowerCase(Locale.ENGLISH);
  }
}
