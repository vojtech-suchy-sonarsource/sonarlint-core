/*
ACR-5fd97e8a18104e57b2ccfe13150c8059
ACR-54d9b13bf9864e1b919af566d84fef56
ACR-2f9f44fc4ce8403db980c72c4340ac0d
ACR-542752b7ace64193946003a71f56c87b
ACR-6911dea7e1ab46d99c03834830565aba
ACR-2eea2fd172ee44159e042d247d568770
ACR-13b2112555be46ff90ab506d209d300d
ACR-c7a8d2aa46ac4ad38200e68ac4526ff1
ACR-f80b58fb899548189d71d0d89aa36cba
ACR-10fb9ba03d304add81e40965ff4dd52b
ACR-fa2721b5226a4252a62206d0d21a3b0e
ACR-68cc979d762543a8bc2e24399a4131e1
ACR-91b03e22097f4ddca7cb992f89363151
ACR-5b2c712e57d947a786bcef7547adc575
ACR-285f49e9dc0d4f5896b177ed44ef90fe
ACR-cb6fe74af3804b7289fb80a8d28cd980
ACR-b53815fc1c84415bbef5d01f4c1fe5dd
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonarsource.api.sonarlint.SonarLintSide;

@SonarLintSide
public class InputFileIndex implements FileSystem.Index {

  private final Set<InputFile> inputFiles = new LinkedHashSet<>();
  private final Map<String, Set<InputFile>> filesByNameIndex = new LinkedHashMap<>();
  private final Map<String, Set<InputFile>> filesByExtensionIndex = new LinkedHashMap<>();
  private final SortedSet<String> languages = new TreeSet<>();

  @Override
  public Iterable<InputFile> inputFiles() {
    return inputFiles;
  }

  public void doAdd(InputFile inputFile) {
    if (inputFile.language() != null) {
      languages.add(inputFile.language());
    }
    inputFiles.add(inputFile);
    filesByNameIndex.computeIfAbsent(inputFile.filename(), f -> new LinkedHashSet<>()).add(inputFile);
    filesByExtensionIndex.computeIfAbsent(FileExtensionPredicate.getExtension(inputFile), f -> new LinkedHashSet<>()).add(inputFile);
  }

  @Override
  public InputFile inputFile(String relativePath) {
    throw new UnsupportedOperationException("inputFile(String relativePath)");
  }

  @Override
  public Iterable<InputFile> getFilesByName(String filename) {
    return filesByNameIndex.get(filename);
  }

  @Override
  public Iterable<InputFile> getFilesByExtension(String extension) {
    return filesByExtensionIndex.get(extension);
  }

  protected SortedSet<String> languages() {
    return languages;
  }

}
