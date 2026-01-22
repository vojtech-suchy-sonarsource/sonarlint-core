/*
ACR-108d52e675eb4432b520770809c61177
ACR-82a40c2237df4773a3f47a88afbef8e4
ACR-cee0202f760d4e21b462d59067ff78bf
ACR-9320a28ced6549c4a077aebb30056f05
ACR-bc1e0e3fda2b49fdb3fd0dfa301a1099
ACR-2b1437fef9c54260a8b035c7412b8ebc
ACR-7ebffb250e6a4be98550c2caf32c47f4
ACR-01c6f63ed78247a7abc1f487af5e8d7a
ACR-de639074806b40349cb9af8149d40f11
ACR-e8a024767f464bb39916925595897c75
ACR-15fdfe1f1e00466491f2fd895372fa1b
ACR-9e1f5ac904bc414bad12b9b341edb07f
ACR-d243fa8082944e889003af59a5ae2d1a
ACR-f09dbe9a16f84f9398c81d09b926d6f7
ACR-17e8aa11eb9a425ca2759ed97abb5fb3
ACR-eeed2c9167ba4c5c80f9983bce7319e3
ACR-85d3af003c55411abd43c094fe690548
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
