/*
ACR-ea24540ad7ea4d36a6e798ddd382ccdf
ACR-51a7bd0061d740059a1b722d1d05be07
ACR-11eb9c5b5c5545ddbe449596a74083d9
ACR-26e81d0646134cd3a4fa54bbfa1396ba
ACR-dd79dfbeb71744f7b7e47984bf97afa4
ACR-5a16fdf173884861bdbfd9dd74fb1682
ACR-c8b51f65ebb54324b5ed08d177879179
ACR-9f2d050e835c4effbd776df853f70a51
ACR-f30c6f8f40e74d6e8dad3eba80394c13
ACR-dd025226637f4aa6ba19e050fb5eb113
ACR-adad1efbf64c4999b4cd6b5a6f592719
ACR-1ef14d2eeaf54a95afac22e947c63b67
ACR-51703f84c52344bdb6004838d9920fb8
ACR-4127c0b1dbeb45139359b001a8a4fbd4
ACR-b77c2f60578b434990f3d1acf847681a
ACR-52e02acc0d444732893d27a2aa435f62
ACR-a9af3913e7634dc792b0cddace6458d3
 */
package org.sonarsource.sonarlint.core.analysis.command;

import org.sonarsource.sonarlint.core.analysis.api.ClientModuleFileEvent;
import org.sonarsource.sonarlint.core.analysis.container.global.ModuleRegistry;
import org.sonarsource.sonarlint.core.analysis.container.module.ModuleFileEventNotifier;

public class NotifyModuleEventCommand extends Command {
  private final String moduleKey;
  private final ClientModuleFileEvent event;

  public NotifyModuleEventCommand(String moduleKey, ClientModuleFileEvent event) {
    this.moduleKey = moduleKey;
    this.event = event;
  }

  @Override
  public void execute(ModuleRegistry moduleRegistry) {
    var moduleContainer = moduleRegistry.getContainerIfStarted(moduleKey);
    if (moduleContainer != null) {
      moduleContainer.getComponentByType(ModuleFileEventNotifier.class).fireModuleFileEvent(event);
    }
  }
}
