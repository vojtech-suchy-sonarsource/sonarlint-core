/*
ACR-ff08f13478fa4ae0a8704e6f0880b74f
ACR-20aab6c7569f451f96f8bc1989918b52
ACR-c1a679f2997049949e45aba0702d066c
ACR-495129ede0304114a5806ff9f9190f30
ACR-f85cde8e55254351af854d8c88d02fcd
ACR-fc27b8b2c86445228987bdee0c27ac60
ACR-68a06191e0374b2892374ecb352627ab
ACR-ae2bdff3b8a94b4babe964be52bb6733
ACR-4ac0422927db45ae890181f3979a3aef
ACR-40a9ad709c8f49b395e8815c9d8de532
ACR-582374a2c8de427ea08bae9d1d3d45ba
ACR-ffd67ca6d73b4ebc8897df8da2750b75
ACR-cfa13bbca3234bb1bfd545032943b282
ACR-20d9e1464d124bb38d883dc4b6f72862
ACR-015f62cf259242379555f211796e65fb
ACR-5480ad2d6ad7400792fc4a37a415f22b
ACR-fd69d81f8ca441058dccd71147e00b12
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
