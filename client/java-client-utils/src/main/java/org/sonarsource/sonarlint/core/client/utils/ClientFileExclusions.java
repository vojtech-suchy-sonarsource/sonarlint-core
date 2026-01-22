/*
ACR-25d0d397c0ae475a808ffcf005806730
ACR-8a989a1fc0704ddc926524b621802066
ACR-ba2edda34fdc4eae9a5e0f58cf61694e
ACR-4a82d15ea8ae4da49408f67bf66da75c
ACR-254fa073932e455f9a86d9a438fc17c6
ACR-06e26e9e1e1e480fbe97ce3091315a03
ACR-af96755f9dac4c99875cae994d9e953e
ACR-4b823b6739704a3186bda865573a0ded
ACR-1d60955b13be42fe9e0d74b9e43032d6
ACR-c5cdcbea9c4346b4a2a98b8f01920d2e
ACR-4bb42f8a7c4543cb958f20df6868fec6
ACR-4c88b3e2a4224b3dbc99276f1e06ce6b
ACR-8b6a5a4913ad4f2c8763b5d9923374cf
ACR-dfafb5e18094456ea731afc8732dda12
ACR-7a7bec93fc804a1f96a7ce5f7d586171
ACR-e90377d1184a413f804194b04a6f3992
ACR-16d579476c5a4df396de04f730c253f8
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

/*ACR-b374cc80936545dcb9a413bc2929ce6a
ACR-bfa1383b3d8c4a1e813bb7ad988773db
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
        //ACR-3cec5514b0d94e44bb6abf4ff0282bc9
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
