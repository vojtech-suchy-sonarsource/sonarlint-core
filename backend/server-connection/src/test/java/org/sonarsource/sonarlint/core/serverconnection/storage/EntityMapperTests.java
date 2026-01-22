/*
ACR-5410f7b602e34548826cba2af331248d
ACR-54497d6d445040aa8fd1d068b57e7bdc
ACR-7ec012c807d04ce68422d399df670fcb
ACR-3f37330d3be2488888c446108b520635
ACR-8d36329cd11a44e5b9421047148ba109
ACR-837c19b4cb9c48aea42ad4eb91d5d97f
ACR-c77fffca8ef84febaaa365ef97be7db4
ACR-054a8b78da2f4adaa08d0f5567726e97
ACR-d25a5887e6e5420eac2f9973c2c7f344
ACR-35ac0911e91f409580fb317e93ec9160
ACR-dbfb1e29701e400c949dab2586bbd827
ACR-f46533bda67c451a9d14fa978ff59c75
ACR-51ba3f98f344452ea9ea165b6ce186a4
ACR-1d2f8a72bce244ada6198bbc764baedf
ACR-0bbe94c42f7c4b968618296ea3ccb6af
ACR-9766fb9e10ee4fb3a63e829a2060d0b6
ACR-6f215f2936344f77ba4e180510a7fab9
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
