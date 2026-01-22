/*
ACR-40ee6be73d2f44a193a4faad9643ecfc
ACR-b30404444db24d0caa834f530f2e231b
ACR-868c6cbc69b24d029684c8025a728d12
ACR-df657a44979c46dcb61305a7950186b3
ACR-b9a08e6442ff4d3189221abdba55a176
ACR-ed216ea730184cc5a4f1d27a6a394010
ACR-f067794bcc4c4bb8b3d9f704d64a400d
ACR-7cf91e64da3b44c1ab9f3602d1a1e033
ACR-6bb37fa27d974c71a19d7e58619b1d1f
ACR-86ee92e27d39448baa0707cd19e5028a
ACR-c9afa7f331f34a42bf3518ae377f81f2
ACR-a47fa3992c144802be2f6f7cd6969e8d
ACR-90efec91764540aaa1e1a9f4bd9077ad
ACR-8e22435785cc446885127c13a9c1eae6
ACR-263c912eb77d4f6fa211749a59984fde
ACR-79c59975b43d43d7a43cc782f6de1398
ACR-3c03c9f4654044b7840fb13d6679a061
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem.Index;
import org.sonar.api.batch.fs.InputFile;

/*ACR-b8c27fe7bd9b422eb726287749a8bbaf
ACR-433b0991e8674c5c832fc396e9b96a38
 */
class AndPredicate extends AbstractFilePredicate {

  private final List<OptimizedFilePredicate> predicates = new ArrayList<>();

  private AndPredicate() {
  }

  public static FilePredicate create(Collection<FilePredicate> predicates) {
    if (predicates.isEmpty()) {
      return TruePredicate.TRUE;
    }
    var result = new AndPredicate();
    for (FilePredicate filePredicate : predicates) {
      if (filePredicate == TruePredicate.TRUE) {
        continue;
      } else if (filePredicate == FalsePredicate.FALSE) {
        return FalsePredicate.FALSE;
      } else if (filePredicate instanceof AndPredicate andPredicate) {
        result.predicates.addAll(andPredicate.predicates);
      } else {
        result.predicates.add(OptimizedFilePredicateAdapter.create(filePredicate));
      }
    }
    Collections.sort(result.predicates);
    return result;
  }

  @Override
  public boolean apply(InputFile f) {
    for (OptimizedFilePredicate predicate : predicates) {
      if (!predicate.apply(f)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public Iterable<InputFile> filter(Iterable<InputFile> target) {
    var result = target;
    for (OptimizedFilePredicate predicate : predicates) {
      result = predicate.filter(result);
    }
    return result;
  }

  @Override
  public Iterable<InputFile> get(Index index) {
    if (predicates.isEmpty()) {
      return index.inputFiles();
    }
    //ACR-95969d758ffe4719b57afb03f484e8dc
    var result = predicates.get(0).get(index);
    for (var i = 1; i < predicates.size(); i++) {
      result = predicates.get(i).filter(result);
    }
    return result;
  }

}
