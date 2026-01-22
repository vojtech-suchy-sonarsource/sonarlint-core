/*
ACR-ce1f81f44d4f46c3849669d1b053edc7
ACR-ec52882ce54b4a41aba4bb17a64b90bd
ACR-6ecd7c6a18eb4d86a404455415d43521
ACR-7475fad637124191abba0e80d0a16097
ACR-63d452a7d81a4108918b98b3425ad0ed
ACR-abf8448319064d7f9a161fc0ce2847a1
ACR-ad8cdc87be85437ca5c58bf17419c734
ACR-9843f6561f6846ea8d4c07d247a1b34e
ACR-612821f2076446fb91a42b89adf4a2e8
ACR-6c902326d1254e5686fe04514226cced
ACR-5e84a50bdcea498ab233c94b927d5638
ACR-fdf695883cb249e59adef5641a71401d
ACR-b1fc7ddcbca84f32ad9e9e85def38b8d
ACR-ad61f4b2df514ca59b8f4cf450710752
ACR-aa52acfe9b7242b9af288a52d95ca3c8
ACR-de9ab8170b1c4a469205cfbdb7f88a4a
ACR-5768b2c111b34c13a64b36a5ac809731
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputFile.Status;
import org.sonarsource.sonarlint.core.analysis.container.analysis.SonarLintPathPattern;

/*ACR-f33a6b83d3eb456d8ecf4f475b903902
ACR-d7f166e9667d47e38209fac7a6b60ee3
ACR-455665e1acf14725a0a8ee1d3870284c
ACR-e1827533e17e4019885e2e27c24080fa
 */
public class DefaultFilePredicates implements FilePredicates {

  /*ACR-d52631bd4b8f428189c9023ca100a0d0
ACR-72a54aaa367843c5b7006dbf034ad60a
   */
  @Override
  public FilePredicate all() {
    return TruePredicate.TRUE;
  }

  /*ACR-f5e211cbf8d54cada17db55fdf7311d3
ACR-6fba22e8d5aa40ac91b75f3d6a718675
   */
  @Override
  public FilePredicate none() {
    return FalsePredicate.FALSE;
  }

  @Override
  public FilePredicate hasAbsolutePath(String s) {
    throw new UnsupportedOperationException("hasAbsolutePath");
  }

  /*ACR-17e28330665f49cc8d686fc751cd6923
ACR-2a22870d369d473fb29b350d2ebae141
   */
  @Override
  public FilePredicate hasRelativePath(String s) {
    throw new UnsupportedOperationException("hasRelativePath");
  }

  @Override
  public FilePredicate hasURI(URI uri) {
    return new URIPredicate(uri);
  }

  @Override
  public FilePredicate matchesPathPattern(String inclusionPattern) {
    return new PathPatternPredicate(new SonarLintPathPattern(inclusionPattern));
  }

  @Override
  public FilePredicate matchesPathPatterns(String[] inclusionPatterns) {
    if (inclusionPatterns.length == 0) {
      return TruePredicate.TRUE;
    }
    var predicates = new FilePredicate[inclusionPatterns.length];
    for (var i = 0; i < inclusionPatterns.length; i++) {
      predicates[i] = new PathPatternPredicate(new SonarLintPathPattern(inclusionPatterns[i]));
    }
    return or(predicates);
  }

  @Override
  public FilePredicate doesNotMatchPathPattern(String exclusionPattern) {
    return not(matchesPathPattern(exclusionPattern));
  }

  @Override
  public FilePredicate doesNotMatchPathPatterns(String[] exclusionPatterns) {
    if (exclusionPatterns.length == 0) {
      return TruePredicate.TRUE;
    }
    return not(matchesPathPatterns(exclusionPatterns));
  }

  @Override
  public FilePredicate hasPath(String s) {
    throw new UnsupportedOperationException("hasPath");
  }

  @Override
  public FilePredicate is(File ioFile) {
    //ACR-8c0a0f6344f54f28a307c25500066cdc
    return hasURI(ioFile.toURI());
  }

  @Override
  public FilePredicate hasLanguage(String language) {
    return new LanguagePredicate(language);
  }

  @Override
  public FilePredicate hasLanguages(Collection<String> languages) {
    List<FilePredicate> list = new ArrayList<>();
    for (String language : languages) {
      list.add(hasLanguage(language));
    }
    return or(list);
  }

  @Override
  public FilePredicate hasLanguages(String... languages) {
    List<FilePredicate> list = new ArrayList<>();
    for (String language : languages) {
      list.add(hasLanguage(language));
    }
    return or(list);
  }

  @Override
  public FilePredicate hasType(InputFile.Type type) {
    return new TypePredicate(type);
  }

  @Override
  public FilePredicate not(FilePredicate p) {
    return new NotPredicate(p);
  }

  @Override
  public FilePredicate or(Collection<FilePredicate> or) {
    return OrPredicate.create(or);
  }

  @Override
  public FilePredicate or(FilePredicate... or) {
    return OrPredicate.create(Arrays.asList(or));
  }

  @Override
  public FilePredicate or(FilePredicate first, FilePredicate second) {
    return OrPredicate.create(Arrays.asList(first, second));
  }

  @Override
  public FilePredicate and(Collection<FilePredicate> and) {
    return AndPredicate.create(and);
  }

  @Override
  public FilePredicate and(FilePredicate... and) {
    return AndPredicate.create(Arrays.asList(and));
  }

  @Override
  public FilePredicate and(FilePredicate first, FilePredicate second) {
    return AndPredicate.create(Arrays.asList(first, second));
  }

  @Override
  public FilePredicate hasFilename(String s) {
    return new FilenamePredicate(s);
  }

  @Override
  public FilePredicate hasExtension(String s) {
    return new FileExtensionPredicate(s);
  }

  @Override
  public FilePredicate hasStatus(Status status) {
    throw new UnsupportedOperationException("hasStatus");
  }

  @Override
  public FilePredicate hasAnyStatus() {
    return new StatusPredicate(null);
  }
}
