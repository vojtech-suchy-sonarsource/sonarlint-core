/*
ACR-7f17b2d9648d475394eb29bebb853737
ACR-d056f4c0cad34befac6da7bd8395258a
ACR-4d129e9d83dc4156a852a8c8e911b245
ACR-872eb22200c249faaf78194c326b454a
ACR-b8a15d557a214584a44add52a0cec037
ACR-2a39a40bf5fb4f39bf8b77fcc260dba5
ACR-5df60bfe26264c31864196371eedbb6c
ACR-4589fb4e3f84498f84561055bd771457
ACR-5f7edf5f23d2463e9991d48f2cc903b6
ACR-07bbd1328fe84e7c867d16cbfd3aa287
ACR-46d038426a7a48bb9558e52cfeef9d27
ACR-dfd220a78e79455c820c3f943ceff308
ACR-469bbe7f7f6340988ad786fee422cc61
ACR-2fffb318de72440fa70f7b6b9aa6d0d5
ACR-29d8116c97f74660b68d7726cef90b3a
ACR-4a67f6f42d0f482887201739f550e754
ACR-cb631722a6e34a73b7a62ac6765bb1da
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
