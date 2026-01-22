/*
ACR-626ee8048cb140fbbeebcc8392b7bff7
ACR-99426c56007a451384618a68137331f7
ACR-76c976635b894defb9b440dc1b0608ea
ACR-b84f0ff65b9640f59abd3d13afa57d89
ACR-8cb9acdac78e4d97abb104c66e48e314
ACR-87d81c1c419f4758a797779b36af0d2f
ACR-005c76f8faeb480ab3cfae5eab4f1b18
ACR-4afa7a456a5e43ff9bcbf7be1cbf2cbd
ACR-ee3a816eebcb4a7a812c90362e4bd035
ACR-9c7627af0fe347a5911d28eb8f67488e
ACR-0275322e37f144afb6214d39ec216b9a
ACR-db15d193281c435f913dc8ac39085f37
ACR-0fb991b9b1c7484c850708e948072e44
ACR-2b43140cbf5f43f99a173ef15744dc55
ACR-f16fb2be27cb43a2ac4666f73f915b3c
ACR-2dbc80551e6b4ffe8c407d1e50375913
ACR-e929efb59d744889b482d0057f4baa1c
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.util.ArrayList;
import java.util.Collection;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.InputFile;

/*ACR-4326c77b51fa4d299b86f035e5bd30d5
ACR-e2c6b930d8074c95a3c5efd1a854423a
 */
class OrPredicate extends AbstractFilePredicate {

  private final Collection<FilePredicate> predicates = new ArrayList<>();

  private OrPredicate() {
  }

  public static FilePredicate create(Collection<FilePredicate> predicates) {
    if (predicates.isEmpty()) {
      return TruePredicate.TRUE;
    }
    var result = new OrPredicate();
    for (FilePredicate filePredicate : predicates) {
      if (filePredicate == TruePredicate.TRUE) {
        return TruePredicate.TRUE;
      } else if (filePredicate == FalsePredicate.FALSE) {
        continue;
      } else if (filePredicate instanceof OrPredicate orPredicate) {
        result.predicates.addAll(orPredicate.predicates);
      } else {
        result.predicates.add(filePredicate);
      }
    }
    return result;
  }

  @Override
  public boolean apply(InputFile f) {
    for (FilePredicate predicate : predicates) {
      if (predicate.apply(f)) {
        return true;
      }
    }
    return false;
  }

  Collection<FilePredicate> predicates() {
    return predicates;
  }

}
