/*
ACR-90dd3ab780164d64abd6475abf6f150b
ACR-f36ef2b203c642c2872a80ebf5ce6c1a
ACR-9672f12f64ed493eb8abd392a22ae3a4
ACR-42aba3a976c24ad5ab133e93eb511dac
ACR-29dc05090c5544a09f3017806a765f92
ACR-f0a01a98af174246a0fa42378667d091
ACR-dc8ef753080d474aa7a4000cabf07623
ACR-8234704702e44817a9336e0bc8e2aca1
ACR-af559992b5914aa188d7753fa3f5b26b
ACR-2af2c0e0c9644cd4baf19ff7f1e81aeb
ACR-be4bb61f2f054a1494346b587c1091b4
ACR-38f2745be71a4e7fa952748202e1d2bf
ACR-aace69a4c956444e9afdeb6e395f5dd8
ACR-14d17c8ee46647878ab0df8ce6a65ffd
ACR-024a257659e94f0b94409858009a3ad1
ACR-1e00794d5a684c78856773f1378ae060
ACR-83965132433c4d009372678db2820bf2
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
