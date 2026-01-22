/*
ACR-d58fb2ecd8694d5486929cfdfce4dd93
ACR-c6e0e7a3c60248bd95ad486d58a0cab2
ACR-49c81f47fdeb4e3fb9ead849d6ceaeef
ACR-4715a00683a94955bd87ea05ee451bd0
ACR-1e13c27557b44fcebf335bcaed8efad2
ACR-f1d91c895c58410e96903253dc26ade9
ACR-74352f2409444e78a9742b737ca7838a
ACR-e335a6c58ecd4ec89072babfc1ac16a2
ACR-cd7cfee54ea1433bbbe9664ce5457f24
ACR-3bf4efc7d2c34145bc95b614c5118b45
ACR-4dc7f18a519944c38d8ef88d3fc93ebe
ACR-8d980633908442eaa5dc92151d687518
ACR-b11a27c08ae0489aa52ed5fc27d4f773
ACR-9707d4a33e014ff89f7af7587e93dece
ACR-9b912f2ce94e4102a3e04e27cab4ef41
ACR-e51b1e75d3134d4db47554d98f12d1fc
ACR-b505d940db3747fbbcb357d3370b9113
 */
package org.sonarsource.sonarlint.core.serverconnection.storage;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import org.jooq.JSON;
import org.junit.jupiter.api.Test;
import org.sonarsource.sonarlint.core.commons.ImpactSeverity;
import org.sonarsource.sonarlint.core.commons.SoftwareQuality;
import org.sonarsource.sonarlint.core.commons.api.TextRangeWithHash;
import org.sonarsource.sonarlint.core.serverconnection.issues.ServerTaintIssue;

import static org.assertj.core.api.Assertions.assertThat;

class EntityMapperTests {

  private final EntityMapper underTest = new EntityMapper();

  @Test
  void should_serialize_issue_impacts() {
    var impacts = new EnumMap<SoftwareQuality, ImpactSeverity>(SoftwareQuality.class);
    impacts.put(SoftwareQuality.MAINTAINABILITY, ImpactSeverity.HIGH);
    impacts.put(SoftwareQuality.SECURITY, ImpactSeverity.LOW);

    var json = underTest.serializeImpacts(impacts);

    assertThat(json.data()).isEqualTo("{\"MAINTAINABILITY\":\"HIGH\",\"SECURITY\":\"LOW\"}");
    var impactsDeserialized = underTest.deserializeImpacts(json);

    assertThat(impactsDeserialized).isEqualTo(impacts);
  }

  @Test
  void should_serialize_issue_flows() {
    var flows = new ArrayList<ServerTaintIssue.Flow>();
    var path = Path.of("file/path");
    var stringPath = path.toString().replace("\\", "\\\\");
    flows.add(new ServerTaintIssue.Flow(List.of(
      new ServerTaintIssue.ServerIssueLocation(path,
        new TextRangeWithHash(1, 2, 3, 4, "hash1"), "Message 1"),
      new ServerTaintIssue.ServerIssueLocation(path,
        new TextRangeWithHash(5, 6, 7, 8, "hash2"), "Message 2"))));
    flows.add(new ServerTaintIssue.Flow(List.of(
      new ServerTaintIssue.ServerIssueLocation(path,
        new TextRangeWithHash(1, 2, 3, 4, "hash1"), "Message 1"))));
    var taint = new ServerTaintIssue(null, null, true, null, null, null, null,
      null, null, null, null, null, null, null, flows);

    var json = underTest.serializeFlows(taint.getFlows());

    assertThat(json.data())
      .isEqualTo("[{\"locations\":[{\"filePath\":\"" + stringPath + "\"," +
        "\"textRange\":{\"startLine\":1,\"startLineOffset\":2,\"endLine\":3,\"endLineOffset\":4,\"hash\":\"hash1\"},\"message\":\"Message 1\"}," +
        "{\"filePath\":\"" + stringPath + "\",\"textRange\":{\"startLine\":5,\"startLineOffset\":6,\"endLine\":7,\"endLineOffset\":8,\"hash\":\"hash2\"}," +
        "\"message\":\"Message 2\"}]},{\"locations\":[{\"filePath\":\"" + stringPath + "\"," +
        "\"textRange\":{\"startLine\":1,\"startLineOffset\":2,\"endLine\":3,\"endLineOffset\":4,\"hash\":\"hash1\"},\"message\":\"Message 1\"}]}]");
  }

  @Test
  void should_deserialize_taint_flows() {
    var path = Path.of("file/path");
    var stringPath = path.toString().replace("\\", "\\\\");

    var flows = underTest.deserializeTaintFlows(JSON.valueOf("[{\"locations\":[{\"filePath\":\"" + stringPath + "\"," +
      "\"textRange\":{\"startLine\":1,\"startLineOffset\":2,\"endLine\":3,\"endLineOffset\":4,\"hash\":\"hash1\"},\"message\":\"Message 1\"}," +
      "{\"filePath\":\"" + stringPath + "\",\"textRange\":{\"startLine\":5,\"startLineOffset\":6,\"endLine\":7,\"endLineOffset\":8,\"hash\":\"hash2\"}," +
      "\"message\":\"Message 2\"}]},{\"locations\":[{\"filePath\":\"" + stringPath + "\"," +
      "\"textRange\":{\"startLine\":1,\"startLineOffset\":2,\"endLine\":3,\"endLineOffset\":4,\"hash\":\"hash1\"},\"message\":\"Message 1\"}]}]"));

    assertThat(flows).isEqualTo(List.of(
      new ServerTaintIssue.Flow(List.of(
        new ServerTaintIssue.ServerIssueLocation(path,
          new TextRangeWithHash(1, 2, 3, 4, "hash1"), "Message 1"),
        new ServerTaintIssue.ServerIssueLocation(path,
          new TextRangeWithHash(5, 6, 7, 8, "hash2"), "Message 2"))),
      new ServerTaintIssue.Flow(List.of(
        new ServerTaintIssue.ServerIssueLocation(path,
          new TextRangeWithHash(1, 2, 3, 4, "hash1"), "Message 1")))));
  }

}
