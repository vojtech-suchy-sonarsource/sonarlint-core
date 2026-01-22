/*
ACR-6e67a84893964e1395e5dcac0544020e
ACR-0748fa45ae0443dbb4b75a48aa6fd6e4
ACR-7b73d1b57fbb46c3bb22e49cd61455c8
ACR-089d4b2bd1e140b89e753be7a285dfcf
ACR-e7659cb0d035413d91ac6143d1f272a1
ACR-9f8a320ddedf446fa7f29588cc1881b9
ACR-1de628ebbc824d4ab3f4e945e6c964fe
ACR-6c02242003114e1d86ba21c20893eaa8
ACR-2f95ea58bfce4886b1682336de332f5b
ACR-6a114aae771b4d3283265a5b83ecfa79
ACR-ac11dd1f8a9d4cf584f933b4c1851cb5
ACR-789c16a735cb4b38bf805711392c66e8
ACR-c5eeb8a8f07d44e4a254ea96247107f6
ACR-266bf70bf8384ad29e7e69e633610b30
ACR-fedb59d78d124bd08b44d3238dfe0e0d
ACR-c1f13235761f4b199bdfaa8a8b3772b7
ACR-e730ce69176c45bfadae658ad5b8d8a4
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

/*ACR-6eb5c4f6c1db4818a99df30547e4256d
ACR-a3804fe178e641c79a4d40c763b146c1
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
    //ACR-dfd717fa5fac474a88b534bbd4ca35c9
    //ACR-402c87b65f9245dcae767e83eac2bdbc
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
        //ACR-827ba0c9efe047c4bdf231ba2bf28ef2
        //ACR-776108f3599543998abc0bdac8268b19
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
