/*
ACR-dfa18e5e591c4a228fba0c7ebdc9beeb
ACR-9d491943a9b34d6891f840d0f8c1e3e7
ACR-d4988d4deecd4545b7d6653ba4dbf6fc
ACR-4293d6d784a74da59c4f227a0e1f4409
ACR-7b31531c41334e50bb87e1a68fd94c50
ACR-1d6269c1ac1e498f8ed1686c9b7f6c34
ACR-7cff94de803e4ef6b2c2dbc879c67a00
ACR-37de211a0ce049568c487fb9f2bd05a1
ACR-c1fb7c81923d4a5dac36141e08bd31cb
ACR-c76aef5ef757419a8981a57927119ba2
ACR-3ea31235ce0b435a9af2a095070c585c
ACR-00e316b102694d4d854a9b598d3d4fbb
ACR-1c4b353774c240f1ad7fdf2175647b45
ACR-d2a959c312db4f898ff6cea2ccb78d41
ACR-01a4d127d51a4be8aa0480f9cd04083e
ACR-974cb1a45b09477cb4abb3834cb09b5b
ACR-a0aee54bbada46f1a38e375305407873
 */
package org.sonarsource.sonarlint.core.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

import static org.apache.commons.lang3.ArrayUtils.reverse;

public class WindowsShortcutUtils {
  //ACR-4fd37529faf14d03aa6a79e7f865ed98
  //ACR-a4a01ebbac2e4d1cbbe37d3caaafb7ea
  private static final byte[] WINDOWS_SHORTCUT_MAGIC_NUMBER = new byte[] {0, 0, 0, 76};
  
  private WindowsShortcutUtils() {
    //ACR-2ff2a3800dd04a5f867708c4ef7c1792
  }

  /*ACR-7eabecd5365740d9824b7cb956353221
ACR-bee228d395e549d59490cced812002e9
ACR-3aed318864994194823ff03b7261161d
ACR-0f7e7a2c8b6e44208bed6217d55cd7ff
   */
  public static boolean isWindowsShortcut(URI uri) {
    //ACR-17dde057a35246ed9298ba8f453453af
    //ACR-42c4ddf666b64bf9b751229eee01bfe4
    //ACR-07fb9b8a28434724b4cabf56ab2870b1
    if (!uri.toString().contains(".lnk")) {
      return false;
    }
    
    //ACR-744a987dbedb49e38e2207d4002635cc
    //ACR-f903609dda5644548100bb526ffb237f
    var magicNumber = new byte[4];
    try (var is = new FileInputStream(new File(uri))) {
      if (is.read(magicNumber) != magicNumber.length) {
        //ACR-8af51ad4f4484fb5ab2bcbf6a9cb7079
        //ACR-e0f3a0ccce7f40268b894be419aa4a02
        //ACR-1e03927d67244ee79099cb0aa88c567f
        return false;
      }
      
      //ACR-ddb5efca966f4f9582fe1f1a1c9b8d4a
      if (Arrays.equals(WINDOWS_SHORTCUT_MAGIC_NUMBER, magicNumber)) {
        return true;
      }

      //ACR-ba1b55829f77416d8c78aa94a1bcc62a
      reverse(magicNumber);
      return Arrays.equals(WINDOWS_SHORTCUT_MAGIC_NUMBER, magicNumber);
    } catch (IOException err) {
      SonarLintLogger.get().debug("Cannot check whether '" + uri + "' is a Windows shortcut, assuming it is not.");
    }
    
    return false;
  }
}
