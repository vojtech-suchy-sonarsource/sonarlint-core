/*
ACR-f4be0582ca9a4659bb3435bfbd5b1da9
ACR-a505233775ee4985b459b7d4b465601b
ACR-6491dd2887844d82bab87a382f42078f
ACR-0d12fcdf2b25430ebc586b7197a1faf7
ACR-342fb90d1931412a8bbc065c44243813
ACR-7b4b13d9efa349af8b21b3ec5620d54b
ACR-e4392f42b83745118d455689d9255ed1
ACR-701091ca23b54dea921b7b8d15492468
ACR-fef2614517ae472ab5e4db52ac59a319
ACR-dfc4bc5c33774d56afdcc66b9890484d
ACR-e0044688b6054f3a9719c5bf41f050a5
ACR-3a35cb23726b4af79584e2471516786f
ACR-247518fc5ad7462ab31bbe69accf0653
ACR-01ba291f6a4842b198f0cecb24bb3492
ACR-dd30d906062d4444aa526802ea65ceac
ACR-1bcb3731f4dc42c7a0d1d64747d0c07d
ACR-26298f30217c4ae49e2b20743ea8e6f6
 */
package org.sonarsource.sonarlint.core.tracking;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.sonarsource.sonarlint.core.commons.LocalOnlyIssue;

public class LocalOnlyIssueRepository {
  private final Map<Path, List<LocalOnlyIssue>> localOnlyIssuesByRelativePath = new ConcurrentHashMap<>();

  public void save(Path serverRelativePath, List<LocalOnlyIssue> localOnlyIssues) {
    localOnlyIssuesByRelativePath.put(serverRelativePath, localOnlyIssues);
  }

  public Optional<LocalOnlyIssue> findByKey(UUID localOnlyIssueKey) {
    return localOnlyIssuesByRelativePath.values().stream().flatMap(List::stream).filter(issue -> issue.getId().equals(localOnlyIssueKey)).findFirst();
  }
}
