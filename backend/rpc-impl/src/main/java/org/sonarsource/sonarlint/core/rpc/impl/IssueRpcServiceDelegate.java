/*
ACR-4fa1ed61757644119902811ce639b6f5
ACR-91aeb30ed4894dd7811e735d08070f46
ACR-780eca192fc14030a88eb4414d4fb7db
ACR-6931a2e94cf24c0fb6005c7c6d324640
ACR-63a629edbced4c67956a988ab9da1da2
ACR-246f25f97e7b4bcf9bd177759e74777e
ACR-e2440fb83c23412d8991c724e4b771ce
ACR-13584b89ac5d40b2be654e756488d7fd
ACR-eea4ec997c074f76ac7932613958277d
ACR-a7b316e2d4b644a78272feeebe352224
ACR-a542f2c7ec7d438da770c84f936b7c47
ACR-36698cdcada4422bbff3e62a7ec0b42a
ACR-6ddc0fd74ded46f2b370a5d3f7567ec0
ACR-8b5dfc6e16064e0ba75f80f1b25bd3de
ACR-6360f9d9e7954e24addc3e0e33c6998a
ACR-0242ce49844d4ff09057482efeefe358
ACR-cdea49443d8148fca204e3432ab5af87
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
