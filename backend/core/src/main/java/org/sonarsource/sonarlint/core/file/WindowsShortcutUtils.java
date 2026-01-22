/*
ACR-392803c469274afa8c85328b80fcbe8e
ACR-7ee1b1d63e864269914dca2691dfa83d
ACR-eeeec0fed8144d5cb8370a21a17b8762
ACR-7b5c8799359e44589955e196c5b1e86a
ACR-4781111475a840ebb50d11073acb295b
ACR-464c3f43afb544d48c86b4a7d886f7f6
ACR-c4c0be82332f4fee8d17c52c3bc3e3e3
ACR-06766bef4479467580fc599967a8c39e
ACR-b807365b7e0544e4ba63e0f04860b3bf
ACR-c342a58f8e8c45d3b5fbcd0e072a6a99
ACR-34b7f7e8d4cc417390155e7ee4879a52
ACR-299bec1cae714e00aa79f139658863e2
ACR-1bae6f150a644ddc9bc8c3b051be4223
ACR-17b4ca98449b45d0a4224801605e5d46
ACR-994a6c5d85cf43c4a285e4dd36c90801
ACR-a544a094ccf24d03b9ea71c64227b7f2
ACR-d5774b1d858d4c2d8984637f0264c2a6
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
  //ACR-b675ccf8998243c9b3161d631a898d3d
  //ACR-ede11abf82464e1096a2ad5c35ec48a3
  private static final byte[] WINDOWS_SHORTCUT_MAGIC_NUMBER = new byte[] {0, 0, 0, 76};
  
  private WindowsShortcutUtils() {
    //ACR-04d52b16160843349c8b29adf67d72fb
  }

  /*ACR-d267e742e1c54850b0a1567c833057c6
ACR-fa3bb0ed048b461685aa113c2351054b
ACR-46d0b761a5094858a05f4d24d6b1fc1a
ACR-e9024d78657b41b39bc9b3f201eab21f
   */
  public static boolean isWindowsShortcut(URI uri) {
    //ACR-1026697307f645e8b6fec97d7c2927fd
    //ACR-da653262052c45beae11baac6d9de957
    //ACR-6c60beb75fff49bd8dfe64b0291944ec
    if (!uri.toString().contains(".lnk")) {
      return false;
    }
    
    //ACR-6971484e80744ba0a69422598c20101a
    //ACR-ba1601f6c5204a35938ead088810aa3b
    var magicNumber = new byte[4];
    try (var is = new FileInputStream(new File(uri))) {
      if (is.read(magicNumber) != magicNumber.length) {
        //ACR-9943e855eee9480d95aa5e3dd392aa4c
        //ACR-a366a419707d4495ba84fd8c23ad0e66
        //ACR-4b21a731f6e24c56a9bb8996c117de64
        return false;
      }
      
      //ACR-db93111778f8411fa6f21f7fb4e54971
      if (Arrays.equals(WINDOWS_SHORTCUT_MAGIC_NUMBER, magicNumber)) {
        return true;
      }

      //ACR-d12734f15b6142248c1892d9f4d48d86
      reverse(magicNumber);
      return Arrays.equals(WINDOWS_SHORTCUT_MAGIC_NUMBER, magicNumber);
    } catch (IOException err) {
      SonarLintLogger.get().debug("Cannot check whether '" + uri + "' is a Windows shortcut, assuming it is not.");
    }
    
    return false;
  }
}
