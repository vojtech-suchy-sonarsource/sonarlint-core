/*
ACR-8fc840e2ff714d958fecde49070bae31
ACR-2aca903710524bbc9173d0ec2ee75d23
ACR-5253c8f786f349d7a5c50f6ce8fe3fbb
ACR-7eb4ced52aec433fbfcc85173f55e769
ACR-65c39188033f456582118bfae7a42bcd
ACR-634195cb587548ba941489b52026b3ea
ACR-9a9bc9fb8dbd4f1388a954a351046441
ACR-96a5e1ee4fc5458a8c39a767f90ce1b7
ACR-16ac5590c2ab4daf9c62146d91e78958
ACR-972f76b5fb11478f9e5ec6226988d901
ACR-e0d1d6ce17d845429165c522d049c2c2
ACR-1dd5b3f49c1444d4a3d8f86cba3e794c
ACR-3988f4e7dfc5417d9f06cc47c26a5c24
ACR-3d2fdd9f4db54b56a86051bb89787816
ACR-89b61b0ac3ec4ccb919ac037f5d7d2f9
ACR-c33dff797fcf4256bca2aa76dbbcc9f9
ACR-44588031a78f43fa9f35643daf799223
 */
package org.sonarsource.sonarlint.core.repository.reporting;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.sonarsource.sonarlint.core.rpc.protocol.client.hotspot.RaisedHotspotDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedFindingDto;
import org.sonarsource.sonarlint.core.rpc.protocol.client.issue.RaisedIssueDto;

public class PreviouslyRaisedFindingsRepository {
  private final Map<String, Map<URI, List<RaisedIssueDto>>> previouslyRaisedIssuesByScopeId = new ConcurrentHashMap<>();
  private final Map<String, Map<URI, List<RaisedHotspotDto>>> previouslyRaisedHotspotsByScopeId = new ConcurrentHashMap<>();

  public Map<URI, List<RaisedIssueDto>> replaceIssuesForFiles(String scopeId, Map<URI, List<RaisedIssueDto>> raisedIssues) {
    return addOrReplaceFindings(scopeId, raisedIssues, previouslyRaisedIssuesByScopeId);
  }

  public Map<URI, List<RaisedHotspotDto>> replaceHotspotsForFiles(String scopeId, Map<URI, List<RaisedHotspotDto>> raisedHotpots) {
    return addOrReplaceFindings(scopeId, raisedHotpots, previouslyRaisedHotspotsByScopeId);
  }

  private static <F extends RaisedFindingDto> Map<URI, List<F>> addOrReplaceFindings(String scopeId, Map<URI, List<F>> raisedFindings,
    Map<String, Map<URI, List<F>>> previouslyRaisedFindingsByScopeId) {
    var findingsPerFile = previouslyRaisedFindingsByScopeId.computeIfAbsent(scopeId, k -> new ConcurrentHashMap<>());
    findingsPerFile.putAll(raisedFindings);
    return findingsPerFile;
  }

  public Map<URI, List<RaisedIssueDto>> getRaisedIssuesForScope(String scopeId) {
    return previouslyRaisedIssuesByScopeId.getOrDefault(scopeId, Map.of());
  }

  public Map<URI, List<RaisedHotspotDto>> getRaisedHotspotsForScope(String scopeId) {
    return previouslyRaisedHotspotsByScopeId.getOrDefault(scopeId, Map.of());
  }

  public void resetFindingsCache(String scopeId, Set<URI> files) {
    resetCacheForFindings(scopeId, files, previouslyRaisedIssuesByScopeId);
    resetCacheForFindings(scopeId, files, previouslyRaisedHotspotsByScopeId);
  }

  private static <F extends RaisedFindingDto> void resetCacheForFindings(String scopeId, Set<URI> files, Map<String, Map<URI, List<F>>> cache) {
    Map<URI, List<F>> blankCache = files.stream().collect(Collectors.toMap(Function.identity(), e -> new ArrayList<>()));
    cache.compute(scopeId, (file, issues) -> blankCache);
  }

  public Optional<RaisedIssue> findRaisedIssueById(UUID issueId) {
    return previouslyRaisedIssuesByScopeId.values().stream()
      .flatMap(issuesByUri -> issuesByUri.entrySet().stream()
        .flatMap(entry -> entry.getValue().stream().filter(issue -> issue.getId().equals(issueId)).findFirst().map(issue -> new RaisedIssue(entry.getKey(), issue)).stream()))
      .findFirst();
  }
}
