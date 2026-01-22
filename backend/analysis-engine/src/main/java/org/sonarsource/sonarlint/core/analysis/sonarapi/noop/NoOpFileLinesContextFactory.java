/*
ACR-6543a3b8264548e59c0df680c6ad3016
ACR-b9743b5c1bc7429ca916b33d87b445ea
ACR-76640ebc6a3844cc893435e7d72d796f
ACR-d04fab026d1843bcb1f5b23bfb332bc6
ACR-550a325203834709b30d0ace2417f72e
ACR-3bbb089476354d50b7cf29a78f9cde56
ACR-ccf86455fb064b3ea0acf228fb105edc
ACR-841240a59c854dd7980c1841ebcdf5f6
ACR-d40c893d4a4e4ec4b3182193557de44a
ACR-dcccaf2dd9bf47b182c8122212ae9c42
ACR-e2fe06db82bd4cf893d5237c20fe35b7
ACR-89aa2d4fc1514a94bea354f1507b251f
ACR-a813536aefd447db9a4174abf9c3e2ff
ACR-a0b44414a2bd4a1292ad77449a9feb45
ACR-04da28a40f634f4d885ae5b24dd5a1c8
ACR-cdc66a817dc54a88af27fc820f13e60a
ACR-6799939d231a40f288592c3c927134fe
 */
package org.sonarsource.sonarlint.core.analysis.sonarapi.noop;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.measures.FileLinesContext;
import org.sonar.api.measures.FileLinesContextFactory;

public class NoOpFileLinesContextFactory implements FileLinesContextFactory {

  @Override
  public FileLinesContext createFor(InputFile inputFile) {
    return new NoOpFileLinesContext();
  }

}
