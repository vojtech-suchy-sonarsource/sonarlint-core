/*
ACR-0b4925e7480049f1838bf5ec4a1ceda6
ACR-5a168557ce914d13a889b25d571c8151
ACR-a96395fb2056449b94590483d67591cc
ACR-175c97347901420c9cf832391fb4fcfa
ACR-4ef16efc803b427c863fb2e09ad1ebaa
ACR-41b6f34c748048469a19ba3b8e90dd0f
ACR-e9096c1a4e3d410d96c2b19ab6731103
ACR-19c7f5ed901d44a68dd4f21955ac2262
ACR-bd956a040b53444a85ab00b9c6ea6ee3
ACR-a809034708cf4583bd03a42c82138b0a
ACR-fad749b0817b4b78a90689a6d9063cc8
ACR-71b70af2705b4416bbf6b6b9a044ed0a
ACR-5c57e17c0eb94c7d95d1b6db4ffc4170
ACR-dd26f321a0e24737862e635da2ac8cf7
ACR-a9ed3d28b99241329921614f6d62218d
ACR-5f350fb6929e41f2a59784208ad073da
ACR-fe6de68456844d1fac0173a9a5f6d2dc
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
