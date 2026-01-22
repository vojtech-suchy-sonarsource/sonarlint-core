/*
ACR-c855adcd4d954319b2a87e2618e2037b
ACR-871d41aa6d3a44f6a0349e192169b765
ACR-b93eb8eac64343698ac586f542b3af8f
ACR-28e028ce800a45ceb98a54a181492e13
ACR-74e0914a8b5d44eda230b6410c1054cd
ACR-fc83049eb02648a89103a48c7f81cb8d
ACR-2586f239e266438390b190394a878658
ACR-597f6c3bc30e4d5d84512a28bacd2c4d
ACR-0bdf55ca614e4f20af9e8f8287ba38a1
ACR-64d04ccf94fc4c8a9474538e4a4d3c89
ACR-9a19267b85d14a6599f9df5e5e0f6356
ACR-821790648b8e4c21bdffcfd2e4feba78
ACR-5465e1d49c71447c97c16048a8dedabd
ACR-d684ac54ba5642f18f6cf30dddd61ab9
ACR-6bf231f463d044d0b34b5140fa3230c5
ACR-03a510be673d4509a0103494be22e0e7
ACR-fd62cc2193c44c21911a02147e06c0d2
 */
package org.sonarsource.sonarlint.core.serverconnection.issues;

import java.util.ArrayList;
import java.util.List;
import org.sonarsource.sonarlint.core.commons.KnownFinding;

public record Findings(List<KnownFinding> issues, List<KnownFinding> hotspots) {
  public Findings mergeWith(Findings other) {
    var mergedIssues = new ArrayList<>(issues);
    mergedIssues.addAll(other.issues);
    var mergedHotspots = new ArrayList<KnownFinding>(hotspots);
    mergedHotspots.addAll(other.hotspots);
    return new Findings(mergedIssues, mergedHotspots);
  }
}
