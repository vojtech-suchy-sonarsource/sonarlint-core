/*
ACR-9451b931a13b43dd9b7cf6ce1a51e1e5
ACR-7ba338a6af724efa86a2888de17332f5
ACR-d30ebd9b12264e35b6fbc08a5f4cb37e
ACR-fe61f2b604e24077b8e6c6e1ac0cfe9e
ACR-71b0426fa1274ffd9e96c91605ce2320
ACR-f0e23e4f01524d06b7f09c8fc980616b
ACR-abc4f38d0a09425dbafaf20660afb867
ACR-d46adcd9a4dd470282c0d7a6805bc1cc
ACR-c452ea6021ee49e08ba5a0533a7ccb66
ACR-231d80940391466ab3fb211fd37603ff
ACR-eaa301bbb2bd4adf91012d84880bf0c7
ACR-5dbcf303a93940b59314648f63a4e6a7
ACR-da6daa1578704df9bc987fe00df9ff4e
ACR-564bfb3edfb946558b737b0c8175a75b
ACR-cc7d999b9467452eab6d28875f96ae14
ACR-9e6f1d1600b34297815e5fee928aa82c
ACR-c656999ede064860b68e5c92a620e6fe
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
