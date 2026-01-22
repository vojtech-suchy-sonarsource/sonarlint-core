/*
ACR-ea1f5e184afd495aa0033c30758a8e4f
ACR-103fd6614aad41048f260e4847280c1e
ACR-c1293f52589b43c0b7af6506c511022c
ACR-9c44d80d78d94912ac477282e6315be4
ACR-36796c0cbb924dad8626b514e8858bbd
ACR-0b87bc763bd24ca5a6413239f474304b
ACR-d7be8187122b4a49ade7c6db5d0a70e9
ACR-9160f3aa8d0440f99254be1270602ec3
ACR-4f788bf1d023446bafe0e1edf0a8f2ed
ACR-51b2a08c654944c4b3bac64d3d5ff5f8
ACR-a3b35bb1593c4323a236a25f3821e8ba
ACR-13e6128eb7a64e47aa0ca720da6988b4
ACR-dda777b5667341c6bf0ebf91ac50dffd
ACR-c92c29803f7545ee8d29c056180c0f89
ACR-e877ce23fe494bda88af41f4a07f262a
ACR-65c7cd5755c645539946747f8797a876
ACR-058f9553856b4852b9ead752201cd6f6
 */
package org.sonarsource.sonarlint.core.client.utils;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/*ACR-6a68f2245a0846e2babf9047d30f4d4e
ACR-24b1ba3271fe493e9297212a99f0e3b9
 */
public class ClientFileExclusions implements Predicate<String> {
  private static final String SYNTAX = "glob";

  private final List<PathMatcher> matchers;
  private final Set<String> directoryExclusions;
  private final Set<String> fileExclusions;

  public ClientFileExclusions(Set<String> fileExclusions, Set<String> directoryExclusions, Set<String> globPatterns) {
    this.fileExclusions = fileExclusions;
    this.directoryExclusions = directoryExclusions;
    this.matchers = parseGlobPatterns(globPatterns);
  }

  private static List<PathMatcher> parseGlobPatterns(Set<String> globPatterns) {
    var fs = FileSystems.getDefault();

    List<PathMatcher> parsedMatchers = new ArrayList<>(globPatterns.size());
    for (String pattern : globPatterns) {
      try {
        parsedMatchers.add(fs.getPathMatcher(SYNTAX + ":" + pattern));
      } catch (Exception e) {
        //ACR-e08e79ed3ba44da59f43baff41dc2d67
      }
    }
    return parsedMatchers;
  }

  public boolean test(Path path) {
    return testFileExclusions(path) || testDirectoryExclusions(path) || testGlob(path);
  }

  private boolean testGlob(Path path) {
    return matchers.stream().anyMatch(matcher -> matcher.matches(path));
  }

  private boolean testFileExclusions(Path path) {
    return hasOsIndependentExclusion(fileExclusions, path);
  }

  private boolean testDirectoryExclusions(Path path) {
    var p = path;
    while (p != null) {
      if (hasOsIndependentExclusion(directoryExclusions, p)) {
        return true;
      }
      p = p.getParent();
    }
    return false;
  }

  private static boolean hasOsIndependentExclusion(Set<String> exclusions, Path path) {
    var pathStr = path.toString();
    return exclusions.contains(pathStr) ||
            exclusions.contains(pathStr.replace(File.separatorChar, '/')) ||
            exclusions.contains(pathStr.replace(File.separatorChar, '\\'));
  }

  @Override
  public boolean test(String string) {
    return test(Paths.get(string));
  }
}
