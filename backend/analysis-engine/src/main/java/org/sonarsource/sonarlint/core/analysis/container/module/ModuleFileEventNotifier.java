/*
ACR-f103591c29384f199a30da892524143c
ACR-dc50b5673f284f759cc7db073952416a
ACR-4511fb7a31454e7792ed0893b5e76f4b
ACR-502af107950843269c5b85d38b7741c0
ACR-e566b0635a874b1989be4d6591618f70
ACR-d4e8ee51609241029d8786a938c1cff1
ACR-9d8fc8e047a4447289d8bf19b3fdc9c2
ACR-c50197b91b4e4b8cb439a2fb29f5c98b
ACR-4d3d58f887bd463491169d95353ba16d
ACR-37d632e39a3740a8bd355617f1afc019
ACR-7024738070784d328426745bf79e0363
ACR-94500f1594e34105a76b72a3c92e6bc4
ACR-cc721ff5d3e0404c99e985280796ff96
ACR-7f37c75a49cb463a98c2d7ac994ffd1a
ACR-d7808da66d5e45849c1023ea4a8fd4a4
ACR-c5f825738b1e4b738393c2d0d8084ba8
ACR-3f0c741c352346bba8d30c1b0a14bf5d
 */
package org.sonarsource.sonarlint.core.analysis.container.module;

import java.util.List;
import java.util.Optional;
import org.sonarsource.sonarlint.core.analysis.api.ClientModuleFileEvent;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.plugin.api.module.file.ModuleFileEvent;
import org.sonarsource.sonarlint.plugin.api.module.file.ModuleFileListener;

public class ModuleFileEventNotifier {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final List<ModuleFileListener> listeners;
  private final ModuleInputFileBuilder inputFileBuilder;

  public ModuleFileEventNotifier(Optional<List<ModuleFileListener>> listeners, ModuleInputFileBuilder inputFileBuilder) {
    this.listeners = listeners.orElse(List.of());
    this.inputFileBuilder = inputFileBuilder;
  }

  public void fireModuleFileEvent(ClientModuleFileEvent event) {
    ModuleFileEvent apiEvent = DefaultModuleFileEvent.of(inputFileBuilder.create(event.target()), event.type());
    listeners.forEach(l -> tryFireModuleFileEvent(l, apiEvent));
  }

  private static void tryFireModuleFileEvent(ModuleFileListener listener, ModuleFileEvent event) {
    try {
      listener.process(event);
    } catch (Exception e) {
      LOG.error("Error processing file event", e);
    }
  }
}
