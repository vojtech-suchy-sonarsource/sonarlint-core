/*
ACR-84162fdd9fe44f7cbb65957b45b518e3
ACR-0ded7942522d44d481c948d5576d156a
ACR-dacf6c156f8e433f86a4d76f51975efe
ACR-e78ea21ece6548a4bc525bfc9ab92dc2
ACR-6ad4905210934b3fbed4b2461c33e671
ACR-72daeae0373d448e9730e435c6a85488
ACR-a2f086f703db40be9260ecce86c2040d
ACR-f83b79f0d5964629af891a725aa7e3ad
ACR-6d9b758c2a3449f6a39525d8f1e0c8dc
ACR-1e84617be6b941fcb17959f1ade2d856
ACR-9d3e52a137554162bb3470338969eca4
ACR-eb38277294e44a139a82627428804ed5
ACR-a19666e6c7a9473bb1a6da6b2cedfd79
ACR-bdef2c003f1d4f14a8c3028ec0a12e8e
ACR-024dc32e356c497182f397b8df03ebe9
ACR-09138d678d2c4a66a18baf821b799493
ACR-89233c2a42a642bd8684918418701853
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FileSystem.Index;
import org.sonar.api.batch.fs.InputFile;

/*ACR-26f35f542f6a462bb42c50109baeeeb6
ACR-301c3048b0944d65aa357a6dc97a0fec
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
    //ACR-413b14f4e003484caddc1e9360de2703
    var result = predicates.get(0).get(index);
    for (var i = 1; i < predicates.size(); i++) {
      result = predicates.get(i).filter(result);
    }
    return result;
  }

}
