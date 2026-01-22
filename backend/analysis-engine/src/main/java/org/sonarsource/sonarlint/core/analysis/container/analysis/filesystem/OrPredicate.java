/*
ACR-36f23a29e7714f8686cfae099fbe7f7c
ACR-c2f7433b1c8b47c0bffefb42bb3d5bae
ACR-a8d8d6793dee4607aacbc1b236aec980
ACR-5901d18dfba749548054dd8fdf1a5b64
ACR-cef8353ac86245be95de3359121e01d4
ACR-db3bab9f8ca64320b87c8001d2575a0a
ACR-de532b8a031240919d1b1d1ada12936b
ACR-77c96b1c29e94e05bc12741476fd8c22
ACR-b357aecca416404296263826d512926d
ACR-7a158d9c312545a7a0e983c618ff34ee
ACR-1f827dc5668c495ba8821675baa70af8
ACR-8158037e077c4ca9b20b6f5450d9fa29
ACR-accd08346d42459fb5e11ce73f804228
ACR-3a4c8d2e16654c6db28a3ee9356b6a41
ACR-68e0c1f26e2440bd93e76c1a21ec64e9
ACR-cd35e8be68af45148859d3abadfda434
ACR-3ac8dd7da31c4c5a8df5ba9ff548072c
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import java.util.ArrayList;
import java.util.Collection;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.InputFile;

/*ACR-b0d03a5da908447da87fb1fef40a5817
ACR-ca0d158b0e26459cadcc0f0738a86412
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
