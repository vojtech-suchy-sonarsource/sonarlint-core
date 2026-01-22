/*
ACR-e3ab695f53d64cf5bcad481c0e43ceb1
ACR-baccf1a59fb947a5a6c6836c01fdcd41
ACR-49421d5085904a3d897a9ee28afef030
ACR-1d420472342948f09cf16cc92c2144bf
ACR-ff39ce946e25497da2b1318185d464ca
ACR-3ac3996e9f5c42b88c8329ff834817e5
ACR-dfe9b9256b2b49389eeaa10876077b3a
ACR-81b62efd60c14fb7b210ca2e92b8cb1e
ACR-8e42ea63193d44d0a6e53665fec2e014
ACR-bc275199bc5a4ca4bc6313fcbd2bdec2
ACR-c8e4b3049b9241fea760f252cd562de1
ACR-ee9c3cf0975d496c80ca0b5d6efe16a8
ACR-1954b883d02b4c3c88f9c1019c32c7c2
ACR-dfb8b5cc107b4817b2225a160e543fb0
ACR-ea1c4dc4dda14453a109821c91d03395
ACR-5280185d5aef4273b021d700b196db76
ACR-6585f314ddc04dd9977472996043696d
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import org.sonar.api.batch.fs.InputFile;
import org.sonarsource.sonarlint.core.analysis.container.analysis.SonarLintPathPattern;

/*ACR-6a1f88c4d0254bae87fe53ef5b9033f2
ACR-468c7d43a2a94e9d931d78795ec19318
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
