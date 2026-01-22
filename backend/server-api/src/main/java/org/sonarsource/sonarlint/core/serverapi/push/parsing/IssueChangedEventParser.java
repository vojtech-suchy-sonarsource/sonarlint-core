/*
ACR-879c45cc1f054e2fad7563b2e7656c93
ACR-2e28a37c84ee4eb1a2a3ecfb07f962f6
ACR-91740c22ac394d0d95ddeedc3ee4d851
ACR-883540403b6045ee89246cf929afd54b
ACR-f71011c3deb4436aa42e036447bf32a3
ACR-3b9895c05d384268a0c455ea027e923c
ACR-5d2c2a91eedf403db6de37aaaf865fa6
ACR-7c3ddfcded5347248dfc6d5a174f8c4b
ACR-5aba3c5c54a041c9a9098995055d5a18
ACR-f235b2b03b9a438ea074f76ba1c2e013
ACR-22a20efc5da44394b1ac8434630c4afd
ACR-42be300daea54323b80f2e87f45ed6da
ACR-dbd72fc28ec5443282d3fdb9bb75063e
ACR-19bb99b0ff1d4fe88a5e92e960972fa9
ACR-098b80905b2b427ca558b248085ecfa3
ACR-88bf1b82fcea4ec792cf95d5c2cb8ce0
ACR-aefc163169d64e48bdafe6dddec7958a
 */
package org.sonarsource.sonarlint.core.serverapi.push.parsing;

import com.google.gson.Gson;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.ImpactSeverity;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.SoftwareQuality;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.serverapi.push.IssueChangedEvent;
import org.sonarsource.sonarlint.core.serverapi.push.parsing.common.ImpactPayload;

import static org.sonarsource.sonarlint.core.serverapi.util.ServerApiUtils.isBlank;

public class IssueChangedEventParser implements EventParser<IssueChangedEvent> {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final Gson gson = new Gson();

  @Override
  public Optional<IssueChangedEvent> parse(String jsonData) {
    var payload = gson.fromJson(jsonData, IssueChangedEventPayload.class);
    if (payload.isInvalid()) {
      LOG.error("Invalid payload for 'IssueChangedEvent' event: {}", jsonData);
      return Optional.empty();
    }
    return Optional.of(new IssueChangedEvent(
      payload.projectKey,
      payload.issues.stream()
        .map(issueChange ->
          new IssueChangedEvent.Issue(issueChange.issueKey, issueChange.branchName, adapt(issueChange.impacts))
        )
        .toList(),
      payload.userSeverity != null ? IssueSeverity.valueOf(payload.userSeverity) : null,
      payload.userType != null ? RuleType.valueOf(payload.userType) : null,
      payload.resolved));
  }

  public static Map<SoftwareQuality, ImpactSeverity> adapt(@Nullable ImpactPayload[] payloads) {
    if (payloads == null) {
      return Map.of();
    }
    return Arrays.stream(payloads)
      .collect(Collectors.toMap(
        payload -> SoftwareQuality.valueOf(payload.getSoftwareQuality()),
        payload -> ImpactSeverity.valueOf(payload.getSeverity())
      ));
  }

  private static class IssueChangedEventPayload {
    private String projectKey;
    private List<ChangedIssuePayload> issues;
    private String userSeverity;
    private String userType;
    private Boolean resolved;

    private boolean isInvalid() {
      return isBlank(projectKey) || isBlank(issues) || issues.stream().anyMatch(ChangedIssuePayload::isInvalid)
        || (isBlank(userSeverity) && isBlank(userType) && resolved == null);
    }

    private static class ChangedIssuePayload {
      private String issueKey;
      private String branchName;
      @Nullable
      private ImpactPayload[] impacts;

      private boolean isInvalid() {
        return isBlank(issueKey) || isBlank(branchName);
      }
    }
  }
}
