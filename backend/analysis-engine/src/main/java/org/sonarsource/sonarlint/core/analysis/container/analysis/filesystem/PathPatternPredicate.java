/*
ACR-0eb95f3652034e5b9ced6ffda1ec27d6
ACR-4f30e11ad5474ba79b26c93957f963c2
ACR-a62e55ea7a3948459f8dd4fd76cfa087
ACR-c257b25d20f546e383641928a413937c
ACR-c522ac862b9e42498d53a3b8b8c2ef40
ACR-3f9eb9f108fe4179aa77bdc102cf0376
ACR-e31e441e8beb46389703e07dd70365f9
ACR-c19a856b05104c6688f285f35927bb90
ACR-358a158b20dc432ea353aaec02f09ecf
ACR-88ec705c33fd492ca147571f56afe1b6
ACR-a6489311d24746db90f5ed3e24f9189b
ACR-424bcb1defcb436f94e2b051de0bebbc
ACR-40dc4c08e9fb43ae9e7118565f9cf1d5
ACR-0fc6ad53fe484a4b85d28986fd977133
ACR-aac9a92dd6864a6fbebb547fe2b1fe7b
ACR-63d85b787240477cbbf2c5306dd5ea56
ACR-50913039e42e4e4a92dd255697da62ed
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import org.sonar.api.batch.fs.InputFile;
import org.sonarsource.sonarlint.core.analysis.container.analysis.SonarLintPathPattern;

/*ACR-7bb36c45343948f1b7e03a321f0a6f38
ACR-96f03c7e6626474bacddfc9c275bd3a6
 */
class PathPatternPredicate extends AbstractFilePredicate {

  private final SonarLintPathPattern pattern;

  PathPatternPredicate(SonarLintPathPattern pattern) {
    this.pattern = pattern;
  }

  @Override
  public boolean apply(InputFile f) {
    return pattern.match(f);
  }

}
