/*
ACR-a7091c4045444bd3af4a6ce283acb5f4
ACR-0f34bf9c79124953a8c499e67d61857f
ACR-07357643b5a743d5894da99590abc7df
ACR-45a30ba716e64715b94c14a89aa88e17
ACR-92a19fcc0e4f41e19bca3076b236975d
ACR-417e3375f6744d148f6e3f46983baaa7
ACR-ef63fcb26f1e41bc8929c2972e6402b4
ACR-2723e68a490f4d61a2d18ee4bd48e9af
ACR-154ae296dbd94dd48ba49397ab85361a
ACR-08f366d6145f4512bcd493342e601433
ACR-9fb1a002452c4c16a1335e533a518e2b
ACR-15b7b8e9e28047a3a9d6dd15e0e9a7c7
ACR-fa75cb03368b48aba2c0beb14ba6aa50
ACR-5df3677c4e954fd79739b5e345b61ebb
ACR-7433c557ddda4ee18924669d99b39348
ACR-79586e8f41564c47881b9e8387545f7f
ACR-b397f0f4cd074b51ab44724242d44a2e
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
