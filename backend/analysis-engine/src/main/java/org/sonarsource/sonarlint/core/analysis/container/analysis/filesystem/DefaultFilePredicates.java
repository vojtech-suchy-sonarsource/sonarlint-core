/*
ACR-fafd96308408414f92bad9845f815b6f
ACR-f61c34c3e82c4b10acd644d30391f1de
ACR-9ecb3be4074e4a7fbd583a04d02dc37a
ACR-92dbbd8b9357404fb1acc57aad4068ca
ACR-d04417965c204bc7a48b2f84465ce963
ACR-5a27a53becc94a5495bdad5391aa1b78
ACR-239984d5185741bfac8ac7e655abc919
ACR-9e42a426e2c84f289bd8f9156300f7e7
ACR-367eed70b9ba4fe484550cf5313882c9
ACR-567bdfb31841463a9f08696dbbf0563c
ACR-de298cee7b9c4c3fa640a5af9b3d2474
ACR-31eb8f06540b49d58aadaf900340e6e3
ACR-430a78a9474544bf946629fb68b91fb8
ACR-c28e41ccd377468590590f8aae557448
ACR-e30158af70104fa5b8eb442c1cf0a4d3
ACR-f6b9513b8ae74fb1b24015f0a07eeb73
ACR-0610098ef5e04fd2b70b9cfa1bea5507
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

/*ACR-07cebedec7b746e1aec4fb595aaa2a04
ACR-d300e00a675f425e96b56382b23f3c72
ACR-5029dadff2374bff8f331b6157efa9ca
ACR-ef39b84de82d4e61978f7df4a374fa09
 */
public class DefaultFilePredicates implements FilePredicates {

  /*ACR-c66ab7cf1d854f8395f4a11de68e2a48
ACR-c170ef67a1c8457983503596f338db03
   */
  @Override
  public FilePredicate all() {
    return TruePredicate.TRUE;
  }

  /*ACR-0327292f7212400fa6b279a542eede43
ACR-bf02321139fa4c55b9f77d70e44e2fe9
   */
  @Override
  public FilePredicate none() {
    return FalsePredicate.FALSE;
  }

  @Override
  public FilePredicate hasAbsolutePath(String s) {
    throw new UnsupportedOperationException("hasAbsolutePath");
  }

  /*ACR-bacaaa62a77942b782b2b1a5ac16de04
ACR-f9a8945107674aa49ea1c0f6c67f40bd
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
    //ACR-8cb61529d165441297b86cbe1548728c
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
