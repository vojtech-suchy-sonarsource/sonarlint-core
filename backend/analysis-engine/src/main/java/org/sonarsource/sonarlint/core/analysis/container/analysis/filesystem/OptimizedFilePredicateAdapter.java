/*
ACR-a9429eb7d80243fa9eaa83b1e08211f1
ACR-35ba8cc2ddde4edb9cab312059c43ecd
ACR-20277c8fe4dd49399bf1378263ee3191
ACR-c1b140eb79ce412089312390af6ab10c
ACR-be7819e6556345448bd33030a20e45e0
ACR-337f21bef7094a488121239df7eed8ff
ACR-4adc4a85a31149f4a88ccc9cd0e992c4
ACR-7398b2deb5c04e6a8a5b55a178b3c4ee
ACR-1db6d9bf8fcf42a7acab74a039cbe1ad
ACR-18bc22d305b74fec81892168353b5cc2
ACR-8c0021e8ecc04f5f92778371130be14b
ACR-2b4d6b62ab6b432ebf50cd68e99b6ac6
ACR-cd581c787b3343068d81577df9797e50
ACR-ca054429ee08409b81baf7ec1813a0fa
ACR-a24a025fe5524ea5a0c08cf09fc9d0a8
ACR-efe8b423e7cc485b963ffcf09a521821
ACR-0dcee797589142da8ffdd20c123abb18
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.InputFile;

class OptimizedFilePredicateAdapter extends AbstractFilePredicate {

  private final FilePredicate unoptimizedPredicate;

  private OptimizedFilePredicateAdapter(FilePredicate unoptimizedPredicate) {
    this.unoptimizedPredicate = unoptimizedPredicate;
  }

  @Override
  public boolean apply(InputFile inputFile) {
    return unoptimizedPredicate.apply(inputFile);
  }

  public static OptimizedFilePredicate create(FilePredicate predicate) {
    if (predicate instanceof OptimizedFilePredicate optimizedFilePredicate) {
      return optimizedFilePredicate;
    } else {
      return new OptimizedFilePredicateAdapter(predicate);
    }
  }

}
