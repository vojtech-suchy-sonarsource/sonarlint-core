/*
ACR-6fd3f7cf3a9045a5941510172d6520f5
ACR-2411743b52e741c997cc26ff5fc51b79
ACR-06bca40cc2334d84beb9d104f750175d
ACR-86d250b17e3746f0be19a35bf959478a
ACR-31e67d62f00c4ae6b0b980a1b4a33b36
ACR-e3433a89c35a45e6ac26ca962bf5a4d4
ACR-d75b4e0ea2064756bf00e54ec31d46e9
ACR-c65de07f247149e183b4f8d89cdbb111
ACR-aa7379ee05224e00b076974c01c23e7a
ACR-f2374389cbe34287b9c350c707c71b06
ACR-3273ec5eeffb41c7b413fec03fa89ccc
ACR-a4bd40b91b50478b8cc80f8ad98f0a80
ACR-25e5137f1f21487382113f3ae7f05d80
ACR-440c07d69f284db8ae565a148c541fcc
ACR-b0b69de2b3a5469ba84e1e9b63aeffd0
ACR-861bd69b764648c5aba8f8f698351d0a
ACR-f70d5d9d1fc04e1ea750911166e29a46
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi.noop;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.code.NewSignificantCode;

public class NoOpNewSignificantCode implements NewSignificantCode {

  @Override
  public void save() {
    //ACR-ce56912050fc427384071491b7f85b70
  }

  @Override
  public NewSignificantCode onFile(InputFile file) {
    //ACR-baf1ab964d694cf48e276aa2fc683f57
    return this;
  }

  @Override
  public NewSignificantCode addRange(TextRange range) {
    //ACR-3bb32ecc34a146ecb85c45df664bd7c2
    return this;
  }
}
