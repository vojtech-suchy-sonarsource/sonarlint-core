/*
ACR-89d773c99f2c473fb7176cf039a888f8
ACR-32ea1f77bd114aa8bdcdbc8074d5e3cf
ACR-f4379a5b19134637b9acad0f92907a81
ACR-9378e7fd6da9465394470137eca95d58
ACR-00cacb32852842889d0becc7edc01ff8
ACR-92eb7b2f6c67418cb2d5d3678847c10b
ACR-afaa4dbeb2e849d2a6e31e157697bb37
ACR-bcecaf2f32704dc0ab6bc175d26a75f1
ACR-05a8c5784a374ddf87327b5a45f5b8e2
ACR-dee7d4dd86df481298da32abfe246f6a
ACR-8042904fcde143a7a9f9f695488a1fc9
ACR-efa6cb9fb0854f8cb37b28d9411e2198
ACR-8efe34f5da8745128a1b714b877fb5b7
ACR-7ced9195368542c98c77548c040d0384
ACR-0d86d0e028ff419badf3292bf5c3d812
ACR-c2f7c1115155460fa995ef9a91c96fd6
ACR-af4ca12667b040d880b7d1b1a4e6fbd0
 */
package org.sonarsource.sonarlint.core.analysis;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.SystemUtils;
import org.sonarsource.sonarlint.core.commons.Version;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.nodejs.InstalledNodeJs;
import org.sonarsource.sonarlint.core.nodejs.NodeJsHelper;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Language;
import org.springframework.context.ApplicationEventPublisher;

import static org.sonarsource.sonarlint.core.commons.api.SonarLanguage.Constants.JAVASCRIPT_PLUGIN_KEY;

/*ACR-a7533ce214c04281b35571c68c82d7b8
ACR-0b4d57c659304232bc1f38e9883326a2
 */
public class NodeJsService {

  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private final ApplicationEventPublisher eventPublisher;
  private final boolean isNodeJsNeeded;
  private volatile boolean nodeAutoDetected;
  @Nullable
  private InstalledNodeJs autoDetectedNodeJs;
  @Nullable
  private Path clientNodeJsPath;
  private boolean clientForcedNodeJsDetected;
  @Nullable
  private InstalledNodeJs clientForcedNodeJs;

  public NodeJsService(InitializeParams initializeParams, ApplicationEventPublisher eventPublisher) {
    var languageSpecificRequirements = initializeParams.getLanguageSpecificRequirements();
    this.clientNodeJsPath = languageSpecificRequirements == null || languageSpecificRequirements.getJsTsRequirements() == null ? null
      : languageSpecificRequirements.getJsTsRequirements().getClientNodeJsPath();
    this.isNodeJsNeeded = isNodeJsNeeded(initializeParams);
    this.eventPublisher = eventPublisher;
  }

  private static boolean isNodeJsNeeded(InitializeParams initializeParams) {
    //ACR-8737c861d96841ee92794ab1a54d98f4
    //ACR-3fbeb11638cb4274a005119191bd1b32
    var languagesNeedingNodeJsInSonarJs = SonarLanguage.getLanguagesByPluginKey(JAVASCRIPT_PLUGIN_KEY).stream().map(l -> Language.valueOf(l.name())).collect(Collectors.toSet());
    return !Collections.disjoint(initializeParams.getEnabledLanguagesInStandaloneMode(), languagesNeedingNodeJsInSonarJs)
      || !Collections.disjoint(initializeParams.getExtraEnabledLanguagesInConnectedMode(), languagesNeedingNodeJsInSonarJs);
  }

  @CheckForNull
  public synchronized InstalledNodeJs didChangeClientNodeJsPath(@Nullable Path clientNodeJsPath) {
    if (!Objects.equals(this.clientNodeJsPath, clientNodeJsPath)) {
      this.clientNodeJsPath = clientNodeJsPath;
      this.clientForcedNodeJsDetected = false;
      this.eventPublisher.publishEvent(new ClientNodeJsPathChanged());
    }
    var forcedNodeJs = getClientForcedNodeJs();
    return forcedNodeJs == null ? null : new InstalledNodeJs(forcedNodeJs.getPath(), forcedNodeJs.getVersion());
  }

  @CheckForNull
  public synchronized InstalledNodeJs getActiveNodeJs() {
    return clientNodeJsPath == null ? getAutoDetectedNodeJs() : getClientForcedNodeJs();
  }

  public synchronized Optional<Version> getActiveNodeJsVersion() {
    return Optional.ofNullable(getActiveNodeJs()).map(InstalledNodeJs::getVersion);
  }

  @CheckForNull
  public InstalledNodeJs getAutoDetectedNodeJs() {
    if (!nodeAutoDetected) {
      if (!isNodeJsNeeded) {
        LOG.debug("Skip Node.js auto-detection as no plugins require it");
        nodeAutoDetected = true;
        return null;
      }
      var helper = new NodeJsHelper();
      autoDetectedNodeJs = helper.autoDetect();
      nodeAutoDetected = true;
      logAutoDetectionResults(autoDetectedNodeJs);
    }
    return autoDetectedNodeJs;
  }

  @CheckForNull
  private InstalledNodeJs getClientForcedNodeJs() {
    if (!clientForcedNodeJsDetected) {
      var helper = new NodeJsHelper();
      clientForcedNodeJs = helper.detect(clientNodeJsPath);
      clientForcedNodeJsDetected = true;
      logClientForcedDetectionResults(clientForcedNodeJs);
    }
    return clientForcedNodeJs;
  }

  private static void logAutoDetectionResults(@Nullable InstalledNodeJs autoDetectedNode) {
    if (autoDetectedNode != null) {
      LOG.debug("Auto-detected Node.js path set to: {} (version {})", autoDetectedNode.getPath(), autoDetectedNode.getVersion());
    } else {
      LOG.warn(
        "Node.js could not be automatically detected, has to be configured manually in the SonarLint preferences!");

      if (SystemUtils.IS_OS_MAC_OSX) {
        //ACR-5390ab3319494247b09ed3f86d0b9ac7
        //ACR-c86731ae5a8d43afa99a3040bd7f84f5
        LOG.warn(
          "Automatic detection does not work on macOS when added to PATH from user shell configuration (e.g. Bash)");
      }
    }
  }

  private static void logClientForcedDetectionResults(@Nullable InstalledNodeJs detectedNode) {
    if (detectedNode != null) {
      LOG.debug("Node.js path set to: {} (version {})", detectedNode.getPath(), detectedNode.getVersion());
    } else {
      LOG.warn(
        "Configured Node.js could not be detected, please check your configuration in the SonarLint settings");
    }
  }
}
