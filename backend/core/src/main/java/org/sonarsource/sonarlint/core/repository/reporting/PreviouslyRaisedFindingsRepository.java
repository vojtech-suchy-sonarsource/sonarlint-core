/*
ACR-a74495cdd50141c0a24df483394d4edf
ACR-083d27b5a0e64c13a2c539681ed92c69
ACR-582d4d9fd036416a9ce8f7449555a1c2
ACR-f8367cbaabe241fab648f5925008b0a4
ACR-32132f88bbfc4eb4a6e88151cc1f138f
ACR-95c28d6c3b774f81810d30c082253b60
ACR-67dab259abfd4352ac56b2bbe860280c
ACR-fef740971b394d49a000b642c6f4010e
ACR-5c6d48acad6243e4a166a1b54fe2f132
ACR-ff1ce036f7d44437aee73a5888748a13
ACR-5eaee6e927c747d687d7b1835f151b3f
ACR-ac81bf1a0f414ce7b010ca56345ff9f1
ACR-38e31e3571de428a82659fc80a00f88e
ACR-20e97c41aff94b72a269a088522c5cb0
ACR-181e1d6e596644d8b05f52c6861e7083
ACR-bc4e147f2bbd48b5b763305ded77bbcf
ACR-7fe95ea45cac4de991f1e475bc180358
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
