/*
ACR-1f3973d8a56d49d981f1ec3e71a57599
ACR-32d13c9a513242d2b5d13d8fd712d787
ACR-22f2b56e662d481faf43d3f846c00e96
ACR-2233a85f04294b12b375b4165531f4c5
ACR-7faa6e7203cb496bb767bdcfac607fd7
ACR-1b553b58b5da4ae98cfa37a2dd2af957
ACR-08da6c35488346eca7428943188b08cc
ACR-f18ac63280a84d0c80efeac2f349973a
ACR-6129ffee7ac94081b58bedb33efc73b3
ACR-8f2aa1a46e80454ab8bc31108e7cbaf8
ACR-54ef4bdd26d9463d9f5c0d7da5c08e0b
ACR-6fe3d87381814318911fdcc4d98b4737
ACR-ef9f3e0ddb4a4df7b16ad0229d2fd4de
ACR-c850ad3fc1b7498fb9feb71b03cc9f7e
ACR-01409b3630964b2c8dc4c5906dd307cc
ACR-3bf1913e16ce48d4b3c2a56b63d82135
ACR-1aa5a14fb5d84681855c4eda97c713bd
 */
package org.sonarsource.sonarlint.core.rpc.impl;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseError;
import org.sonarsource.sonarlint.core.issue.IssueNotFoundException;
import org.sonarsource.sonarlint.core.issue.IssueService;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcErrorCode;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.AddIssueCommentParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.ChangeIssueStatusParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.CheckAnticipatedStatusChangeSupportedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.CheckAnticipatedStatusChangeSupportedResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.CheckStatusChangePermittedParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.CheckStatusChangePermittedResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.GetEffectiveIssueDetailsParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.GetEffectiveIssueDetailsResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.IssueRpcService;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.ReopenAllIssuesForFileParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.ReopenAllIssuesForFileResponse;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.ReopenIssueParams;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.issue.ReopenIssueResponse;
import org.sonarsource.sonarlint.core.rules.RuleNotFoundException;

public class IssueRpcServiceDelegate extends AbstractRpcServiceDelegate implements IssueRpcService {
  public IssueRpcServiceDelegate(SonarLintRpcServerImpl server) {
    super(server);
  }

  @Override
  public CompletableFuture<Void> changeStatus(ChangeIssueStatusParams params) {
    return runAsync(cancelMonitor -> getBean(IssueService.class).changeStatus(params.getConfigurationScopeId(), params.getIssueKey(), params.getNewStatus(), params.isTaintIssue(),
      cancelMonitor), params.getConfigurationScopeId());
  }

  @Override
  public CompletableFuture<Void> addComment(AddIssueCommentParams params) {
    return runAsync(cancelMonitor -> getBean(IssueService.class).addComment(params.getConfigurationScopeId(), params.getIssueKey(), params.getText(), cancelMonitor),
      params.getConfigurationScopeId());
  }

  @Override
  public CompletableFuture<CheckAnticipatedStatusChangeSupportedResponse> checkAnticipatedStatusChangeSupported(CheckAnticipatedStatusChangeSupportedParams params) {
    return requestAsync(cancelMonitor -> new CheckAnticipatedStatusChangeSupportedResponse(
      getBean(IssueService.class).checkAnticipatedStatusChangeSupported(params.getConfigScopeId())), params.getConfigScopeId());
  }

  @Override
  public CompletableFuture<CheckStatusChangePermittedResponse> checkStatusChangePermitted(CheckStatusChangePermittedParams params) {
    return requestAsync(cancelMonitor -> getBean(IssueService.class).checkStatusChangePermitted(params.getConnectionId(), params.getIssueKey(), cancelMonitor));
  }

  @Override
  public CompletableFuture<ReopenIssueResponse> reopenIssue(ReopenIssueParams params) {
    return requestAsync(
      cancelMonitor -> new ReopenIssueResponse(
        getBean(IssueService.class).reopenIssue(params.getConfigurationScopeId(), params.getIssueId(), params.isTaintIssue(), cancelMonitor)),
      params.getConfigurationScopeId());
  }

  @Override
  public CompletableFuture<ReopenAllIssuesForFileResponse> reopenAllIssuesForFile(ReopenAllIssuesForFileParams params) {
    return requestAsync(cancelMonitor -> new ReopenAllIssuesForFileResponse(getBean(IssueService.class).reopenAllIssuesForFile(params, cancelMonitor)),
      params.getConfigurationScopeId());
  }

  @Override
  public CompletableFuture<GetEffectiveIssueDetailsResponse> getEffectiveIssueDetails(GetEffectiveIssueDetailsParams params) {
    return requestAsync(cancelMonitor -> {
      try {
        return new GetEffectiveIssueDetailsResponse(getBean(IssueService.class)
          .getEffectiveIssueDetails(params.getConfigurationScopeId(), params.getIssueId(), cancelMonitor));
      } catch (IssueNotFoundException e) {
        var error = new ResponseError(SonarLintRpcErrorCode.ISSUE_NOT_FOUND, e.getMessage(), e.getIssueKey());
        throw new ResponseErrorException(error);
      } catch (RuleNotFoundException e) {
        var error = new ResponseError(SonarLintRpcErrorCode.RULE_NOT_FOUND, e.getMessage(), e.getRuleKey());
        throw new ResponseErrorException(error);
      }
    });
  }
}
